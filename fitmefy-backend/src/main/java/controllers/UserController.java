package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import services.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UUID setUserInfo(@RequestParam String name, @RequestParam String email) {
        return userService.setUserInfoInDB(name, email);
    }
}
