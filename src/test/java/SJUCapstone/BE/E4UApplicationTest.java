package SJUCapstone.BE;

import SJUCapstone.BE.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = E4UApplication.class)
public class E4UApplicationTest {

    @Autowired
    private UserService userService;


//    @Test
//    public void saveUser() {
//        userService.save(userService.newUser());
//
//        assertEquals("testemail", userService.findByEmail("testemail").getEmail());
//        assertEquals("testname", userService.findByEmail("testemail").getName());
//    }
}

