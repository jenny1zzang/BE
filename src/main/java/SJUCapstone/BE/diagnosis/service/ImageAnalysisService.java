package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.diagnosis.model.ImageAnalysisResult;
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
import java.util.List;
import java.util.Map;

@Service
public class ImageAnalysisService {
    private final RestTemplate restTemplate;

    @Autowired
    public ImageAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean areMouthImages(List<MultipartFile> images) {
        // 모델 서버의 /is_mouth/ 엔드포인트 URL 설정
        String modelServerUrl = "http://222.109.26.240:8000/is_mouth/";

        try {
            // 요청을 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 요청 본문에 MultipartFile 포함하여 전송
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for (MultipartFile image : images) {
                // ByteArrayResource 사용해 파일의 내용을 바이트 배열로 변환
                byte[] bytes = image.getBytes();
                Resource fileResource = new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                };
                body.add("file", fileResource);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 모델 서버로 다중 이미지 파일 전송
            ResponseEntity<Map> response = restTemplate.postForEntity(modelServerUrl, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    // 모델 서버의 응답에서 "is_mouth_image" 필드를 체크
                    return Boolean.TRUE.equals(responseBody.get("is_mouth_image"));
                } else {
                    System.err.println("Response body is null");
                }
            } else {
                System.err.println("Unexpected status code: " + response.getStatusCode());
            }
        } catch (IOException e) {
            System.err.println("IOException occurred while reading the image file: " + e.getMessage());
            e.printStackTrace();
        } catch (RestClientException e) {
            System.err.println("RestTemplate request error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

//    public List<byte[]> sendImagesForDetectionAndReceiveImages(List<MultipartFile> images) {
//        String detectUrl = "http://222.109.26.240:8000/detect/";
//        List<byte[]> analyzedImages = new ArrayList<>();
//
//        for (MultipartFile image : images) {
//            try {
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//                byte[] bytes = image.getBytes();
//                Resource fileResource = new ByteArrayResource(bytes) {
//                    @Override
//                    public String getFilename() {
//                        return image.getOriginalFilename();
//                    }
//                };
//                body.add("file", fileResource);
//
//                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//                ResponseEntity<byte[]> response = restTemplate.exchange(detectUrl, HttpMethod.POST, requestEntity, byte[].class);
//
//                if (response.getStatusCode() == HttpStatus.OK) {
//                    analyzedImages.add(response.getBody());
//                } else {
//                    System.err.println("Failed to get analyzed image: " + response.getStatusCode());
//                }
//            } catch (IOException e) {
//                System.err.println("IOException occurred while reading the image file: " + e.getMessage());
//                e.printStackTrace();
//            } catch (RestClientException e) {
//                System.err.println("RestTemplate request error: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//
//        return analyzedImages;
//    }

    public List<ImageAnalysisResult> processImagesAndGetDetectionResults(List<MultipartFile> images) {
        String detectUrl = "http://222.109.26.240:8000/detect/";
        String detectionResultUrl = "http://222.109.26.240:8000/detection_result/";

        List<ImageAnalysisResult> results = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                // Step 1: Send image for detection and get analyzed image
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                byte[] bytes = image.getBytes();
                Resource fileResource = new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                };
                body.add("file", fileResource);

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<byte[]> response = restTemplate.exchange(detectUrl, HttpMethod.POST, requestEntity, byte[].class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    byte[] analyzedImage = response.getBody();

                    // Step 2: Send analyzed image to detection_result API
                    HttpHeaders detectionHeaders = new HttpHeaders();
                    detectionHeaders.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<byte[]> detectionRequestEntity = new HttpEntity<>(analyzedImage, detectionHeaders);
                    ResponseEntity<Map> detectionResponse = restTemplate.postForEntity(detectionResultUrl, detectionRequestEntity, Map.class);

                    if (detectionResponse.getStatusCode() == HttpStatus.OK && detectionResponse.getBody() != null) {
                        // Create DTO for each image
                        ImageAnalysisResult analysisResult = new ImageAnalysisResult();
                        analysisResult.setAnalyzedImage(analyzedImage);
                        analysisResult.setDetectionResult(detectionResponse.getBody());

                        results.add(analysisResult);
                    } else {
                        System.err.println("Failed to get detection result: " + detectionResponse.getStatusCode());
                    }
                } else {
                    System.err.println("Failed to get analyzed image: " + response.getStatusCode());
                }
            } catch (IOException e) {
                System.err.println("IOException occurred while reading the image file: " + e.getMessage());
                e.printStackTrace();
            } catch (RestClientException e) {
                System.err.println("RestTemplate request error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return results;
    }


//    public List<Map<String, Object>> getDetectionResults(List<byte[]> analyzedImages) {
//        String detectionResultUrl = "http://222.109.26.240:8000/detection_result/";
//        List<Map<String, Object>> detectionResults = new ArrayList<>();
//
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            for (byte[] analyzedImage : analyzedImages) {
//                // Prepare individual request for each analyzed image
//                HttpEntity<byte[]> requestEntity = new HttpEntity<>(analyzedImage, headers);
//                ResponseEntity<Map> response = restTemplate.postForEntity(detectionResultUrl, requestEntity, Map.class);
//
//                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                    detectionResults.add(response.getBody());
//                } else {
//                    System.err.println("Failed to get detection result for one image: " + response.getStatusCode());
//                }
//            }
//        } catch (RestClientException e) {
//            System.err.println("RestTemplate request error: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return detectionResults;
//    }

}
