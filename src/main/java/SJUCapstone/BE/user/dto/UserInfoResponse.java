package SJUCapstone.BE.user.dto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UserInfoResponse {
    private Timestamp LastDiagnoseDate;
    private int LastDiagnoseScore;
    private String LastDiagnoseStatus;
    private int DiagnoseNum;

    public UserInfoResponse(Timestamp LastDiagnoseDate, int LastDiagnoseScore, String LastDiagnoseStatus, int DiagnoseNum) {
        this.LastDiagnoseDate = LastDiagnoseDate;
        this.LastDiagnoseScore = LastDiagnoseScore;
        this.LastDiagnoseStatus = LastDiagnoseStatus;
        this.DiagnoseNum = DiagnoseNum;
    }

    public UserInfoResponse() {
    }
}
