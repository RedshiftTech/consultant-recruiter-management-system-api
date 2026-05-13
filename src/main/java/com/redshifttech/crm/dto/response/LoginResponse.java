package com.redshifttech.crm.dto.response;

import com.redshifttech.crm.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private UserRole role;

    private String redirectUrl;

    private String message;
}