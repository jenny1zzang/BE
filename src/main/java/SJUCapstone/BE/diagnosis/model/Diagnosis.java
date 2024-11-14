package SJUCapstone.BE.diagnosis.model;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long diagnosisId; //진단키

    @Schema(hidden = true)
    private Long userId; //회원키
    @Schema(hidden = true)
    private String userName; // 회원 이름

    @Type(JsonType.class)
    @Column(name = "images")
    private Map<String, Object> images; //이미지들

    @CreationTimestamp
    @Schema(hidden = true)
    private Timestamp diagnoseDate; //진단 날짜

    private String reportScore; //질병 별 확률
    private Long status; // 진단 정보( 전체 점수 )

    @Lob
    @Type(JsonType.class)
    private List<String> recommendation; //관리 권장 사항

    @Lob
    @Type(JsonType.class)
    private List<String> diagnoseCondition; //진단 질병 명

    @Lob
    @Type(JsonType.class)
    private List<String> description; //질병 설명
}
