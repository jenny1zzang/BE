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

import java.util.List;

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

    public Symptom createSymptom(Long userId, String userName, Long painLevel, List<String> symptomText,
                                 List<String> symptomArea) {

        Symptom symptom = Symptom.builder()
                .userId(userId)
                .userName(userName)
                .painLevel(painLevel)
                .symptomText(symptomText)
                .symptomArea(symptomArea)
                .build();

        return symptomRepository.save(symptom);
    }

}
