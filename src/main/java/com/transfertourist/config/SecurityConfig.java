package com.transfertourist.config;

import com.transfertourist.security.JwtAuthenticationFilter;
import com.transfertourist.security.RestAccessDeniedHandler;
import com.transfertourist.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Stateless JWT security (Milestone 2.4).
 *
 * <p>The endpoint matrix mirrors the frontend service layer: the public site
 * (locations, vehicles, prices, route estimates, testimonials, settings, and the
 * customer-facing {@code POST /bookings} and {@code POST /contact}) is open,
 * while everything else — {@code /admin/**}, statistics, the booking admin list
 * and status changes, and every resource mutation (POST/PUT/DELETE/PATCH) — is
 * {@code ROLE_ADMIN}. Because unmatched requests default to authenticated-admin,
 * the Milestone 2.5 admin endpoints are protected as soon as they are added.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        // Public reads
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/ping",
                                "/api/v1/locations",
                                "/api/v1/locations/grouped",
                                "/api/v1/vehicles",
                                "/api/v1/transfer-prices",
                                "/api/v1/routes/estimate",
                                "/api/v1/testimonials",
                                "/api/v1/settings/pricing").permitAll()
                        // Public customer submissions
                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings", "/api/v1/contact").permitAll()
                        // Docs + health
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                        // Allow CORS preflight for any route
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Everything else (admin CRUD, statistics, booking admin) requires ROLE_ADMIN
                        .anyRequest().hasRole("ADMIN"))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
