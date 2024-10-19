package SJUCapstone.BE.user.service;

import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User newUser() {
        User user = new User(
                "testemail",
                "1234",
                "testname",
                20,
                "남자"
        );

        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found By email"));
    }
}
