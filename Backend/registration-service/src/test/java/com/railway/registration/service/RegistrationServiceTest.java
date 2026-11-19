
package com.railway.registration.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.railway.registration.dto.RegistrationRequest;
import com.railway.registration.dto.RegistrationResponse;
import com.railway.registration.entity.Role;
import com.railway.registration.entity.User;
import com.railway.registration.exception.DuplicateResourceException;
import com.railway.registration.exception.ResourceNotFoundException;
import com.railway.registration.repository.RoleRepository;
import com.railway.registration.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationRequest request;
    private Role passengerRole;

    @BeforeEach
    void setUp() {

        request = new RegistrationRequest();

        request.setFullName("Susmitha");
        request.setEmail("susmitha@gmail.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("Railway@123");

        passengerRole = new Role();
        passengerRole.setId(2L);
        passengerRole.setName("ROLE_PASSENGER");
    }

    // Test 1: Successful passenger registration
    @Test
    void registerPassenger_Success() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(
                request.getPhoneNumber()))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_PASSENGER"))
                .thenReturn(Optional.of(passengerRole));

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        RegistrationResponse response =
                registrationService.registerPassenger(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("Susmitha", response.getFullName());
        assertEquals("susmitha@gmail.com", response.getEmail());
        assertEquals("ROLE_PASSENGER", response.getRole());
        assertEquals("Registration successful",
                response.getMessage());

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("hashedPassword", savedUser.getPassword());
        assertEquals(passengerRole, savedUser.getRole());
    }

    // Test 2: Duplicate email
    @Test
    void registerPassenger_DuplicateEmail() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> registrationService.registerPassenger(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository);
    }

    // Test 3: Duplicate phone number
    @Test
    void registerPassenger_DuplicatePhone() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(
                request.getPhoneNumber()))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> registrationService.registerPassenger(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    // Test 4: Passenger role not found
    @Test
    void registerPassenger_RoleNotFound() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(
                request.getPhoneNumber()))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_PASSENGER"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> registrationService.registerPassenger(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }
}
