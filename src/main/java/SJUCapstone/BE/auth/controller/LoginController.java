package SJUCapstone.BE.auth.controller;

import SJUCapstone.BE.auth.dto.LoginRequest;
import SJUCapstone.BE.auth.dto.LoginResponse;
import SJUCapstone.BE.auth.dto.ServerLoginResponse;
import SJUCapstone.BE.auth.exception.InvalidPasswordException;
import SJUCapstone.BE.auth.exception.UserNotFoundException;
import SJUCapstone.BE.auth.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            ServerLoginResponse serverLoginResponse = loginService.login(loginRequest);

            response.addCookie(serverLoginResponse.getCookie());
            LoginResponse loginResponse = serverLoginResponse.getLoginResponse();

            return ResponseEntity.ok(loginResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
