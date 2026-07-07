package com.transfertourist;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: the Spring context loads. Runs against an in-memory H2 datasource
 * (test profile) so it needs no external Postgres.
 */
@SpringBootTest
@ActiveProfiles("test")
class TransferTouristApplicationTests {

    @Test
    void contextLoads() {
    }
}
