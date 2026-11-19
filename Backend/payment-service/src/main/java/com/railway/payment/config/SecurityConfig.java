package com.railway.payment.config;

import java.util.Base64;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Bean
    public JwtDecoder jwtDecoder() {

        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);

        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(
                        org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(jwtIssuer));

        return decoder;
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken>
            jwtAuthenticationConverter() {

        return jwt -> {

            String role = jwt.getClaimAsString("role");

            List<SimpleGrantedAuthority> authorities =
                    role == null
                    ? List.of()
                    : List.of(new SimpleGrantedAuthority(role));

            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/payments/**")
                    .hasAuthority("ROLE_PASSENGER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(
                            jwtAuthenticationConverter())
                )
            );

        return http.build();
    }
}