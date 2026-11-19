
package com.railway.registration.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.railway.registration.dto.InternalUserAuthResponse;
import com.railway.registration.entity.User;
import com.railway.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final UserRepository userRepository;

    @Value("${internal.auth.api-key}")
    private String internalApiKey;

    @GetMapping("/users/{email}")
    public ResponseEntity<InternalUserAuthResponse> getUserForAuthentication(
            @PathVariable String email,
            @RequestHeader("X-INTERNAL-API-KEY") String apiKey) {

        if (!internalApiKey.equals(apiKey)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid internal service credentials"
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        InternalUserAuthResponse response =
                new InternalUserAuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole().getName()
                );

        return ResponseEntity.ok(response);
    }
}
