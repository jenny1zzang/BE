package SJUCapstone.BE.challenge.controller;

import SJUCapstone.BE.challenge.dto.AddChallengeRequest;
import SJUCapstone.BE.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/challenge")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @PostMapping("/add")
    public ResponseEntity<?> addChallenge(@RequestBody AddChallengeRequest request) {
        try {
            challengeService.addChallengeToCommunity(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{communityChallengeId}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long communityChallengeId) {
        try {
            challengeService.deleteCommunityChallenge(communityChallengeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list/{communityId}")
    public ResponseEntity<?> getCommunityChallenges(@PathVariable Long communityId) {
        try {
            return ResponseEntity.ok(challengeService.getCommunityChallenges(communityId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/success/{communityChallengeId}")
    public ResponseEntity<?> markChallengeSuccess(
            @PathVariable Long communityChallengeId
    ) {
        try {
            challengeService.markChallengeSuccess(communityChallengeId);
            return ResponseEntity.ok().body("챌린지가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/fail/{communityChallengeId}")
    public ResponseEntity<?> markChallengeFail(
            @PathVariable Long communityChallengeId
    ) {
        try {
            challengeService.markChallengeFail(communityChallengeId);
            return ResponseEntity.ok().body("챌린지가 실패로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/achievements/{communityId}")
    public ResponseEntity<?> getAchievements(
            @PathVariable Long communityId
    ) {
        try {
            return ResponseEntity.ok(challengeService.getAchievementsByCommunity(communityId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/restart/{communityChallengeId}")
    public ResponseEntity<?> restartChallenge(
            @PathVariable Long communityChallengeId
    ) {
        try {
            challengeService.restartChallenge(communityChallengeId);
            return ResponseEntity.ok().body("챌린지가 다시 시작되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
