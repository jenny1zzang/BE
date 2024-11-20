package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
            // 모델 서버로 다중 이미지 판별 요청
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

            // S3에 이미지 업로드 (각각의 이미지를 업로드하고 URL 리스트를 생성)
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageUrl = s3ImageService.upload(image);
                imageUrls.add(imageUrl);
            }

            // 모델 서버로 다중 이미지 진단 요청 (이미지를 전송하고 응답으로 이미지 수신)
            List<byte[]> analyzedImages = imageAnalysisService.sendImagesForDetectionAndReceiveImages(images);
            if (analyzedImages == null || analyzedImages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get analyzed images from model server.");
            }

            List<String> analyzedImageUrls = new ArrayList<>();
            try {
                int index = 0;
                for (byte[] analyzedImage : analyzedImages) {
                    String originalFileName = "analyzed_image_" + index + ".jpg";
                    String analyzedImageUrl = s3ImageService.uploadByteImage(analyzedImage, originalFileName);
                    analyzedImageUrls.add(analyzedImageUrl);
                    index++;
                }
            } catch (Exception e) {
                // 업로드 중 예외 처리
                throw new RuntimeException("Failed to upload analyzed images", e);
            }

            // 모델 서버로부터 진단 결과 받기 (JSON 데이터 수신)
            List<Map<String, Object>> detectionResult = imageAnalysisService.getDetectionResults(analyzedImages);
            if (detectionResult == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get detection result from model server.");
            }

            // 세션에 이미지 URL 저장
            request.getSession().setAttribute("uploadedImageUrls", imageUrls);

            // 결과와 분석된 이미지 반환 (이미지를 URL로 변환하여 반환)
            return ResponseEntity.ok(Map.of(
                    "message", "All images are valid oral images",
                    "analyzedImages", analyzedImageUrls,
                    "detectionResult", detectionResult
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
