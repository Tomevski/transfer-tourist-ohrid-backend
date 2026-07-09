package com.transfertourist.dto.response;

/**
 * Login result (mirrors the frontend {@code AuthResult}): a bearer token plus
 * the display identity the SPA stores for the admin session.
 *
 * @param token JWT to send as {@code Authorization: Bearer <token>}
 * @param admin the authenticated admin's public identity
 */
public record AuthResponse(String token, Admin admin) {

    /** Mirrors the frontend {@code AdminUser} ({ email, name }). */
    public record Admin(String email, String name) {
    }
}
