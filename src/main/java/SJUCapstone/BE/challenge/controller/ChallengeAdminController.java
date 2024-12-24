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


    @Operation(
            summary = "새로운 챌린지 생성",
            description = "관리자가 새로운 챌린지를 생성합니다. 요청 본문에는 챌린지 설명이 포함됩니다."
    )
    @PostMapping("/create")
    public ResponseEntity<?> createChallenge(@RequestBody CreateChallengeRequest request) {
        try {
            challengeAdminService.createChallenge(request);
            return ResponseEntity.ok().body("챌린지가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "모든 챌린지 조회",
            description = "관리자가 생성된 모든 챌린지를 조회합니다."
    )
    @GetMapping("/list")
    public ResponseEntity<?> listChallenges() {
        try {
            return ResponseEntity.ok(challengeAdminService.getAllChallenges());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "특정 챌린지 삭제",
            description = "관리자가 특정 챌린지를 삭제합니다. URL 경로 변수로 삭제할 챌린지 ID를 전달해야 합니다."
    )
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
