package com.redshifttech.crm.service;

import com.redshifttech.crm.dto.request.RegisterRequest;
import com.redshifttech.crm.dto.response.RegisterResponse;
import com.redshifttech.crm.entity.User;
import com.redshifttech.crm.enums.UserRole;
import com.redshifttech.crm.exception.DuplicateEmailException;
import com.redshifttech.crm.repository.OtpRepository;
import com.redshifttech.crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpRepository otpRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, emailService, otpRepository, passwordEncoder);
    }

    @Test
    void registerSavesEncryptedUserAndSendsSuccessEmail() {
        RegisterRequest request = createRequest();
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42L);
            return user;
        });

        RegisterResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("user@example.com", savedUser.getEmail());
        assertNotEquals("Password123!", savedUser.getPassword());
        assertTrue(passwordEncoder.matches("Password123!", savedUser.getPassword()));
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertEquals(42L, response.getUserId());
        assertEquals("User registered successfully", response.getMessage());
        verify(emailService).sendRegistrationSuccessEmail("user@example.com", "Sudha", UserRole.ADMIN);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = createRequest();
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> authService.register(request)
        );

        assertEquals("An account already exists for this email address", exception.getMessage());
        verify(userRepository, never()).saveAndFlush(any(User.class));
        verify(emailService, never()).sendRegistrationSuccessEmail(any(), any(), any());
    }

    private RegisterRequest createRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName(" Sudha ");
        request.setLastName(" Testing ");
        request.setEmail(" User@Example.com ");
        request.setMobileNumber("1234567890");
        request.setAddress(" Texas ");
        request.setPassword("Password123!");
        request.setRole(UserRole.ADMIN);
        return request;
    }
}
