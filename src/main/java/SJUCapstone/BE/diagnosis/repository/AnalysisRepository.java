package SJUCapstone.BE.diagnosis.repository;

import SJUCapstone.BE.diagnosis.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Analysis findByUserIdAndIsComplete(Long userId, boolean isComplete);

    Analysis findTopByUserIdAndIsCompleteOrderByAnalysisIdDesc(Long userId, boolean isComplete);

}
