package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.MyUser;

public interface UserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByLogin(String login);
} 
