package SJUCapstone.BE.user.controller;

import SJUCapstone.BE.auth.service.AuthService;
import SJUCapstone.BE.image.S3ImageService;
import SJUCapstone.BE.user.dto.UserInfoResponse;
import SJUCapstone.BE.user.dto.UserUpdateRequest;
import SJUCapstone.BE.user.service.UserInfoService;
import SJUCapstone.BE.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    AuthService authService;
    @Autowired
    S3ImageService s3ImageService;


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

    @PatchMapping(value = "/user/profile_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserProfileImage(@RequestPart(value = "image", required = false) MultipartFile image, HttpServletRequest request) {
        try {
            // 사용자 확인
            Long userId = authService.getUserId(request);
            // 해당 사용자가 올린 프로필 이미지 저장
            String profileImageURL = s3ImageService.upload(image);
            userInfoService.updateUserImage(userId, profileImageURL);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
