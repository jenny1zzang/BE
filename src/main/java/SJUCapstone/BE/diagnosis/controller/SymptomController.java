package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.model.Symptom;
import SJUCapstone.BE.diagnosis.repository.SymptomRepository;
import SJUCapstone.BE.diagnosis.service.SymptomsService;
import SJUCapstone.BE.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/symptom")
public class SymptomController {

    private final SymptomsService symptomsService;

    @Autowired
    public SymptomController(SymptomsService symptomsService) {
        this.symptomsService = symptomsService;}
    @Autowired
    AuthService authService;
    @Autowired
    UserInfoService userInfoService;

    @PostMapping
    public ResponseEntity<?> createSymptom(@RequestBody Symptom symptom, HttpServletRequest request){
        try {
            Long userId = authService.getUserId(request);
            Symptom createdSymptom = symptomsService.createSymptom(symptom, userId);
            return ResponseEntity.ok(createdSymptom);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
