package SJUCapstone.BE.challenge.controller;

import SJUCapstone.BE.challenge.dto.CreateChallengeRequest;
import SJUCapstone.BE.challenge.service.ChallengeAdminService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/challenge")
public class ChallengeAdminController {

    @Autowired
    private ChallengeAdminService challengeAdminService;


    @PostMapping("/create")
    public ResponseEntity<?> createChallenge(@RequestBody CreateChallengeRequest request) {
        try {
            challengeAdminService.createChallenge(request);
            return ResponseEntity.ok().body("챌린지가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listChallenges() {
        try {
            return ResponseEntity.ok(challengeAdminService.getAllChallenges());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{challengeId}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long challengeId) {
        try {
            challengeAdminService.deleteChallenge(challengeId);
            return ResponseEntity.ok().body("챌린지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
