package SJUCapstone.BE.user.dto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UserInfoResponse {
    private Long userInfoId;
    private Timestamp LastDiagnoseDate;
    private String LastDiagnoseScore;
    private Long LastDiagnoseStatus;
    private int DiagnoseNum;

    public UserInfoResponse(Long userInfoId, Timestamp LastDiagnoseDate, String LastDiagnoseScore, Long LastDiagnoseStatus, int DiagnoseNum) {
        this.userInfoId = userInfoId;
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
    }

    public UserInfoResponse() {
    }
}
