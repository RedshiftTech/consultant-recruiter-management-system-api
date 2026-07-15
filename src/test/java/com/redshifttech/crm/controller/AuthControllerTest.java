package com.redshifttech.crm.controller;

import com.redshifttech.crm.exception.DuplicateEmailException;
import com.redshifttech.crm.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void registerReturnsValidationErrorsForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/user/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Testing",
                                  "email": "not-an-email",
                                  "mobileNumber": "123",
                                  "address": "Texas",
                                  "password": "password",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please correct the highlighted fields"))
                .andExpect(jsonPath("$.validationErrors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.validationErrors.email").value("Email should be valid"))
                .andExpect(jsonPath("$.validationErrors.mobileNumber").value("Mobile number must be 10 digits"));
    }

    @Test
    void registerReturnsConflictForDuplicateEmail() throws Exception {
        when(authService.register(any())).thenThrow(
                new DuplicateEmailException("An account already exists for this email address")
        );

        mockMvc.perform(post("/api/user/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "Sudha",
                                  "lastName": "Testing",
                                  "email": "user@example.com",
                                  "mobileNumber": "1234567890",
                                  "address": "Texas",
                                  "password": "password",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("An account already exists for this email address"));
    }
}
