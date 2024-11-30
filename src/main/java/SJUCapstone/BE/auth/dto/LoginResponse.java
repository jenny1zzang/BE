package SJUCapstone.BE.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    String refreshToken;
    String accessToken;

    public LoginResponse(String accessToken, String refreshToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
