package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    private final int startBalance = 1000;
    
    public User registerUser(String login, String passwordHash) {
        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(passwordHash);
        user.setBalance(startBalance);
        return userRepository.save(user);
    }

    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public void updateUserData(User user) {
        userRepository.save(user);
    }
}