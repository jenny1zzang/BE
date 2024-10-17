package com.example.E4U.repository;

import com.example.E4U.model.Diagnosis;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository {
    Diagnosis save(Diagnosis diagnosis);
    List<Diagnosis> findAll();
    Optional<Diagnosis> findById(Long id);
    List<Diagnosis> findByUserId(Long userId);
    void deleteById(Long id);
}
