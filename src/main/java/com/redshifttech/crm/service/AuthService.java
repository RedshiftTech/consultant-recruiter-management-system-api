package com.redshifttech.crm.service;


import com.redshifttech.crm.dto.request.ForgotPasswordRequest;
import com.redshifttech.crm.dto.request.LoginRequest;
import com.redshifttech.crm.dto.request.OtpRequest;
import com.redshifttech.crm.dto.request.RegisterRequest;
import com.redshifttech.crm.dto.response.ForgotPasswordResponse;
import com.redshifttech.crm.dto.response.LoginResponse;
import com.redshifttech.crm.dto.response.OtpResponse;
import com.redshifttech.crm.dto.response.RegisterResponse;
import com.redshifttech.crm.entity.Otp;
import com.redshifttech.crm.entity.User;
import com.redshifttech.crm.enums.UserRole;
import com.redshifttech.crm.repository.OtpRepository;
import com.redshifttech.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();
        User savedUser = userRepository.save(user);
        emailService.sendRegistrationSuccessEmail(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getRole()
        );
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .mobileNumber(savedUser.getMobileNumber())
                .address(savedUser.getAddress())
                .role(savedUser.getRole())
                .active(savedUser.getActive())
                .message("User registered successfully")
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!user.getActive()) {
            throw new RuntimeException("User account is inactive. Please contact Admin.");
        }
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String redirectUrl = getRedirectUrlByRole(user.getRole());
        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .redirectUrl(redirectUrl)
                .message("Login successful")
                .build();
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        Otp otp = otpRepository
                .findTopByEmailAndOtpAndUsedFalseOrderByIdDesc(
                        request.getEmail(),
                        request.getOtp()
                )
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Please request a new OTP.");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));
        if (!user.getActive()) {
            throw new RuntimeException("User account is inactive. Please contact Admin.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        User updatedUser = userRepository.save(user);
        otp.setUsed(true);
        otpRepository.save(otp);
        return ForgotPasswordResponse.builder()
                .email(updatedUser.getEmail())
                .message("Password reset successfully")
                .build();
    }


    public OtpResponse otp(OtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));
        if (!user.getActive()) {
            throw new RuntimeException("User account is inactive. Please contact Admin.");
        }
        List<Otp> oldOtps = otpRepository.findByEmailAndUsedFalse(request.getEmail());
        for (Otp oldOtp : oldOtps) {
            oldOtp.setUsed(true);
        }
        otpRepository.saveAll(oldOtps);
        // Generate new OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        Otp forgotPasswordOtp = Otp.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        otpRepository.save(forgotPasswordOtp);
        emailService.sendForgotPasswordOtpEmail(user.getEmail(), otp);
        return OtpResponse.builder()
                .message("OTP sent successfully to your registered email")
                .build();
    }

    private String getRedirectUrlByRole(UserRole role) {
        if (role == UserRole.ADMIN) {
            return "/admin/dashboard";
        }
        if (role == UserRole.MANAGER) {
            return "/manager/dashboard";
        }
        if (role == UserRole.RECRUITER) {
            return "/recruiter/dashboard";
        }
        if (role == UserRole.HR_OPERATIONS) {
            return "/hr/consultants";
        }
        throw new RuntimeException("Invalid user role");
    }
}
