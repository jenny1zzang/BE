package SJUCapstone.BE.user.dto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UserInfoResponse {
    private Long userInfoId;
    private Timestamp LastDiagnoseDate;
    private String LastDiagnoseScore;
    private String LastDiagnoseStatus;
    private int DiagnoseNum;
    private String UserName;

    public UserInfoResponse(Long userInfoId, Timestamp LastDiagnoseDate, String LastDiagnoseScore, String LastDiagnoseStatus, int DiagnoseNum, String UserName) {
        this.userInfoId = userInfoId;
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
        this.UserName = UserName;
    }

    public UserInfoResponse() {
    }
}
