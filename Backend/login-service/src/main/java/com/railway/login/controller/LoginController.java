
package com.railway.login.controller;

import com.railway.login.dto.LoginRequest;
import com.railway.login.dto.LoginResponse;
import com.railway.login.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(
        org.springframework.security.core.Authentication authentication) {

    String role = authentication.getAuthorities()
            .stream()
            .map(authority -> authority.getAuthority())
            .findFirst()
            .orElse("UNKNOWN");

    return ResponseEntity.ok(
            java.util.Map.of(
                    "email", authentication.getName(),
                    "role", role
            )
    );
}

}
