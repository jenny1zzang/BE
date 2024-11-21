package SJUCapstone.BE.diagnosis.model;

import java.util.Map;

public class ImageAnalysisResult {
    private byte[] analyzedImage;
    private Map<String, Object> detectionResult;

    // Getter와 Setter 추가
    public byte[] getAnalyzedImage() {
        return analyzedImage;
    }

    public void setAnalyzedImage(byte[] analyzedImage) {
        this.analyzedImage = analyzedImage;
    }

    public Map<String, Object> getDetectionResult() {
        return detectionResult;
    }

    public void setDetectionResult(Map<String, Object> detectionResult) {
        this.detectionResult = detectionResult;
    }
}
