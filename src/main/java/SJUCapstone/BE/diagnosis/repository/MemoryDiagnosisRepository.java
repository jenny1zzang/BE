package SJUCapstone.BE.diagnosis.repository;

import SJUCapstone.BE.diagnosis.model.Diagnosis;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemoryDiagnosisRepository implements DiagnosisRepository {

    private final Map<Long, Diagnosis> diagnosisStore = new HashMap<>();
    private long idCounter = 0;

    @Override
    public Diagnosis save(Diagnosis diagnosis) {
        diagnosis.setDiagnosisId(++idCounter);
        diagnosisStore.put(diagnosis.getDiagnosisId(), diagnosis);
        return diagnosis;
    }

    @Override
    public List<Diagnosis> findAll() {
        return new ArrayList<>(diagnosisStore.values());
    }

    @Override
    public Optional<Diagnosis> findById(Long id) {
        return Optional.ofNullable(diagnosisStore.get(id));
    }

    @Override
    public List<Diagnosis> findByUserId(Long userId) {
        List<Diagnosis> result = new ArrayList<>();
        for (Diagnosis diagnosis : diagnosisStore.values()) {
            if (diagnosis.getUserId().equals(userId)) {
                result.add(diagnosis);
            }
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        diagnosisStore.remove(id);
    }
}
