package com.example.E4U.service;

import com.example.E4U.model.Diagnosis;
import com.example.E4U.repository.DiagnosisRepository;
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
