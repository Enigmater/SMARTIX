package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.MyUser;
import com.example.demo.model.Payment;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public Payment makePayment(String login, String phoneNumber, BigDecimal amount) {
        Optional<MyUser> optionalUser = userRepository.findByLogin(login);
        if (!optionalUser.isPresent()) throw new IllegalArgumentException("Пользователь не найден!");
        MyUser user = optionalUser.get();
        if (user.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Недостаточно средств на счете!");
        user.setBalance(user.getBalance().subtract(amount));
        Payment payment = new Payment();
        payment.setPhoneNumber(phoneNumber);
        payment.setAmount(amount); 
        payment.setDateTime(java.time.LocalDateTime.now());
        payment.setUser(user);

        paymentRepository.save(payment);
        userRepository.save(user);

        return payment;
    }

    public List<Payment> getPayments(String login, Pageable pageable) {
        Optional<MyUser> optionalUser = userRepository.findByLogin(login);
    if (optionalUser.isPresent()) {
        MyUser user = optionalUser.get();
        return paymentRepository.findByUserId(user.getId(), pageable).getContent();
    } else {
        throw new IllegalArgumentException("Пользователь с логином " + login + " не найден.");
    }
    }
}