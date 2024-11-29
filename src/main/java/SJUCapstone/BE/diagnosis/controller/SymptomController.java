package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Analysis;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.model.Symptom;
import SJUCapstone.BE.diagnosis.repository.AnalysisRepository;
import SJUCapstone.BE.diagnosis.service.DiagnosisService;
import SJUCapstone.BE.diagnosis.service.ImageAnalysisService;
import SJUCapstone.BE.diagnosis.service.SymptomsService;
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
    //    @Autowired
//    UserInfoService userInfoService;
    @Autowired
    S3ImageService s3ImageService;
    @Autowired
    ImageAnalysisService imageAnalysisService;
    @Autowired
    AnalysisRepository analysisRepository;
    @Autowired
    SymptomsService symptomsService;
    @Autowired
    DiagnosisService diagnosisService;

//    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadAndDiagnoseImages(@RequestParam(name = "file") List<MultipartFile> images, HttpServletRequest request) {
//        try {
//            // Step 1: 이미지 판별 및 사용자 ID 추출
//            boolean allMouthImages = imageAnalysisService.areMouthImages(images);
//            if (!allMouthImages) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errorCode", "INVALID_IMAGE_TYPE", "message", "One or more uploaded images are not valid oral images."));
//            }
//
//            Long userId = authService.getUserId(request);
//            System.out.println("userId = " + userId);
//
//            // Step 2: 이미지 업로드 및 분석
//            Map<String, Object> combinedResults = imageAnalysisService.uploadAndAnalyzeImages(images, userId);
//
//            // Step 3: 결과를 세션에 저장
//            List<String> imageUrls = (List<String>) combinedResults.get("analyzedImageUrls");
//            request.getSession().setAttribute("uploadedImageUrls", imageUrls);
//
//            // Step 4: 결과를 데이터베이스에 저장
//            imageAnalysisService.saveAnalysisResult(userId, combinedResults);
//
//            // Step 5: 결과 반환
//            return ResponseEntity.ok(combinedResults);
//
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image processing failed: " + e.getMessage());
//        }
//    }

    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndDiagnoseSingleImage(@RequestParam(name = "file") MultipartFile image, HttpServletRequest request) {
        try {
            // Step 1: 이미지 판별 및 사용자 ID 추출
            boolean isMouthImage = imageAnalysisService.isMouthImage(image);
            if (!isMouthImage) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errorCode", "INVALID_IMAGE_TYPE", "message", "The uploaded image is not a valid oral image."));
            }

            Long userId = authService.getUserId(request);
            System.out.println("userId = " + userId);

            // Step 2: 이미지 업로드 및 분석
            Map<String, Object> analysisResult = imageAnalysisService.uploadAndAnalyzeImage(image, userId);

            // Step 3: 결과를 데이터베이스에 저장
            imageAnalysisService.saveAnalysisResult(userId, analysisResult);

            // Step 4: 결과 반환
            return ResponseEntity.ok(analysisResult);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image processing failed: " + e.getMessage());
        }
    }

    //    @Operation(summary = "추가 진단 입력할 때 사용할 API(완성", description = "챗봇 api 다 완성되면 수정 예정임!!!!아직 미완!!!")
    @PostMapping("/submit")
    public ResponseEntity<?> submitSymptom(
            @RequestParam("painLevel") Long painLevel,
            @RequestParam("symptomText") List<String> symptomText,
            @RequestParam("symptomArea") List<String> symptomArea,
            HttpServletRequest request) {
        try {
            // 사용자 ID 및 이름 가져오기
            Long userId = authService.getUserId(request);
            String userName = authService.getUserName(request);
            System.out.println("userId = " + userId);

            // Symptom 생성 및 저장
            Symptom symptom = symptomsService.createSymptom(userId, userName, painLevel, symptomText, symptomArea);

            // 진단 정보 조회 (완료되지 않은 진단만)
            Analysis existingDiagnosis = analysisRepository.findByUserIdAndIsComplete(userId, false);
            if (existingDiagnosis == null) {
                return ResponseEntity.badRequest().body("No diagnosis found for user. Please upload images first.");
            }

            // 기존 진단 정보와 합쳐서 반환할 결과 생성
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.put("tooth_diseases", existingDiagnosis.getToothDiseases());
            combinedResults.put("gum_diseases", existingDiagnosis.getGumDiseases());
            combinedResults.put("painLevel", painLevel);
            combinedResults.put("symptomText", symptomText);
            combinedResults.put("symptomArea", symptomArea);

            // 모델 서버로 combinedResults 전송
            String responseFromModelServer = imageAnalysisService.sendCombinedResultsToModelServer(combinedResults);

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

                    Float dangerPoint = imageAnalysisService.getDetectionPoint(painLevel.intValue());
                    System.out.println("dangerPoint = " + dangerPoint);
                    diagnosis.setDangerPoint(Math.round(dangerPoint));
                    diagnosisService.createDiagnoses(diagnosis, userId);

                    // 진단 완료 상태로 변경
                    imageAnalysisService.completeAnalysis(userId);

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
            Analysis analysis = analysisRepository.findByUserId(userId);
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
