package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Analysis;
import SJUCapstone.BE.diagnosis.model.AnalysisResult;
import SJUCapstone.BE.diagnosis.repository.AnalysisRepository;
import SJUCapstone.BE.image.S3ImageService;
import SJUCapstone.BE.user.service.UserInfoService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageAnalysisService {

    private static final String MODEL_SERVER_BASE_URL = "http://115.23.175.131:8000";

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
    public boolean areMouthImages(List<MultipartFile> images) {
        String url = MODEL_SERVER_BASE_URL + "/is_mouth/";

        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(images);
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
    public List<AnalysisResult> processImagesAndGetDetectionResults(List<MultipartFile> images) {
        List<AnalysisResult> results = new ArrayList<>();

        for (MultipartFile image : images) {
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
                        results.add(analysisResult);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading image file: " + e.getMessage());
            } catch (RestClientException e) {
                System.err.println("Error processing image: " + e.getMessage());
            }
        }

        return results;
    }

    /**
     * Detect an image and return the analyzed image as a byte array.
     */
    private byte[] detectImage(MultipartFile image) throws IOException {
        String url = MODEL_SERVER_BASE_URL + "/detect/";
        HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(List.of(image));

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

    /**
     * Create a multipart request entity for a list of images.
     */
    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequest(List<MultipartFile> images) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile image : images) {
            Resource fileResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
        }

        return new HttpEntity<>(body, headers);
    }

    /**
     * Send combined results to the model server.
     */
    public String sendCombinedResultsToModelServer(Map<String, Object> combinedResults) {
        String url = MODEL_SERVER_BASE_URL + "/result_report/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(combinedResults, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                System.err.println("Failed to send combined results to model server: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.err.println("Error calling submit_results API: " + e.getMessage());
        }

        return null;
    }
    /**
     * Upload and analyze images, and return the combined results.
     */
    public Map<String, Object> uploadAndAnalyzeImages(List<MultipartFile> images, Long userId) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        List<AnalysisResult> analysisResults = processImagesAndGetDetectionResults(images);

        // S3에 원본 이미지 업로드
        for (MultipartFile image : images) {
            String imageUrl = s3ImageService.upload(image);
            imageUrls.add(imageUrl);
        }

        Map<String, Object> combinedResults = new HashMap<>();
        Map<String, Object> combinedToothDiseases = new HashMap<>();
        Map<String, Object> combinedGumDiseases = new HashMap<>();
        List<String> analyzedImageUrls = new ArrayList<>();

        for (AnalysisResult analysisResult : analysisResults) {
            byte[] analyzedImage = analysisResult.getAnalyzedImage();
            String originalFileName = "analyzed_image_" + analyzedImageUrls.size() + ".jpg";
            String analyzedImageUrl = s3ImageService.uploadByteImage(analyzedImage, originalFileName);
            analyzedImageUrls.add(analyzedImageUrl);

            // 치아 질병 결과 병합
            Map<String, Object> detectionResult = analysisResult.getDetectionResult();
            if (detectionResult.containsKey("tooth_diseases")) {
                mergeDiseaseResults((Map<String, Object>) detectionResult.get("tooth_diseases"), combinedToothDiseases);
            }
            if (detectionResult.containsKey("gum_diseases")) {
                mergeDiseaseResults((Map<String, Object>) detectionResult.get("gum_diseases"), combinedGumDiseases);
            }
        }

        combinedResults.put("tooth_diseases", combinedToothDiseases);
        combinedResults.put("gum_diseases", combinedGumDiseases);
        combinedResults.put("analyzedImageUrls", analyzedImageUrls);
//        combinedResults.put("originalImageUrls", imageUrls);
        combinedResults.put("message", "All images are valid oral images");

        return combinedResults;
    }

    /**
     * Save analysis result to the database.
     */
    public void saveAnalysisResult(Long userId, Map<String, Object> combinedResults) {
        Map<String, Object> combinedToothDiseases = (Map<String, Object>) combinedResults.get("tooth_diseases");
        Map<String, Object> combinedGumDiseases = (Map<String, Object>) combinedResults.get("gum_diseases");
        List<String> analyzedImageUrls = (List<String>) combinedResults.get("analyzedImageUrls");

        Analysis existingAnalysis = analysisRepository.findByUserId(userId);
        if (existingAnalysis != null) {
            existingAnalysis.setToothDiseases(combinedToothDiseases);
            existingAnalysis.setGumDiseases(combinedGumDiseases);
            existingAnalysis.setAnalyzedImageUrls(analyzedImageUrls);
            analysisRepository.save(existingAnalysis);
        } else {
            Analysis analysis = new Analysis();
            analysis.setToothDiseases(combinedToothDiseases);
            analysis.setAnalyzedImageUrls(analyzedImageUrls);
            analysis.setGumDiseases(combinedGumDiseases);
            analysis.setUserId(userId);
            analysisRepository.save(analysis);
        }
    }

    private void mergeDiseaseResults(Map<String, Object> source, Map<String, Object> target) {
        source.forEach((key, value) -> {
            List<Map<String, Object>> diseasesList = (List<Map<String, Object>>) value;
            List<Map<String, Object>> filteredList = new ArrayList<>();
            for (Map<String, Object> disease : diseasesList) {
                Map<String, Object> filteredDisease = new HashMap<>(disease);
                filteredDisease.remove("disease_id");
                filteredList.add(filteredDisease);
            }

            if (!target.containsKey(key)) {
                target.put(key, filteredList);
            } else {
                List<Map<String, Object>> existingList = (List<Map<String, Object>>) target.get(key);
                existingList.addAll(filteredList);
            }
        });
    }
}