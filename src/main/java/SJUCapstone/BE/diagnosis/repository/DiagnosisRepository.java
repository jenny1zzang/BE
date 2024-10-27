package SJUCapstone.BE.diagnosis.repository;

import SJUCapstone.BE.diagnosis.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
//    List<Diagnosis> findByUserId(Long userId);
    List<Diagnosis> findByUserIdOrderByDiagnoseDateDesc(Long userId);
    List<Diagnosis> findByUserIdOrderByDiagnoseDateAsc(Long userId);
}
