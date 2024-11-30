package SJUCapstone.BE.auth.service;

import SJUCapstone.BE.auth.domain.Token;
import SJUCapstone.BE.auth.dto.*;
import SJUCapstone.BE.auth.exception.InvalidPasswordException;
import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.domain.UserInfo;
import SJUCapstone.BE.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;

    public LoginService() {}

    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userService.findByEmail(email);

        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        } else {
            TokenResponse tokens = authService.generateTokens(user.getEmail());
            saveToken(tokens, user);

            return new LoginResponse(tokens.getAccessToken(),tokens.getRefreshToken());
        }
    }

    private void saveToken(TokenResponse tokens, User user) {
        Token token = new Token(user.getUserId(), tokens.getRefreshToken(), tokens.getAccessToken());
        authService.saveToken(token);
    }

    public void register(RegisterRequest registerRequest) {
        User user = new User(registerRequest);

        userService.saveUser(user);
        userService.saveUserInfo(new UserInfo(user));
    }


    public boolean isEmailAvailable(String email) {
        return !userService.checkDuplicateEmail(email);
    }
}
