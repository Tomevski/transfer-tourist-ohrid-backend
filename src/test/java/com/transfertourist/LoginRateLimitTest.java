package com.transfertourist;

import com.transfertourist.security.LoginRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the per-client login throttle (Milestone 2.7): after the configured
 * max failed attempts (default 5) from one IP the endpoint returns 429, and a
 * successful login clears the counter. MockMvc reports a fixed remote address
 * ({@code 127.0.0.1}), which is cleared before each test so the classes' shared
 * limiter singleton doesn't leak state between tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginRateLimitTest {

    private static final String CLIENT_IP = "127.0.0.1";
    private static final String WRONG =
            "{\"email\":\"admin@transfertourist.com\",\"password\":\"wrong-password\"}";
    private static final String CORRECT =
            "{\"email\":\"admin@transfertourist.com\",\"password\":\"admin123\"}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LoginRateLimiter rateLimiter;

    @BeforeEach
    void clearThrottle() {
        rateLimiter.recordSuccess(CLIENT_IP);
    }

    @Test
    void locksOutAfterFiveFailedAttempts() throws Exception {
        failWrongLogins(5);
        // Sixth attempt is rejected before any credential check.
        mvc.perform(post("/api/v1/auth/login").contentType(APPLICATION_JSON).content(WRONG))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void lockoutIgnoresCorrectPasswordOnceTripped() throws Exception {
        failWrongLogins(5);
        // Even valid credentials are refused while the IP is locked out.
        mvc.perform(post("/api/v1/auth/login").contentType(APPLICATION_JSON).content(CORRECT))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void successfulLoginResetsTheCounter() throws Exception {
        failWrongLogins(4); // below the threshold
        // A success clears the count, so the next failure is a plain 401, not a lockout.
        mvc.perform(post("/api/v1/auth/login").contentType(APPLICATION_JSON).content(CORRECT))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/login").contentType(APPLICATION_JSON).content(WRONG))
                .andExpect(status().isUnauthorized());
    }

    /** Submits {@code count} wrong-password logins, each expected to be a plain 401. */
    private void failWrongLogins(int count) throws Exception {
        for (int attempt = 0; attempt < count; attempt++) {
            mvc.perform(post("/api/v1/auth/login").contentType(APPLICATION_JSON).content(WRONG))
                    .andExpect(status().isUnauthorized());
        }
    }
}
