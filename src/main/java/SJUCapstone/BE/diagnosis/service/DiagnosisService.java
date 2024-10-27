package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.diagnosis.exception.DiagnosisNotFoundException;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.repository.DiagnosisRepository;
import SJUCapstone.BE.user.domain.UserInfo;
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
    public DiagnosisService(DiagnosisRepository diagnosisRepository){
        this.diagnosisRepository = diagnosisRepository;
    }

    public Diagnosis createDiagnoses(Diagnosis diagnosis) {
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
