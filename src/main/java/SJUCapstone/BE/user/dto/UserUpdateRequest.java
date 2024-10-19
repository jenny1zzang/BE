package SJUCapstone.BE.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String name;
    private int age;
    private String gender;

    public UserUpdateRequest(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public UserUpdateRequest() {}
}
