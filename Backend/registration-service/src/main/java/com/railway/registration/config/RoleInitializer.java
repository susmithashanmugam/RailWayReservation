package com.railway.registration.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.railway.registration.entity.Role;
import com.railway.registration.entity.User;
import com.railway.registration.repository.RoleRepository;
import com.railway.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean adminBootstrapEnabled;

    @Value("${app.bootstrap.admin.full-name:}")
    private String adminFullName;

    @Value("${app.bootstrap.admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.phone:}")
    private String adminPhone;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // Create all required roles if they do not exist
        List<String> roles = List.of(
                "ROLE_ADMIN",
                "ROLE_PASSENGER",
                "ROLE_STAFF"
        );

        for (String roleName : roles) {

            if (roleRepository.findByName(roleName).isEmpty()) {

                Role role = new Role();
                role.setName(roleName);

                roleRepository.save(role);

                log.info("Created role: {}", roleName);
            }
        }

        log.info("Role initialization completed.");

        // Create Admin only when explicitly enabled
        if (adminBootstrapEnabled) {
            createInitialAdmin();
        }
    }

    private void createInitialAdmin() {

        if (isBlank(adminFullName)
                || isBlank(adminEmail)
                || isBlank(adminPhone)
                || isBlank(adminPassword)) {

            throw new IllegalStateException(
                    "Admin bootstrap is enabled, but one or more "
                    + "Admin credentials are missing."
            );
        }

        String email = adminEmail.trim().toLowerCase();
        String phone = adminPhone.trim();

        // Check whether an account already exists
        var existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {

            if ("ROLE_ADMIN".equals(
                    existingUser.get().getRole().getName())) {

                log.info("Initial Admin account already exists.");
                return;
            }

            throw new IllegalStateException(
                    "The configured Admin email already belongs "
                    + "to a non-admin account."
            );
        }

        // Prevent duplicate phone numbers
        if (userRepository.existsByPhoneNumber(phone)) {

            throw new IllegalStateException(
                    "The configured Admin phone number is already registered."
            );
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN was not initialized."
                ));

        User admin = new User();

        admin.setFullName(adminFullName.trim());
        admin.setEmail(email);
        admin.setPhoneNumber(phone);

        // Always store an encrypted password
        admin.setPassword(passwordEncoder.encode(adminPassword));

        admin.setRole(adminRole);

        userRepository.save(admin);

        log.info("Initial Admin account created successfully.");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

