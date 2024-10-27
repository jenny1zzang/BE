package SJUCapstone.BE.diagnosis.model;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import java.sql.Timestamp;
import java.util.Map;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagnosisId; //진단키

    private Long userId; //회원키
    private String userName; // 회원 이름

    @Type(JsonType.class)
    @Column(name = "images")
    private Map<String, Object> images; //이미지들

    @CreationTimestamp
    private Timestamp diagnoseDate; //진단 날짜

    private String reportScore; //질병 별 확률
    private String status; // 진단 정보( 전체 점수 )
    private String recommendation; //관리 권장 사항
    private String diagnoseCondition; //진단 질병 명
    private String description; //질병 설명
}
