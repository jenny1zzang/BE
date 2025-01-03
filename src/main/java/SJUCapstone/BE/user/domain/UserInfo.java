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
    private Integer LastDiagnoseScore;
//    private Long LastDiagnoseStatus;
    private int DiagnoseNum;
    private String UserName;

    public UserInfo(Long userId, Timestamp LastDiagnoseDate, Integer LastDiagnoseScore, int DiagnoseNum, String UserName) {
        this.userId = userId;
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
//        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
        this.UserName = UserName;
    }

    public UserInfo(User user) {
        this.userId = user.getUserId();
        this.UserName = user.getName();
    }

    public UserInfo() {
    }
}