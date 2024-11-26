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
    @Column(columnDefinition = "JSON")
    private List<String> analyzedImageUrls;

    @CreationTimestamp
    @Schema(hidden = true)
    private Timestamp diagnoseDate; //진단 날짜


    private String result;
    private String detailed_result;
    private String care_method;
    private Integer dangerPoint;
}