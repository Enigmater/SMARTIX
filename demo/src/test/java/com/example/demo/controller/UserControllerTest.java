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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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

import com.example.demo.dto.UserDTO;
import com.example.demo.model.MyUser;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private MyUser testUser;

    private final String apiRegister = "/users/register";
    private final String apiBalance = "/users/balance";
    private final String apiUpdate = "/users/update";

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

        mockMvc.perform(post(apiRegister)
                        .param("login", "testuser")
                        .param("passwordHash", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testuser"))
                .andExpect(jsonPath("$.balance").value(1000)); 
    }

    @Test
    void testRegisterUser_Failure() throws Exception {
        mockMvc.perform(post(apiRegister)
                        .param("login", "")
                        .param("passwordHash", "password123"));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testGetBalance_UserFound() throws Exception {
        when(userService.getUserByLogin("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get(apiBalance)
                        .param("login", "testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс пользователя testuser: " + testUser.getBalance()));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testGetBalance_UserNotFound() throws Exception {
        when(userService.getUserByLogin("nonexistentuser")).thenReturn(Optional.empty());

        mockMvc.perform(get(apiBalance)
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

        mockMvc.perform(patch(apiUpdate)
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

        mockMvc.perform(patch(apiUpdate)
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
    void testPartialUpdateUserData_EmailUpdated1() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("testuser");
        userDTO.setEmail("old@example.com");

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setLogin("testuser");
        updatedUserDTO.setEmail("new@example.com");

        when(userService.partialUpdateUserData(userDTO)).thenReturn(updatedUserDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(patch("/users/update1")
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content(jsonRequest))        
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testPartialUpdateUserData_LoginMissing() throws Exception {
        UserDTO invalidUserDTO = new UserDTO();
        invalidUserDTO.setEmail("updated@example.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(invalidUserDTO);

        mockMvc.perform(patch("/users/update1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());  
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void testPartialUpdateUserData_UserNotFound() throws Exception {
        when(userService.partialUpdateUserData(any())).thenThrow(new IllegalArgumentException("Пользователь не найден"));

        UserDTO userDTO = new UserDTO(testUser);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modulesToInstall(new JavaTimeModule()).build();
        String jsonRequest = objectMapper.writeValueAsString(userDTO);

        userDTO.setLogin("nonexistentuser");
        mockMvc.perform(patch("/users/update1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }
}
