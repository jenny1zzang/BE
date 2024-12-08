package SJUCapstone.BE.diagnosis.repository;

import SJUCapstone.BE.diagnosis.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByUserIdOrderByDiagnoseDateDesc(Long userId);

    List<Diagnosis> findByUserIdOrderByDiagnoseDateAsc(Long userId);
}
