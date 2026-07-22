package com.transfertourist.security;

import com.transfertourist.exception.TooManyRequestsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory, per-client login throttle (Milestone 2.7 hardening). Counts failed
 * login attempts per key (client IP) within a rolling window; once they reach the
 * configured maximum the key is locked out for a cooldown, and further attempts
 * are rejected with 429 <em>before</em> any credential check runs. A successful
 * login clears the key.
 *
 * <p>State lives in this single process — fine for the single-instance MVP; a
 * clustered deployment would move it to a shared store (Redis). Behind a reverse
 * proxy, set {@code server.forward-headers-strategy} so the throttle keys on the
 * real client IP rather than the proxy's.
 */
@Component
public class LoginRateLimiter {

    private final int maxAttempts;
    private final long windowSeconds;
    private final long lockoutSeconds;

    private final Map<String, Attempts> byKey = new ConcurrentHashMap<>();

    public LoginRateLimiter(
            @Value("${app.security.login.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login.window-seconds:900}") long windowSeconds,
            @Value("${app.security.login.lockout-seconds:900}") long lockoutSeconds) {
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
        this.lockoutSeconds = lockoutSeconds;
    }

    /** Rejects with 429 if the key is currently locked out. Call before authenticating. */
    public void checkAllowed(String key) {
        Attempts a = byKey.get(key);
        if (a == null) {
            return;
        }
        Instant now = Instant.now();
        if (a.lockedUntil != null && now.isBefore(a.lockedUntil)) {
            throw new TooManyRequestsException(
                    "Too many failed login attempts. Please try again later.");
        }
        // Opportunistically drop fully-expired entries so the map doesn't grow unbounded.
        if (now.isAfter(a.windowStart.plusSeconds(windowSeconds))) {
            byKey.remove(key, a);
        }
    }

    /** Records a failed attempt, locking the key out once the max is reached. */
    public void recordFailure(String key) {
        Instant now = Instant.now();
        byKey.compute(key, (k, a) -> {
            if (a == null || now.isAfter(a.windowStart.plusSeconds(windowSeconds))) {
                a = new Attempts(now); // start a fresh window
            }
            a.count++;
            if (a.count >= maxAttempts) {
                a.lockedUntil = now.plusSeconds(lockoutSeconds);
            }
            return a;
        });
    }

    /** Clears all throttle state for the key after a successful login. */
    public void recordSuccess(String key) {
        byKey.remove(key);
    }

    /** Mutated only inside {@link ConcurrentHashMap#compute}; fields volatile for reader visibility. */
    private static final class Attempts {
        private final Instant windowStart;
        private volatile int count;
        private volatile Instant lockedUntil;

        private Attempts(Instant windowStart) {
            this.windowStart = windowStart;
        }
    }
}
