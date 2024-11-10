package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @NotNull(message = "Login не может быть null")
    @Size(min = 3, max = 50, message = "Login должен содержать от 3 до 20 символов")
    private String login;

    @NotNull(message = "Пароль не может быть null")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String passwordHash;

    @Email(message = "Email должен быть валидным")
    private String email;

    private String fullName;

    private Character gender;

    private LocalDate birthDate;
}
