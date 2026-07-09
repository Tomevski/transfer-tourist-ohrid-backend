package com.transfertourist.config;

import com.transfertourist.constants.Role;
import com.transfertourist.entity.User;
import com.transfertourist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Seeds the single administrator account at startup if it does not already
 * exist. The password is hashed with the configured {@link PasswordEncoder}
 * rather than embedded in a migration, so the bcrypt work factor and hash are
 * produced by the app. Credentials default to the Phase 1 mock admin for local
 * dev and are overridable via {@code app.admin.*} (env) in production.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String ADMIN_ID = "usr-admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String name;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.admin.email}") String email,
                       @Value("${app.admin.password}") String password,
                       @Value("${app.admin.name}") String name) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.debug("Admin account {} already present; skipping seed.", email);
            return;
        }

        User admin = new User();
        admin.setId(ADMIN_ID);
        admin.setEmail(email);
        admin.setName(name);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(Instant.now());
        userRepository.save(admin);

        log.info("Seeded admin account {}", email);
    }
}
