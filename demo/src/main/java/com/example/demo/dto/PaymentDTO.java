package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    @Size(max = 20, message = "Телефонный номер содержит не более 20 символов")
    private String phoneNumber;
    private BigDecimal amount;
    private LocalDateTime dateTime;
}
