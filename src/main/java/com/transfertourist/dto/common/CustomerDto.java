package com.transfertourist.dto.common;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Customer contact details, shared by the booking request and response. The
 * constraints apply on the inbound request (they are inert on the response) and
 * mirror the frontend customer schema.
 */
public record CustomerDto(
        @NotBlank(message = "Enter your first name") String firstName,
        @NotBlank(message = "Enter your last name") String lastName,
        @NotBlank(message = "Enter your email") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Enter your phone number")
        @Size(min = 6, message = "Enter a valid phone number") String phone
) {
}
