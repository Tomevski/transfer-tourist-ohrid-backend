package com.transfertourist.constants;

/**
 * Application role. Only administrators authenticate in v1; the Spring Security
 * {@code ROLE_} prefix is applied in the security layer, not stored here.
 */
public enum Role {
    ADMIN
}
