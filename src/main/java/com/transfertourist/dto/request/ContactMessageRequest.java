package com.transfertourist.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payload for the Contact Us form (mirrors the frontend {@code ContactMessageInput}). */
public record ContactMessageRequest(
        @NotBlank(message = "Enter your name") @Size(max = 120) String name,
        @NotBlank(message = "Enter your surname") @Size(max = 120) String surname,
        @NotBlank(message = "Enter your email") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Enter a message") @Size(max = 2000) String message
) {
}
