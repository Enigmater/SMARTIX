package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.demo.model.MyUser;
import com.example.demo.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private MyUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new MyUser();
        testUser.setLogin("testuser");
        testUser.setPasswordHash("password123");
        testUser.setBalance(new BigDecimal(1000));
        testUser.setFullName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setGender('M');
        testUser.setBirthDate(LocalDate.parse("2000-01-01"));
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.registerUser(anyString(), anyString())).thenReturn(testUser);

        mockMvc.perform(post("/users/register")
                        .param("login", "testuser")
                        .param("passwordHash", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testuser"))
                .andExpect(jsonPath("$.balance").value(1000)); 
    }

    @Test
    void testRegisterUser_Failure() throws Exception {
        mockMvc.perform(post("/users/register")
                        .param("login", "")
                        .param("passwordHash", "password123"));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testGetBalance_UserFound() throws Exception {
        when(userService.getUserByLogin("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/balance")
                        .param("login", "testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс пользователя testuser: " + testUser.getBalance()));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testGetBalance_UserNotFound() throws Exception {
        when(userService.getUserByLogin("nonexistentuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/balance")
                        .param("login", "nonexistentuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь не найден"));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testPartialUpdateUserData_FullNameUpdated() throws Exception {
        MyUser updataedUser = testUser;
        updataedUser.setFullName("Updated Name");
        when(userService.partialUpdateUserData("testuser", "Updated Name", null, null, null))
                .thenReturn(updataedUser);

        mockMvc.perform(patch("/users/update")
                        .param("login", "testuser")
                        .param("fullName", "Updated Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.birthDate").value("2000-01-01"));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testPartialUpdateUserData_EmailUpdated() throws Exception {
        MyUser updataedUser = testUser;
        updataedUser.setEmail("updated@example.com");
        when(userService.partialUpdateUserData("testuser", null, "updated@example.com", null, null))
                .thenReturn(updataedUser);

        mockMvc.perform(patch("/users/update")
                        .param("login", "testuser")
                        .param("email", "updated@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.birthDate").value("2000-01-01"));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testPartialUpdateUserData_UserNotFound() throws Exception {
        when(userService.partialUpdateUserData(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Пользователь не найден"));

        mockMvc.perform(patch("/users/update")
                        .param("login", "nonexistentuser")
                        .param("fullName", "Updated Name")
                        .param("email", "updated@example.com")
                        .param("gender", "F")
                        .param("birthDate", "1990-12-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }
}
