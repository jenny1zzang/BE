package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Analysis;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.repository.AnalysisRepository;
import SJUCapstone.BE.diagnosis.service.DiagnosisService;
import SJUCapstone.BE.diagnosis.service.AnalysisService;
import SJUCapstone.BE.image.S3ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/symptom")
public class SymptomController {
    @Autowired
    AuthService authService;
    @Autowired
    S3ImageService s3ImageService;
    @Autowired
    AnalysisService analysisService;
    @Autowired
    AnalysisRepository analysisRepository;
    @Autowired
    DiagnosisService diagnosisService;

    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam(name = "file") MultipartFile image) {
        try {
            boolean isMouthImage = analysisService.isMouthImage(image);
            if (!isMouthImage) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errorCode", "INVALID_IMAGE_TYPE", "message", "The uploaded image is not a valid oral image."));
            }
            return ResponseEntity.ok(Map.of("message", "All images are valid oral images"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("errorCode", "SERVER_ERROR", "message", "An error occurred while processing the image."));
        }
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitSymptom(
            @RequestParam("painLevel") Long painLevel,
            @RequestParam("symptomText") List<String> symptomText,
            @RequestParam("symptomArea") List<String> symptomArea,
            @RequestParam("file") List<MultipartFile> images,
            HttpServletRequest request) {
        try {
            // 사용자 ID 및 이름 가져오기
            Long userId = authService.getUserId(request);
            System.out.println("userId = " + userId);


            for (int i = 0; i < images.size(); i++) {

                MultipartFile image = images.get(i);

                Map<String, Object> analysisResult = analysisService.uploadAndAnalyzeImage(image, userId, painLevel, symptomText, symptomArea);
                if (analysisResult == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send data to model server.");
                }
                System.out.println("analysisResult = " + analysisResult);

                // Step 4: 결과를 데이터베이스에 저장 (기존 분석에 추가)
                analysisService.saveAnalysisResult(userId, analysisResult);
            }

            // 진단 정보 조회 (완료되지 않은 진단만)
            Analysis existingDiagnosis = analysisRepository.findByUserIdAndIsComplete(userId, false);
            if (existingDiagnosis == null) {
                return ResponseEntity.badRequest().body("No diagnosis found for user. Please upload images first.");
            }

            // 기존 진단 정보와 합쳐서 반환할 결과 생성
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.put("tooth_diseases", existingDiagnosis.getToothDiseases());
            combinedResults.put("gum_diseases", existingDiagnosis.getGumDiseases());
            combinedResults.put("etc_diseases", existingDiagnosis.getEtcDiseases());
            combinedResults.put("painLevel", painLevel);
            combinedResults.put("symptomText", symptomText);
            combinedResults.put("symptomArea", symptomArea);

            // 모델 서버로 combinedResults 전송
            String responseFromModelServer = analysisService.sendCombinedResultsToModelServer(combinedResults);

            if (responseFromModelServer != null) {
                // 서버 응답 파싱 및 데이터베이스 저장
                Map<String, Object> responseMap = parseResponse(responseFromModelServer);
                if (responseMap != null) {
                    Diagnosis diagnosis = new Diagnosis();
                    diagnosis.setDetectedDiseases(parsingDisease(existingDiagnosis)); // Analysis 파싱해서 질병 이름 뽑기
                    diagnosis.setResult((String) responseMap.get("result"));
                    diagnosis.setDetailed_result((String) responseMap.get("detailed_result"));
                    diagnosis.setCare_method((String) responseMap.get("care_method"));
                    diagnosis.setAnalyzedImageUrls(existingDiagnosis.getAnalyzedImageUrls());

                    Float dangerPoint = analysisService.getDetectionPoint(painLevel.intValue());
                    System.out.println("dangerPoint = " + dangerPoint);
                    diagnosis.setDangerPoint(Math.round(dangerPoint));
                    diagnosisService.createDiagnoses(diagnosis, userId);

                    // 진단 완료 상태로 변경
                    analysisService.completeAnalysis(userId);

                    List<Diagnosis> diagnosisList = diagnosisService.getDiagnosesByUserId(userId);
                    int diagnosisIndex = diagnosisList.size();
                    return ResponseEntity.ok(Map.of("diagnosisId", diagnosisIndex));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse response from model server.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send data to model server.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save symptom: " + e.getMessage());
        }
    }

    private List<String> parsingDisease(Analysis existingDiagnosis) {
        Map<String, Object> toothDiseases = existingDiagnosis.getToothDiseases();
        Map<String, Object> gumDiseases = existingDiagnosis.getGumDiseases();


        // 영어 -> 한글 매핑
        Map<String, String> diseaseNameMapping = new HashMap<>();
        diseaseNameMapping.put("Calculus", "치석");
        diseaseNameMapping.put("Caries", "충치");
        diseaseNameMapping.put("Cas", "칸디다증");
        diseaseNameMapping.put("CoS", "구순포진");
        diseaseNameMapping.put("Gingivitis", "치은염");
        diseaseNameMapping.put("GUM", "치주염");
        diseaseNameMapping.put("Hypodontia", "치아 결손증");
        diseaseNameMapping.put("MC", "구강암");
        diseaseNameMapping.put("MouthUI", "구강 궤양");
        diseaseNameMapping.put("OLP", "구강 편평태선");
        diseaseNameMapping.put("Tooth Discoloration", "치아변색");

        List<String> combinedTranslatedDiseaseNames = Stream.concat(
                extractAndTranslateDiseaseNames(toothDiseases, diseaseNameMapping).stream(),
                extractAndTranslateDiseaseNames(gumDiseases, diseaseNameMapping).stream()
        ).distinct().collect(Collectors.toList());

        return combinedTranslatedDiseaseNames;
    }

    private static List<String> extractAndTranslateDiseaseNames(Map<String, Object> diseases, Map<String, String> mapping) {
        return diseases.values().stream()
                .flatMap(value -> ((List<Map<String, Object>>) value).stream())
                .map(disease -> (String) disease.get("disease_name")) // 영어 이름 추출
                .distinct()
                .map(mapping::get) // 한글로 변환
                .filter(Objects::nonNull) // 매핑이 없는 경우 제외
                .collect(Collectors.toList());
    }

    @GetMapping("/toothDiseases")
    public ResponseEntity<?> getToothDiseasesFromAnalysis(HttpServletRequest request) {
        try {
            Long userId = authService.getUserId(request);
            Analysis analysis = analysisRepository.findTopByUserIdAndIsCompleteOrderByAnalysisIdDesc(userId, true);
            if (analysis == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No analysis found for user.");
            }
            return ResponseEntity.ok(analysis.getToothDiseases());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    private Map<String, Object> parseResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
            return null;
        }
    }
}
