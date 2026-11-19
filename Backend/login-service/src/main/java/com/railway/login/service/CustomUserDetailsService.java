
package com.railway.login.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.railway.login.client.RegistrationServiceClient;
import com.railway.login.dto.InternalUserAuthResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final RegistrationServiceClient registrationServiceClient;

    @Value("${internal.auth.api-key}")
    private String internalApiKey;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        log.info("Loading user details for authentication");

        try {

            InternalUserAuthResponse user =
                    registrationServiceClient.getUserForAuthentication(
                            email,
                            internalApiKey
                    );

            return User.withUsername(user.getEmail())
                    .password(user.getPasswordHash())
                    .authorities(user.getRole())
                    .build();

        } catch (Exception ex) {

            log.error("Unable to load user details: ", ex);

            throw new UsernameNotFoundException(
                    "Invalid email or password"
            );
        }
    }
}
