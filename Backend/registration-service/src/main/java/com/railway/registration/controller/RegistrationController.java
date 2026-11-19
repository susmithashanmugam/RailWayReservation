
package com.railway.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.railway.registration.dto.RegistrationRequest;
import com.railway.registration.dto.RegistrationResponse;
import com.railway.registration.dto.CreateStaffRequest;
import com.railway.registration.service.RegistrationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    // Passenger Registration
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerPassenger(
            @Valid @RequestBody RegistrationRequest request) {

        RegistrationResponse response =
                registrationService.registerPassenger(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Admin creates Staff
    @PostMapping("/admin/staff")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        return registrationService.createStaff(request);
    }
}
