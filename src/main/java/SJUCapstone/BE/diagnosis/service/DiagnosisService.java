package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.diagnosis.exception.DiagnosisNotFoundException;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.repository.DiagnosisRepository;
import SJUCapstone.BE.user.domain.UserInfo;
import SJUCapstone.BE.user.dto.UserInfoResponse;
import SJUCapstone.BE.user.service.UserInfoService;
import SJUCapstone.BE.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    public DiagnosisService(DiagnosisRepository diagnosisRepository){
        this.diagnosisRepository = diagnosisRepository;
    }

    public Diagnosis createDiagnoses(Diagnosis diagnosis, Long userId) {
        List<Diagnosis> diagnosisList =diagnosisRepository.findByUserIdOrderByDiagnoseDateAsc(userId);
        UserInfoResponse userInfo = userInfoService.getUserInfo(userId);
        System.out.println("diagnosis = " + diagnosis.getReportScore());
        System.out.println("userInfo.getLastDiagnoseScore() = " + userInfo.getLastDiagnoseScore());
        userInfoService.updateUser(userInfo.getUserInfoId(), diagnosis, diagnosisList.size());
        return diagnosisRepository.save(diagnosis);
    }

    public List<Diagnosis> getAllDiagnoses() {
        return diagnosisRepository.findAll();
    }


    public List<Diagnosis> getDiagnosesByUserId(Long userId) {
        return diagnosisRepository.findByUserIdOrderByDiagnoseDateDesc(userId);
    }

    public void deleteDiagnosisByUserAndIdx(Long userId, int index) {
        Diagnosis diagnosis = getDiagnosisByIndex(userId, index);
        System.out.println("diagnosis.getDiagnosisId() = " + diagnosis.getDiagnosisId());
        List<Diagnosis> diagnosisList =diagnosisRepository.findByUserIdOrderByDiagnoseDateAsc(userId);
        if (diagnosisList.size() == index) {
            Diagnosis last_diagnosis = getDiagnosisByIndex(userId, index - 1);
            System.out.println("last_diagnosis.getDiagnosisId() = " + last_diagnosis.getDiagnosisId());
            UserInfoResponse userInfo = userInfoService.getUserInfo(userId);
            userInfoService.updateUser(userInfo.getUserInfoId(), last_diagnosis, diagnosisList.size() - 2);
        }
        diagnosisRepository.delete(diagnosis);
    }

    public Diagnosis getDiagnosisByIndex(Long userId, int index) {
        List<Diagnosis> diagnosisList = diagnosisRepository.findByUserIdOrderByDiagnoseDateAsc(userId);

        if(index < 1 || index > diagnosisList.size()) {
            throw new DiagnosisNotFoundException("Report not found for index: " + index);
        }

        return diagnosisList.get(index - 1);
    }
}
