package SJUCapstone.BE.user.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.user.dto.UserInfoResponse;
import SJUCapstone.BE.user.service.UserInfoService;
import SJUCapstone.BE.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    AuthService authService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {

        String accessToken = authService.accessTokenExtractor(request);

        if (authService.validateToken(accessToken)) {
            String email = authService.extractEmail(accessToken);
            Long userId = userService.findByEmail(email).getUserId();

            UserInfoResponse response = userInfoService.getUserInfo(userId);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
