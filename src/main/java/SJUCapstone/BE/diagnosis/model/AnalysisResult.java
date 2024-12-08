package SJUCapstone.BE.diagnosis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AnalysisResult {
    // Getter와 Setter 추가

    private byte[] analyzedImage;

    private Map<String, Object> detectionResult;

    private Map<String, Float> detectionPointResult;
}