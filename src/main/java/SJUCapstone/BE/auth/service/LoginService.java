package SJUCapstone.BE.auth.service;

import SJUCapstone.BE.auth.domain.Token;
import SJUCapstone.BE.auth.dto.*;
import SJUCapstone.BE.auth.exception.InvalidPasswordException;
import SJUCapstone.BE.auth.exception.UserNotFoundException;
import SJUCapstone.BE.auth.repository.TokenRepository;
import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    AuthService authService;

    public LoginService() {
    }

    public ServerLoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));

        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        } else {
            TokenResponse tokens = authService.generateTokens(user.getEmail());

            Cookie cookie = getCookie(tokens);
            LoginResponse loginResponse = new LoginResponse(tokens.getRefreshToken());

            saveToken(tokens, user);

            System.out.println(cookie);
            System.out.println(loginResponse);

            return new ServerLoginResponse(cookie, loginResponse);
        }
    }

    private Cookie getCookie(TokenResponse tokens) {
        Cookie cookie = new Cookie("accessToken", tokens.getAccessToken());
        cookie.setHttpOnly(true);  // 자바스크립트에서 접근 불가
        cookie.setMaxAge(60 * 60 * 24);  // 1일 동안 유지
        cookie.setPath("/");
        return cookie;
    }

    private void saveToken(TokenResponse tokens, User user) {
        Token token = new Token(user.getUserId(), tokens.getRefreshToken(), tokens.getAccessToken());
        tokenRepository.save(token);
    }

    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setName(registerRequest.getName());
        user.setAge(registerRequest.getAge());
        user.setGender(registerRequest.getGender());

        userRepository.save(user);
    }

    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
