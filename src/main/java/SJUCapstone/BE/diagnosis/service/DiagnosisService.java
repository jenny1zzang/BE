package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.diagnosis.exception.DiagnosisNotFoundException;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.repository.DiagnosisRepository;
import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.dto.UserInfoResponse;
import SJUCapstone.BE.user.repository.UserRepository;
import SJUCapstone.BE.user.service.UserInfoService;
import SJUCapstone.BE.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    private UserRepository userRepository;


    @Autowired
    public DiagnosisService(DiagnosisRepository diagnosisRepository){
        this.diagnosisRepository = diagnosisRepository;
    }

    public void createDiagnoses(Diagnosis diagnosis, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String userName = user.getName();

        diagnosis.setUserId(userId);
        diagnosis.setUserName(userName);

        int diagnoseNumber = diagnosisRepository.findByUserIdOrderByDiagnoseDateAsc(userId).size();
        UserInfoResponse userInfo = userInfoService.getUserInfo(userId);

        userInfoService.updateUser(userInfo.getUserInfoId(), diagnosis, diagnoseNumber);

        diagnosisRepository.save(diagnosis);
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