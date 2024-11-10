package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Payment;
import com.example.demo.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public Payment makePayment(@RequestParam String login, @RequestParam String phoneNumber, @RequestParam BigDecimal amount) {  
        return paymentService.makePayment(login, phoneNumber, amount);
    }

    @GetMapping("/history")
    public List<Payment> getHistory(@RequestParam String login, @RequestParam int page, @RequestParam int size) {
        return paymentService.getPayments(login, PageRequest.of(page, size));
    }
}
