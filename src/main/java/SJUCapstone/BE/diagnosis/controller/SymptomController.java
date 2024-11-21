package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.model.ImageAnalysisResult;
import SJUCapstone.BE.diagnosis.model.Symptom;
import SJUCapstone.BE.diagnosis.repository.SymptomRepository;
import SJUCapstone.BE.diagnosis.service.ImageAnalysisService;
import SJUCapstone.BE.diagnosis.service.SymptomsService;
import SJUCapstone.BE.image.S3ImageService;
import SJUCapstone.BE.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/symptom")
public class SymptomController {

    private final SymptomsService symptomsService;

    @Autowired
    public SymptomController(SymptomsService symptomsService) {
        this.symptomsService = symptomsService;}
    @Autowired
    AuthService authService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    S3ImageService s3ImageService;
    @Autowired
    ImageAnalysisService imageAnalysisService;

    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndDiagnoseImages(@RequestParam(name = "file") List<MultipartFile> images, HttpServletRequest request) {
        try {
            // Step 1: 모델 서버로 다중 이미지 판별 요청
            boolean allMouthImages;
            try {
                allMouthImages = imageAnalysisService.areMouthImages(images);
            } catch (Exception ex) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("errorCode", "IMAGE_ANALYSIS_ERROR", "message", "Failed to analyze images."));
            }

            if (!allMouthImages) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "errorCode", "INVALID_IMAGE_TYPE",
                                "message", "One or more uploaded images are not valid oral images."
                        ));
            }

            // Step 2: S3에 원본 이미지 업로드
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageUrl = s3ImageService.upload(image);
                imageUrls.add(imageUrl);
            }

            // Step 3: 모델 서버로 다중 이미지 진단 요청 (분석된 이미지와 결과를 모두 반환)
            List<ImageAnalysisResult> analysisResults;
            try {
                analysisResults = imageAnalysisService.processImagesAndGetDetectionResults(images);
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process images and get detection results.");
            }

            // Step 4: 분석된 이미지를 S3에 업로드하고 URL 생성
            List<Map<String, Object>> responseResults = new ArrayList<>();
            int index = 0;

            for (ImageAnalysisResult analysisResult : analysisResults) {
                try {
                    // 분석된 이미지 업로드
                    byte[] analyzedImage = analysisResult.getAnalyzedImage();
                    String originalFileName = "analyzed_image_" + index + ".jpg";
                    String analyzedImageUrl = s3ImageService.uploadByteImage(analyzedImage, originalFileName);

                    // 분석된 이미지 URL과 결과를 저장
                    Map<String, Object> result = new HashMap<>();
                    result.put("analyzedImageUrl", analyzedImageUrl);
                    result.put("detectionResult", analysisResult.getDetectionResult());
                    responseResults.add(result);
                    index++;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload analyzed images to S3", e);
                }
            }

            // Step 5: 세션에 원본 이미지 URL 저장
            request.getSession().setAttribute("uploadedImageUrls", imageUrls);

            // Step 6: 결과 반환 (원본 이미지 URL, 분석된 이미지 URL 및 결과)
            return ResponseEntity.ok(Map.of(
                    "message", "All images are valid oral images",
                    "analysisResults", responseResults
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image processing failed: " + e.getMessage());
        }
    }

    @Operation(summary = "추가 진단 입력할 때 사용할 API(미완)", description = "챗봇 api 다 완성되면 수정 예정임!!!!아직 미완!!!")
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

//            //삭제하는게 좋을지????
//            request.getSession().removeAttribute("uploadedImageUrls");
            return ResponseEntity.ok(symptom);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save symptom: " + e.getMessage());
        }
    }
}
