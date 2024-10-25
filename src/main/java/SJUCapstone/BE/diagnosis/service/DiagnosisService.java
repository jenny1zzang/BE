package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.repository.DiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;

    @Autowired
    public DiagnosisService(DiagnosisRepository diagnosisRepository){
        this.diagnosisRepository = diagnosisRepository;
    }

    public Diagnosis createDiagnoses(Diagnosis diagnosis) {
        System.out.println(diagnosis.toString());
        return diagnosisRepository.save(diagnosis);
    }

    public List<Diagnosis> getAllDiagnoses() {
        return diagnosisRepository.findAll();
    }

    public Optional<Diagnosis> getDiagnosesById(Long id) {
        return diagnosisRepository.findById(id);
    }

    public List<Diagnosis> getDiagnosesByUserId(Long userId) {
        return diagnosisRepository.findByUserId(userId);
    }

    public void deleteDiagnoses(Long id) {
        diagnosisRepository.deleteById(id);
    }
}
