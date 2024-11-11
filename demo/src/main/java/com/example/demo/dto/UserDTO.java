package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.model.MyUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    public UserDTO(MyUser user) {
        login = user.getLogin();
        passwordHash = user.getPasswordHash();
        balance = user.getBalance();
        email = user.getEmail();
        fullName = user.getFullName();
        gender = user.getGender();
        birthDate = user.getBirthDate();
    }

    @Size(min = 3, max = 50, message = "Login должен содержать от 3 до 50 символов")
    private String login;

    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String passwordHash;

    private BigDecimal balance;
    @Email(message = "Email должен быть валидным")
    private String email;

    @Size(max = 100, message = "ФИО содержит не более 100 символов")
    private String fullName;

    private Character gender;


    private LocalDate birthDate;
}
