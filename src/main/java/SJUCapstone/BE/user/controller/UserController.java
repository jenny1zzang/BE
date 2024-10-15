package SJUCapstone.BE.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/getUser")
    public ResponseEntity<Void> getUserInfo() {
        return ResponseEntity.ok().build();
    }
}
