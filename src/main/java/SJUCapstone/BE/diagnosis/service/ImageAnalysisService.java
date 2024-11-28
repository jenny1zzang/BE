package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Analysis;
import SJUCapstone.BE.diagnosis.model.AnalysisResult;
import SJUCapstone.BE.diagnosis.repository.AnalysisRepository;
import SJUCapstone.BE.image.S3ImageService;
import SJUCapstone.BE.user.service.UserInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ImageAnalysisService {

    private static final String MODEL_SERVER_BASE_URL = "http://222.109.26.240:8000";

    private final RestTemplate restTemplate;
    @Autowired
    public ImageAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    AuthService authService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    S3ImageService s3ImageService;
    @Autowired
    AnalysisRepository analysisRepository;

    /**
     * Check if all images are valid oral images.
     */
    public boolean isMouthImage(MultipartFile image) {
        String url = MODEL_SERVER_BASE_URL + "/is_mouth/";

        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(image);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return responseBody != null && Boolean.TRUE.equals(responseBody.get("is_mouth_image"));
            } else {
                System.err.println("Unexpected status code from is_mouth API: " + response.getStatusCode());
            }
        } catch (IOException e) {
            System.err.println("Error reading image file: " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("Error calling is_mouth API: " + e.getMessage());
        }

        return false;
    }

    /**
     * Process images and get detection results from the model server.
     */
//    public List<AnalysisResult> processImagesAndGetDetectionResults(List<MultipartFile> images) {
//        List<AnalysisResult> results = new ArrayList<>();
//
//        for (MultipartFile image : images) {
//            try {
//                // Step 1: Detect and get analyzed image
//                byte[] analyzedImage = detectImage(image);
//
//                if (analyzedImage != null) {
//                    // Step 2: Get detection results for the analyzed image
//                    Map<String, Object> detectionResult = getDetectionResult(analyzedImage);
//
//                    if (detectionResult != null) {
//                        // Create ImageAnalysisResult object
//                        AnalysisResult analysisResult = new AnalysisResult();
//                        analysisResult.setAnalyzedImage(analyzedImage);
//                        analysisResult.setDetectionResult(detectionResult);
//                        results.add(analysisResult);
//                    }
//                }
//            } catch (IOException e) {
//                System.err.println("Error reading image file: " + e.getMessage());
//            } catch (RestClientException e) {
//                System.err.println("Error processing image: " + e.getMessage());
//            }
//        }
//
//        return results;
//    }
    public AnalysisResult processImageAndGetDetectionResult(MultipartFile image) {
        try {
            // Step 1: Detect and get analyzed image
            byte[] analyzedImage = detectImage(image);

            if (analyzedImage != null) {
                // Step 2: Get detection results for the analyzed image
                Map<String, Object> detectionResult = getDetectionResult(analyzedImage);

                if (detectionResult != null) {
                    // Create ImageAnalysisResult object
                    AnalysisResult analysisResult = new AnalysisResult();
                    analysisResult.setAnalyzedImage(analyzedImage);
                    analysisResult.setDetectionResult(detectionResult);
                    return analysisResult;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading image file: " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }

        return null;
    }

    /**
     * Detect an image and return the analyzed image as a byte array.
     */
//    private byte[] detectImage(MultipartFile image) throws IOException {
//        String url = MODEL_SERVER_BASE_URL + "/detect/";
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(List.of(image));
//
//        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);
//
//        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//            return response.getBody();
//        }
//
//        System.err.println("Failed to detect image: " + response.getStatusCode());
//        return null;
//    }
    private byte[] detectImage(MultipartFile image) throws IOException {
        String url = MODEL_SERVER_BASE_URL + "/detect/";
        HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(image);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }

        System.err.println("Failed to detect image: " + response.getStatusCode());
        return null;
    }

    /**
     * Get detection results for the analyzed image.
     */
    private Map<String, Object> getDetectionResult(byte[] analyzedImage) {
        String url = MODEL_SERVER_BASE_URL + "/detection_result/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(analyzedImage, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }

        System.err.println("Failed to get detection result: " + response.getStatusCode());
        return null;
    }

    public Float getDetectionPoint(int painLevel) {
        String url = MODEL_SERVER_BASE_URL + "/danger_point/?pain_level=" + painLevel;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            // 디버깅: 요청 URL 및 헤더 출력
            System.out.println("Sending request to URL: " + url);
            System.out.println("Request Headers: " + headers);

            // POST 요청 수행
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            // 디버깅: 응답 상태 코드 출력
            System.out.println("Response Status Code: " + response.getStatusCode());

            // 응답 바디 확인
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // 디버깅: 응답 바디 출력
                System.out.println("Response Body: " + responseBody);

                // "danger_point" 키가 있는지 확인
                if (responseBody.containsKey("danger_point")) {
                    Object dangerPointValue = responseBody.get("danger_point");

                    // 디버깅: "danger_point" 값 확인
                    System.out.println("Danger Point Value: " + dangerPointValue);

                    // 값이 Number인지 확인 후 변환
                    if (dangerPointValue instanceof Number) {
                        Float dangerPoint = ((Number) dangerPointValue).floatValue();
                        System.out.println("Extracted Danger Point: " + dangerPoint);
                        return dangerPoint;
                    } else {
                        System.err.println("Unexpected type for danger_point: " + dangerPointValue.getClass().getName());
                    }
                } else {
                    System.err.println("Response does not contain 'danger_point' key.");
                }
            } else {
                System.err.println("Unexpected response status or empty body: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Failed to get detection result: " + e.getMessage());
        }

        // 기본값 반환
        System.out.println("Returning default danger point: 0.0");
        return 0.0F;
    }


    /**
     * Create a multipart request entity for a list of images.
     */
//    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequest(List<MultipartFile> images) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        for (MultipartFile image : images) {
//            Resource fileResource = new ByteArrayResource(image.getBytes()) {
//                @Override
//                public String getFilename() {
//                    return image.getOriginalFilename();
//                }
//            };
//            body.add("file", fileResource);
//        }
//
//        return new HttpEntity<>(body, headers);
//    }
    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequest(MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
        body.add("file", fileResource);

        return new HttpEntity<>(body, headers);
    }

    /**
     * Send combined results to the model server.
     */
    public String sendCombinedResultsToModelServer(Map<String, Object> combinedResults) {
        String url = MODEL_SERVER_BASE_URL + "/result_report/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(combinedResults); // 배열로 감싸지 않음
        } catch (JsonProcessingException e) {
            System.err.println("Error converting combinedResults to JSON: " + e.getMessage());
            return null;
        }

        // 디버깅: 변환된 JSON 출력
        System.out.println("combinedResults as JSON = " + jsonString);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

        try {
            // POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Successfully sent data to the model server. Response: " + response.getBody());
                return response.getBody();
            } else {
                System.err.println("Failed to send combined results to model server: HTTP " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.err.println("Error calling submit_results API: " + e.getMessage());
        }

        return null;
    }

    /**
     * Upload and analyze images, and return the combined results.
     */
    public Map<String, Object> uploadAndAnalyzeImage(MultipartFile image, Long userId) throws IOException {
        String imageUrl = s3ImageService.upload(image);

        AnalysisResult analysisResult = processImageAndGetDetectionResult(image);

        if (analysisResult == null) {
            throw new IOException("Image processing failed");
        }

        byte[] analyzedImage = analysisResult.getAnalyzedImage();
        String originalFileName = "analyzed_image_" + UUID.randomUUID() + ".jpg";
        String analyzedImageUrl = s3ImageService.uploadByteImage(analyzedImage, originalFileName);

        Map<String, Object> combinedResults = new HashMap<>();
        combinedResults.put("analyzedImageUrl", analyzedImageUrl);
        combinedResults.put("originalImageUrl", imageUrl);
        combinedResults.put("tooth_diseases", analysisResult.getDetectionResult().get("tooth_diseases"));
        combinedResults.put("gum_diseases", analysisResult.getDetectionResult().get("gum_diseases"));
        combinedResults.put("message", "Image is a valid oral image");

        return combinedResults;
    }

    /**
     * Save analysis result to the database.
     */
    public void saveAnalysisResult(Long userId, Map<String, Object> combinedResults) {
        Map<String, Object> combinedToothDiseases = (Map<String, Object>) combinedResults.get("tooth_diseases");
        Map<String, Object> combinedGumDiseases = (Map<String, Object>) combinedResults.get("gum_diseases");
        String analyzedImageUrl = (String) combinedResults.get("analyzedImageUrl");

        // 사용자 ID에 대한 기존 미완료 분석 가져오기
        Analysis existingAnalysis = analysisRepository.findByUserIdAndIsComplete(userId, false);

        if (existingAnalysis != null) {
            // 기존 미완료 분석이 있는 경우 업데이트

            // 기존 이미지 URL에 새로운 URL 추가
            List<String> analyzedImageUrls = new ArrayList<>(existingAnalysis.getAnalyzedImageUrls());
            analyzedImageUrls.add(analyzedImageUrl);
            existingAnalysis.setAnalyzedImageUrls(analyzedImageUrls);

            // 치아 질병 데이터 병합
            Map<String, Object> updatedToothDiseases = existingAnalysis.getToothDiseases();
            mergeDiseaseData(updatedToothDiseases, combinedToothDiseases);
            existingAnalysis.setToothDiseases(updatedToothDiseases); // 병합된 데이터를 다시 설정

            // 잇몸 질병 데이터 병합
            Map<String, Object> updatedGumDiseases = existingAnalysis.getGumDiseases();
            mergeDiseaseData(updatedGumDiseases, combinedGumDiseases);
            existingAnalysis.setGumDiseases(updatedGumDiseases); // 병합된 데이터를 다시 설정

            // 분석 결과 저장
            analysisRepository.save(existingAnalysis);
        } else {
            // 기존 미완료 분석이 없으면 새로운 분석 생성
            Analysis newAnalysis = new Analysis();

            // 새로운 분석 데이터 설정
            newAnalysis.setToothDiseases(filterDiseaseData(combinedToothDiseases));
            newAnalysis.setGumDiseases(filterDiseaseData(combinedGumDiseases));
            newAnalysis.setAnalyzedImageUrls(new ArrayList<>(List.of(analyzedImageUrl)));
            newAnalysis.setUserId(userId);

            // 분석 결과 저장
            analysisRepository.save(newAnalysis);
        }
    }
    // 공통 병합 로직: 기존 데이터와 새로운 데이터를 병합
    private void mergeDiseaseData(Map<String, Object> existingDiseases, Map<String, Object> newDiseases) {
        newDiseases.forEach((key, value) -> {
            List<Map<String, Object>> newDiseaseList = (List<Map<String, Object>>) value;

            // 새로운 데이터에서 disease_id 제거
            List<Map<String, Object>> filteredNewDiseaseList = new ArrayList<>();
            for (Map<String, Object> disease : newDiseaseList) {
                Map<String, Object> filteredDisease = new HashMap<>(disease);
                filteredDisease.remove("disease_id");
                filteredNewDiseaseList.add(filteredDisease);
            }

            // 기존 데이터 가져오기 (없으면 새 리스트 생성)
            List<Map<String, Object>> existingDiseaseList = (List<Map<String, Object>>) existingDiseases.getOrDefault(key, new ArrayList<>());
            existingDiseaseList.addAll(filteredNewDiseaseList);

            // 병합된 데이터 업데이트
            existingDiseases.put(key, existingDiseaseList);
        });
    }

    // 데이터 필터링 로직: disease_id 제거
    private Map<String, Object> filterDiseaseData(Map<String, Object> diseases) {
        Map<String, Object> filteredDiseases = new HashMap<>();

        diseases.forEach((key, value) -> {
            List<Map<String, Object>> diseaseList = (List<Map<String, Object>>) value;
            List<Map<String, Object>> filteredList = new ArrayList<>();

            // disease_id 제거
            for (Map<String, Object> disease : diseaseList) {
                Map<String, Object> filteredDisease = new HashMap<>(disease);
                filteredDisease.remove("disease_id");
                filteredList.add(filteredDisease);
            }

            filteredDiseases.put(key, filteredList);
        });

        return filteredDiseases;
    }



    /**
     * Mark the analysis as complete after submitting the symptom.
     */
    public void completeAnalysis(Long userId) {
        Analysis existingAnalysis = analysisRepository.findByUserIdAndIsComplete(userId, false);
        if (existingAnalysis != null) {
            existingAnalysis.setComplete(true);
            analysisRepository.save(existingAnalysis);
        }
    }

}