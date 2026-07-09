package com.transfertourist.service;

import com.transfertourist.dto.request.LoginRequest;
import com.transfertourist.dto.response.AuthResponse;
import com.transfertourist.entity.User;
import com.transfertourist.repository.UserRepository;
import com.transfertourist.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticates the admin and issues a JWT. Credential verification is delegated
 * to the {@link AuthenticationManager} (which runs the bcrypt check via
 * {@code CustomUserDetailsService}); a bad email/password surfaces as a
 * {@code BadCredentialsException} handled globally as 401.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        // Credentials verified above; load the entity for its display name.
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + request.email()));

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, new AuthResponse.Admin(user.getEmail(), user.getName()));
    }
}
