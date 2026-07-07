package com.transfertourist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Application entry point for the Transfer Tourist Ohrid backend.
 *
 * <p>Organized <b>package-by-layer</b> (config, controller, service, repository,
 * entity, dto, mapper, exception, validation, security, util, constants). This
 * is the Milestone 2.1 foundation; domain layers are added from 2.2 onward.
 */
@SpringBootApplication
@EnableAsync
public class TransferTouristApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferTouristApplication.class, args);
    }
}
