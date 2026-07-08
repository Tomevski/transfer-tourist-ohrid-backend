package com.transfertourist.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates human-friendly booking reference codes of the form {@code TT-XXXXXX}
 * (6 upper-case alphanumerics), matching the frontend mock format. Uniqueness is
 * enforced by the caller against the persisted bookings.
 */
@Component
public class ReferenceCodeGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder("TT-");
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
