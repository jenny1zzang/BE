package com.example.E4U.controller;

import com.example.E4U.model.Diagnosis;
import com.example.E4U.service.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    @Autowired
    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping
    public ResponseEntity<Diagnosis> createDiagnosis(@RequestBody Diagnosis diagnosis, @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = extractAccessToken(authorizationHeader);

        if (!isValidToken(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Diagnosis createdDiagnosis = diagnosisService.createDiagnoses(diagnosis);
        return ResponseEntity.ok(createdDiagnosis);
    }

    @GetMapping
    public ResponseEntity<List<Diagnosis>> getAllDiagnosis() {
        List<Diagnosis> diagnosisList = diagnosisService.getAllDiagnoses();
        return ResponseEntity.ok(diagnosisList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diagnosis> getDiagnosisById(@PathVariable Long id) {
        Optional<Diagnosis> diagnoses = diagnosisService.getDiagnosesById(id);
        return diagnoses.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diagnosis>> getDiagnosisByUserId(@PathVariable Long userId) {
        List<Diagnosis> diagnosisList = diagnosisService.getDiagnosesByUserId(userId);
        return ResponseEntity.ok(diagnosisList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnoses(id);
        return ResponseEntity.noContent().build();
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
    }
}
