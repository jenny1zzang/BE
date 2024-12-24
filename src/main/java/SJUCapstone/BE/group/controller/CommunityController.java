package SJUCapstone.BE.group.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.group.domain.Community;
import SJUCapstone.BE.group.dto.InviteRequest;
import SJUCapstone.BE.group.service.CommunityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    CommunityService communityService;
    @Autowired
    AuthService authService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createCommunity(@RequestParam("communityName") String communityName, HttpServletRequest request) {
        try {
            // 사용자 ID 및 이름 가져오기
            Long userId = authService.getUserId(request);

            // group 생성하기
            Long groupId = communityService.createCommunity(communityName);
            communityService.registerCommunity(groupId, userId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/invite")
    public ResponseEntity<?> invite(@RequestBody InviteRequest invite, HttpServletRequest request) {
        try {
            communityService.inviteCommunity(invite);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getCommunities(HttpServletRequest request) {
        try{
            // 사용자 ID 및 이름 가져오기
            Long userId = authService.getUserId(request);

            List<Community> communities = communityService.getCommunities(userId);

            return ResponseEntity.ok().body(communities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
