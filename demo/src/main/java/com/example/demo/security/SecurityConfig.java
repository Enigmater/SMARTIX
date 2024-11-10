package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private LoginConsistencyFilter loginConsistencyFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/users/register").permitAll()
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Unauthorized");
                }))
            .userDetailsService(customUserDetailService);
        http.addFilterBefore(loginConsistencyFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

