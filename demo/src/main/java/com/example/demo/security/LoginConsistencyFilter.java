package com.example.demo.security;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
@Component
public class LoginConsistencyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String queryLogin = httpRequest.getParameter("login");
        String authHeader = httpRequest.getHeader("Authorization");
        String headerLogin = null;

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            headerLogin = credentials.split(":")[0];
        }

        if (queryLogin != null && headerLogin != null && !queryLogin.equals(headerLogin)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login mismatch between query and Authorization header");
            return;
        }

        chain.doFilter(request, response);

    }
    
}
