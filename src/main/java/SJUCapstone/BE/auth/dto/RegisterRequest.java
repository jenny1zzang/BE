package SJUCapstone.BE.auth.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
    String email;
    String password;
    String name;
    int age;
    String gender;

    public RegisterRequest(String email, String password, String name, int age, String gender) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public RegisterRequest() {}
}
