package com.transfertourist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI metadata. The interactive docs are served at
 * {@code /swagger-ui.html} and the raw spec at {@code /v3/api-docs}.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transferTouristOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transfer Tourist Ohrid API")
                        .description("Booking platform for private car & bus transfers around Ohrid, North Macedonia.")
                        .version("v1")
                        .contact(new Contact().name("Transfer Tourist"))
                        .license(new License().name("Proprietary")));
    }
}
