package com.transfertourist.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Admin login payload (mirrors the frontend {@code LoginInput}). */
public record LoginRequest(
        @NotBlank(message = "Enter your email") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Enter your password") String password
) {
}
