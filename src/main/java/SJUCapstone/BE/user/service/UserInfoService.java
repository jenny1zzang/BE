package SJUCapstone.BE.user.service;

import SJUCapstone.BE.auth.exception.UserNotFoundException;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.user.domain.UserInfo;
import SJUCapstone.BE.user.dto.UserInfoResponse;
import SJUCapstone.BE.user.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    public UserInfoResponse getUserInfo(Long userId) {
        UserInfo userInfo = userInfoRepository.findUserInfoByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        return makeUserInfoResponse(userInfo);
    }

    private UserInfoResponse makeUserInfoResponse(UserInfo userInfo) {
        return new UserInfoResponse(
                userInfo.getUserInfoId(),
                userInfo.getLastDiagnoseDate(),
                userInfo.getLastDiagnoseScore(),
                userInfo.getLastDiagnoseStatus(),
                userInfo.getDiagnoseNum());
    }

    public void updateUser(Long userInfoId, Diagnosis diagnosis, int num) {
        if (diagnosis.getDiagnoseDate() == null) {
            userInfoRepository.updateUserInfo(Timestamp.valueOf(LocalDateTime.now()), diagnosis.getReportScore(), diagnosis.getStatus(), num + 1, userInfoId, diagnosis.getUserId());
        } else {
            userInfoRepository.updateUserInfo(diagnosis.getDiagnoseDate(), diagnosis.getReportScore(), diagnosis.getStatus(), num + 1, userInfoId, diagnosis.getUserId());
        }

    }

}
