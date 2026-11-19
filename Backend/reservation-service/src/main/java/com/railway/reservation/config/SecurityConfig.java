
package com.railway.reservation.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    // 1. JWT Decoder
    @Bean
    public JwtDecoder jwtDecoder() {

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(jwtIssuer)
        );

        return decoder;
    }

    // 2. JWT Role Converter
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String role = jwt.getClaimAsString("role");

            if (role == null || role.isBlank()) {
                return List.<GrantedAuthority>of();
            }

            return List.<GrantedAuthority>of(
                    new SimpleGrantedAuthority(role)
            );
        });

        return converter;
    }

    // 3. Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Health check
                .requestMatchers("/actuator/health").permitAll()

                // Internal Payment Service callback
                // Controller validates X-INTERNAL-API-KEY
                .requestMatchers("/internal/**").permitAll()

                // Passenger payment details
                .requestMatchers("/api/reservations/payment/**")
                    .hasAuthority("ROLE_PASSENGER")

                // Staff operations
                .requestMatchers("/api/reservations/staff/**")
                    .hasAuthority("ROLE_STAFF")

                // Passenger reservation operations
                .requestMatchers("/api/reservations/**")
                    .hasAuthority("ROLE_PASSENGER")

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // JWT Authentication
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(
                            jwtAuthenticationConverter()
                    )
                )
            );

        return http.build();
    }
}
