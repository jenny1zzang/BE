package SJUCapstone.BE.diagnosis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {
    private Long diagnosisId; //진단키
    private Long userId; //회원키
    private String images; //이미지들
    private LocalDateTime diagnosisDate; //진단 날짜
    private String conditions; //진단 질병 명
    private Long riskScores; //진단 점수
    private String recommendation; //관리 권장 사항
    private String description; //질병 설명
}
