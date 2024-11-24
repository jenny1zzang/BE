package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.WebMvcConfig;
import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Analysis;
import SJUCapstone.BE.diagnosis.model.AnalysisResult;
import SJUCapstone.BE.diagnosis.model.Symptom;
import SJUCapstone.BE.diagnosis.repository.AnalysisRepository;
import SJUCapstone.BE.diagnosis.service.ImageAnalysisService;
import SJUCapstone.BE.diagnosis.service.SymptomsService;
import SJUCapstone.BE.image.S3ImageService;
import SJUCapstone.BE.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/symptom")
public class SymptomController {
    @Autowired
    AuthService authService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    S3ImageService s3ImageService;
    @Autowired
    ImageAnalysisService imageAnalysisService;
    @Autowired
    AnalysisRepository analysisRepository;
    @Autowired
    SymptomsService symptomsService;

    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndDiagnoseImages(@RequestParam(name = "file") List<MultipartFile> images, HttpServletRequest request) {
        try {
            // Step 1: 이미지 판별 및 사용자 ID 추출
            boolean allMouthImages = imageAnalysisService.areMouthImages(images);
            if (!allMouthImages) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errorCode", "INVALID_IMAGE_TYPE", "message", "One or more uploaded images are not valid oral images."));
            }

            Long userId = authService.getUserId(request);
            System.out.println("userId = " + userId);

            // Step 2: 이미지 업로드 및 분석
            Map<String, Object> combinedResults = imageAnalysisService.uploadAndAnalyzeImages(images, userId);

            // Step 3: 결과를 세션에 저장
            List<String> imageUrls = (List<String>) combinedResults.get("analyzedImageUrls");
            request.getSession().setAttribute("uploadedImageUrls", imageUrls);

            // Step 4: 결과를 데이터베이스에 저장
            imageAnalysisService.saveAnalysisResult(userId, combinedResults);

            // Step 5: 결과 반환
            return ResponseEntity.ok(combinedResults);

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
            List<String> imageUrls = (List<String>) request.getSession().getAttribute("uploadedImageUrls");
            if (imageUrls == null) {
                return ResponseEntity.badRequest().body("No images were uploaded before symptom submission.");
            }
            // 사용자 ID 및 이름 가져오기
            Long userId = authService.getUserId(request);
            String userName = authService.getUserName(request);

            // Symptom 생성 및 저장
            Symptom symptom = symptomsService.createSymptom(userId, userName, painLevel, symptomText, symptomArea, imageUrls);

            Analysis existingDiagnosis = analysisRepository.findByUserId(userId);
            if (existingDiagnosis == null) {
                return ResponseEntity.badRequest().body("No diagnosis found for user. Please upload images first.");
            }

            // 기존 진단 정보와 합쳐서 반환할 결과 생성
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.put("tooth_diseases", existingDiagnosis.getToothDiseases());
            combinedResults.put("gum_diseases", existingDiagnosis.getGumDiseases());
//            combinedResults.put("analyzedImageUrls", existingDiagnosis.getAnalyzedImageUrls());
            combinedResults.put("painLevel", painLevel);
            combinedResults.put("symptomText", symptomText);
            combinedResults.put("symptomArea", symptomArea);

            // 모델 서버로 combinedResults 전송
            String responseFromModelServer = imageAnalysisService.sendCombinedResultsToModelServer(combinedResults);

            if (responseFromModelServer != null) {
                return ResponseEntity.ok(responseFromModelServer);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send data to model server.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save symptom: " + e.getMessage());
        }
    }
}
