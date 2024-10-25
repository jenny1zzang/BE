package SJUCapstone.BE.diagnosis.repository;

import SJUCapstone.BE.diagnosis.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Diagnosis save(Diagnosis diagnosis);
    List<Diagnosis> findAll();
    Optional<Diagnosis> findById(Long id);
    List<Diagnosis> findByUserId(Long userId);
    void deleteById(Long id);
}
