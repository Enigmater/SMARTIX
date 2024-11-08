package com.example.demo.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = userRepository.findByLogin(username)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));
        return new User(myUser.getLogin(), "{noop}" + myUser.getPasswordHash(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        // Вместо {noop} нужно использовать PasswordEncoder с необходимым типом хэширования
    }
}
