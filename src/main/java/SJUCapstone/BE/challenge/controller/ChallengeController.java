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


    @Operation(
            summary = "커뮤니티에 챌린지 추가",
            description = "지정된 커뮤니티에 챌린지를 추가합니다. 요청 본문에는 커뮤니티 ID와 추가하려는 챌린지 ID가 필요합니다. 챌린지 ID는 /admin/challenge/list를 통해 조회 가능합니다."
    )
    @PostMapping("/add")
    public ResponseEntity<?> addChallenge(@RequestBody AddChallengeRequest request) {
        try {
            challengeService.addChallengeToCommunity(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "커뮤니티에서 챌린지 삭제",
            description = "지정된 커뮤니티에 연결된 챌린지를 삭제합니다. URL 경로에서 communityChallengeId를 전달해야 합니다."
    )
    @DeleteMapping("/delete/{communityChallengeId}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long communityChallengeId) {
        try {
            challengeService.deleteCommunityChallenge(communityChallengeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "특정 커뮤니티의 챌린지 조회",
            description = "특정 communityId에 속한 모든 챌린지와 관련 정보를 반환"
    )
    @GetMapping("/list/{communityId}")
    public ResponseEntity<?> getCommunityChallenges(@PathVariable Long communityId) {
        try {
            return ResponseEntity.ok(challengeService.getCommunityChallenges(communityId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
