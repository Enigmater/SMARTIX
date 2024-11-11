package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter @Setter
@NoArgsConstructor
public class MyUser {

    public MyUser(UserDTO userDTO) {
        login = userDTO.getLogin();
        passwordHash = userDTO.getPasswordHash();
        balance = userDTO.getBalance();
        fullName = userDTO.getFullName();
        email = userDTO.getEmail();
        gender = userDTO.getGender();
        birthDate = userDTO.getBirthDate();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(length = 100)
    private String fullName;

    @Column(unique = true, length = 150)
    private String email;
    private Character gender;

    private LocalDate birthDate;

    // By default loading is lazy
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Payment> payments;
}