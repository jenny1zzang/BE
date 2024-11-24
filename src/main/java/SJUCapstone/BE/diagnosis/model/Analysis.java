package SJUCapstone.BE.diagnosis.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "analysis")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long analysisId;

    @Setter
    @Schema(hidden = true)
    private Long userId; //회원키

    @Column(columnDefinition = "JSON")
    private String toothDiseases;

    @Column(columnDefinition = "JSON")
    private String gumDiseases;


    @Column(columnDefinition = "JSON")
    private String analyzedImageUrls;


    public Map<String, Object> getToothDiseases() {
        try {
            return new ObjectMapper().readValue(toothDiseases, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to toothDiseases", e);
        }
    }

    public Map<String, Object> getGumDiseases() {
        try {
            return new ObjectMapper().readValue(gumDiseases, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to toothDiseases", e);
        }
    }

    public void setToothDiseases(Map<String, Object> toothDiseases) {
        try {
            this.toothDiseases = new ObjectMapper().writeValueAsString(toothDiseases);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert toothDiseases to JSON", e);
        }
    }

    public void setGumDiseases(Map<String, Object> gumDiseases) {
        try {
            this.gumDiseases = new ObjectMapper().writeValueAsString(gumDiseases);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert toothDiseases to JSON", e);
        }
    }

    public List<String> getAnalyzedImageUrls() {
        try {
            return new ObjectMapper().readValue(analyzedImageUrls, List.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to analyzedImageUrls", e);
        }
    }

    public void setAnalyzedImageUrls(List<String> analyzedImageUrls) {
        try {
            this.analyzedImageUrls = new ObjectMapper().writeValueAsString(analyzedImageUrls);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert analyzedImageUrls to JSON", e);
        }
    }
}
