package com.redshifttech.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String newPassword;

    @NotBlank(message = "Please enter OTP to reset password")
    private String otp;
}
