package SJUCapstone.BE.diagnosis.service;

import SJUCapstone.BE.diagnosis.model.Symptom;
import SJUCapstone.BE.diagnosis.repository.DiagnosisRepository;
import SJUCapstone.BE.diagnosis.repository.SymptomRepository;
import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.repository.UserRepository;
import SJUCapstone.BE.user.service.UserInfoService;
import SJUCapstone.BE.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SymptomsService {
    private final SymptomRepository symptomRepository;

    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public SymptomsService(SymptomRepository symptomRepository){
        this.symptomRepository = symptomRepository;
    }

    public Symptom createSymptom(Symptom symptom, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String userName = user.getName();

        symptom.setUserId(userId);
        symptom.setUserName(userName);

        return symptomRepository.save(symptom);
    }

}
