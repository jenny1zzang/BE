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

    public UserInfoResponse(Long userInfoId, Timestamp LastDiagnoseDate, String LastDiagnoseScore, String LastDiagnoseStatus, int DiagnoseNum) {
        this.userInfoId = userInfoId;
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
    }

    public UserInfoResponse() {
    }
}
