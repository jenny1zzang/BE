package SJUCapstone.BE.diagnosis.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.io.IOException;
import java.util.ArrayList;
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

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private String toothDiseases;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private String gumDiseases;


    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private String analyzedImageUrls;


    @Schema(hidden = true)
    private boolean isComplete = false;

    // getter와 setter 추가
    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

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
        if (analyzedImageUrls == null || analyzedImageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(analyzedImageUrls, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to List of analyzedImageUrls", e);
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
