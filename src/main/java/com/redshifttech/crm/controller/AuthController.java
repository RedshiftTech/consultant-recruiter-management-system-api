package com.redshifttech.crm.controller;

import com.redshifttech.crm.dto.request.ForgotPasswordRequest;
import com.redshifttech.crm.dto.request.LoginRequest;
import com.redshifttech.crm.dto.request.OtpRequest;
import com.redshifttech.crm.dto.request.RegisterRequest;
import com.redshifttech.crm.dto.response.ForgotPasswordResponse;
import com.redshifttech.crm.dto.response.LoginResponse;
import com.redshifttech.crm.dto.response.OtpResponse;
import com.redshifttech.crm.dto.response.RegisterResponse;
import com.redshifttech.crm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173"})
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp")
    public ResponseEntity<OtpResponse> otp(@RequestBody OtpRequest request) {
        OtpResponse response = authService.otp(request);
        return ResponseEntity.ok(response);
    }
}
