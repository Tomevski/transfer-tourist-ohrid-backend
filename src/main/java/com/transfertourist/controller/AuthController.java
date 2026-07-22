package com.transfertourist.controller;

import com.transfertourist.dto.request.LoginRequest;
import com.transfertourist.dto.response.AuthResponse;
import com.transfertourist.security.LoginRateLimiter;
import com.transfertourist.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin authentication. Issues a JWT for valid credentials, throttled per client IP. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter loginRateLimiter) {
        this.authService = authService;
        this.loginRateLimiter = loginRateLimiter;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        String clientKey = http.getRemoteAddr();
        loginRateLimiter.checkAllowed(clientKey); // 429 if locked out — before any credential check

        try {
            AuthResponse response = authService.login(request);
            loginRateLimiter.recordSuccess(clientKey);
            return response;
        } catch (AuthenticationException ex) {
            loginRateLimiter.recordFailure(clientKey);
            throw ex; // handled globally as 401
        }
    }
}
