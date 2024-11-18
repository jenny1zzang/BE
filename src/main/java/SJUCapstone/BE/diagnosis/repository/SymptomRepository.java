package SJUCapstone.BE.diagnosis.repository;

import SJUCapstone.BE.diagnosis.model.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
}
