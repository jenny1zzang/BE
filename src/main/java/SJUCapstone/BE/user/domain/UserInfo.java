package SJUCapstone.BE.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userInfoId;

    private Long userId;
    private Timestamp LastDiagnoseDate;
    private String LastDiagnoseScore;
    private Long LastDiagnoseStatus;
    private int DiagnoseNum;

    public UserInfo(Long userId, Timestamp LastDiagnoseDate, String LastDiagnoseScore, Long LastDiagnoseStatus, int DiagnoseNum) {
        this.userId = userId;
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
    }

    public UserInfo(User user) {
        this.userId = user.getUserId();
    }

    public UserInfo() {
    }
}
