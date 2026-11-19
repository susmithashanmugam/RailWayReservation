
package com.railway.login.service;

import com.railway.login.dto.LoginRequest;
import com.railway.login.dto.LoginResponse;
import com.railway.login.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long expiration;

    public LoginResponse login(LoginRequest request) {

        // Step 1: Validate email and password
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        // Step 2: Generate JWT token
        String token = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal()
        );

        // Step 3: Get the authenticated user's role
        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_PASSENGER");

        // Step 4: Return login response
        return new LoginResponse(
                token,
                "Bearer",
                expiration,
                role
        );
    }
}
