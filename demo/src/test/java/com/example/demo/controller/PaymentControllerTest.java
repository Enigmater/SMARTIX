package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.model.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserRepository userRepository;

    private MyUser testUser;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new MyUser();
        testUser.setLogin("testuser");
        testUser.setPasswordHash("testpassword");
        testUser.setBalance(new BigDecimal(100));

        testPayment = new Payment();
        testPayment.setPhoneNumber("1234567890");
        testPayment.setAmount(new BigDecimal(50));
        testPayment.setUser(testUser);
        testPayment.setDate(java.time.LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testMakePayment_Success() throws Exception {
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(testUser));
        when(paymentService.makePayment(anyString(), anyString(), any())).thenReturn(testPayment);

        mockMvc.perform(post("/payments/pay")
                        .param("login", testUser.getLogin())
                        .param("phoneNumber", "1234567890")
                        .param("amount", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.user.login").value(testUser.getLogin()));

        verify(paymentService, times(1)).makePayment(anyString(), anyString(), any());
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testMakePayment_UserNotFound() throws Exception {
        when(userRepository.findByLogin("nonexistentuser")).thenReturn(Optional.empty());
        when(paymentService.makePayment(anyString(), anyString(), any())).thenThrow(new IllegalArgumentException("Пользователь не найден!"));

        mockMvc.perform(post("/payments/pay")
                        .param("login", "nonexistentuser")
                        .param("phoneNumber", "1234567890")
                        .param("amount", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь не найден!"));

        verify(paymentService, times(1)).makePayment(anyString(), anyString(), any());
    }
    
    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testMakePayment_InsufficientFunds() throws Exception {
        when(paymentService.makePayment(anyString(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("Недостаточно средств на счете!"));

        mockMvc.perform(post("/payments/pay")
                        .param("login", "testuser")
                        .param("phoneNumber", "1234567890")
                        .param("amount", "150.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Недостаточно средств на счете!"));

        verify(paymentService, times(1)).makePayment(anyString(), anyString(), any());
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testGetHistory_Success() throws Exception {
        List<Payment> paymentList = List.of(testPayment);
        when(paymentService.getPayments(anyString(), any())).thenReturn(paymentList);

        mockMvc.perform(get("/payments/history")
                        .param("login", "testuser")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].user.login").value(testUser.getLogin()));

        verify(paymentService, times(1)).getPayments(anyString(), any());
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testGetHistory_UserNotFound() throws Exception {
        when(paymentService.getPayments(anyString(), any()))
                .thenThrow(new IllegalArgumentException("Пользователь с логином nonexistentuser не найден."));

        mockMvc.perform(get("/payments/history")
                        .param("login", "nonexistentuser")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с логином nonexistentuser не найден."));

        verify(paymentService, times(1)).getPayments(anyString(), any());
    }
}