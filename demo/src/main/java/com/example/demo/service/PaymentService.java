package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;
import com.example.demo.model.Payment;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public Payment makePayment(String login, String phoneNumber, double amount) {
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getBalance() >= amount) {
                user.setBalance(user.getBalance() - amount);
                Payment payment = new Payment();
                payment.setPhoneNumber(phoneNumber);
                payment.setAmount(amount); 
                payment.setDate(java.time.LocalDateTime.now());
                payment.setUser(user);

                paymentRepository.save(payment);
                userRepository.save(user);

                return payment;
            }
            else throw new IllegalArgumentException("Недостаточно средств на счете!");
        }
        else throw new IllegalArgumentException("Пользователь не найден!");
    }

    public Page<Payment> getPayments(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable);
    }
}