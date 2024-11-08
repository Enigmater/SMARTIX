package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;
import com.example.demo.model.MyUser;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    private final int startBalance = 1000;
    
    public MyUser registerUser(String login, String passwordHash) {
        MyUser user = new MyUser();
        user.setLogin(login);
        user.setPasswordHash(passwordHash);
        user.setBalance(startBalance);
        return userRepository.save(user);
    }

    public Optional<MyUser> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public MyUser partialUpdateUserData(String login, String fullName, String email, String gender, String birthDate) {
        Optional<MyUser> userOpt = userRepository.findByLogin(login);
        if (!userOpt.isPresent()) throw new IllegalArgumentException("Пользователь не найден");
        MyUser user = userOpt.get();

        if (fullName != null) user.setFullName(fullName);
        if (email != null) user.setEmail(email);
        if (gender != null) user.setGender(gender);
        if (birthDate != null) user.setBirthDate(birthDate);

        return userRepository.save(user);          
    }

    
}