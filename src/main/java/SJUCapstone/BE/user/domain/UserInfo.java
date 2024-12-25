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

    private static final String basicImageURL = "https://e4u.s3.ap-northeast-2.amazonaws.com/341316c5-4basic.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userInfoId;

    private Long userId;
    private Timestamp LastDiagnoseDate;
    private Integer LastDiagnoseScore;
//    private Long LastDiagnoseStatus;
    private int DiagnoseNum;
    private String UserName;
    private String UserImage;

    public UserInfo(Long userId, Timestamp LastDiagnoseDate, Integer LastDiagnoseScore, int DiagnoseNum, String UserName, String UserImage) {
        this.userId = userId;
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
//        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
        this.UserName = UserName;
        this.UserImage = UserImage;
    }

    public UserInfo(User user) {
        this.userId = user.getUserId();
        this.UserName = user.getName();
        this.UserImage = basicImageURL;
    }

    public UserInfo() {
    }
}