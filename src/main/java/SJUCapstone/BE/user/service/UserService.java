package SJUCapstone.BE.user.service;

import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.domain.UserInfo;
import SJUCapstone.BE.user.dto.UserUpdateRequest;
import SJUCapstone.BE.user.repository.UserInfoRepository;
import SJUCapstone.BE.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserInfoRepository userInfoRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public boolean checkDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateUser(Long userId , UserUpdateRequest request) {
        userRepository.updateUser(userId, request.getName(), request.getAge(), request.getGender());
    }
}
