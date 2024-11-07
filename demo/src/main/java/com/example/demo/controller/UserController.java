package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.MyUser;
import com.example.demo.service.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;



@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public MyUser register(@RequestParam String login, @RequestParam String passwordHash) {
        return userService.registerUser(login, passwordHash);
    }

    @GetMapping("/balance")
    public String getBalance(@RequestParam String login) {
        Optional<MyUser> userOpt = userService.getUserByLogin(login);
        return userOpt.map(user -> "Баланс пользователя " + user.getLogin() + ": " + user.getBalance())
                .orElse("Пользователь не найден");
    }

    @PatchMapping("/update")
    public MyUser partialUpdateUserData(@RequestParam String login,
                                        @RequestParam(required = false) String fullName,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) String birthDate) {
        return userService.partialUpdateUserData(login, fullName, email, gender, birthDate);
    }
    
}