package SJUCapstone.BE.auth.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SubmitRequest {
    private Long painLevel;
    private List<String> symptomText;
    private List<String> symptomArea;
    private List<MultipartFile> images;
}
