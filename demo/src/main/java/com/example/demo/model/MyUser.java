package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter @Setter
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(length = 150)
    private String fullName;

    @Column(unique = true, length = 50)
    private String email;
    private Character gender;

    private LocalDate birthDate;

    // By default loading is lazy
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Payment> payments;
}