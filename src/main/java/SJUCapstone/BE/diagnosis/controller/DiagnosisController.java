package SJUCapstone.BE.diagnosis.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.diagnosis.exception.DiagnosisNotFoundException;
import SJUCapstone.BE.diagnosis.model.Diagnosis;
import SJUCapstone.BE.diagnosis.service.DiagnosisService;
import SJUCapstone.BE.user.service.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    @Autowired
    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @Autowired
    AuthService authService;
    @Autowired
    UserInfoService userInfoService;

//    @PostMapping
//    public ResponseEntity<?> createDiagnosis(@RequestBody Diagnosis diagnosis, HttpServletRequest request) {
//        try {
//            Long userId = authService.getUserId(request);
//            Diagnosis createdDiagnosis = diagnosisService.createDiagnoses(diagnosis, userId);
//            return ResponseEntity.ok(createdDiagnosis);
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }

    @GetMapping("/user")
    public ResponseEntity<?> getDiagnosisByUserId(HttpServletRequest request) {
        try {
            Long userId = authService.getUserId(request);
            List<Diagnosis> diagnosisList = diagnosisService.getDiagnosesByUserId(userId);

            return ResponseEntity.ok(diagnosisList);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/user/{idx}")
    public ResponseEntity<Diagnosis> getDiagnosisByUserAndIndex(HttpServletRequest request, @PathVariable int idx) {
        try {
            Long userId = authService.getUserId(request);
            Diagnosis diagnosis = diagnosisService.getDiagnosisByIndex(userId, idx);
            return ResponseEntity.ok(diagnosis);
        } catch (DiagnosisNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/user/{index}")
    public ResponseEntity<String> deleteDiagnosisByIndex(HttpServletRequest request, @PathVariable int index) {
        try {
            Long userId = authService.getUserId(request);
            Diagnosis diagnosis = diagnosisService.getDiagnosisByIndex(userId, index);
            diagnosisService.deleteDiagnosisByUserAndIdx(userId, index);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (DiagnosisNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found.");
        }
    }
}
