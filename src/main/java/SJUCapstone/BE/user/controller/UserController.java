package SJUCapstone.BE.user.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.user.dto.UserInfoResponse;
import SJUCapstone.BE.user.dto.UserUpdateRequest;
import SJUCapstone.BE.user.service.UserInfoService;
import SJUCapstone.BE.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    AuthService authService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {

        try {
            Long userId = authService.getUserId(request);
            UserInfoResponse response = userInfoService.getUserInfo(userId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest httpServletRequest) {

        try {
            Long userId = authService.getUserId(httpServletRequest);
            userService.updateUser(userId, userUpdateRequest);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
