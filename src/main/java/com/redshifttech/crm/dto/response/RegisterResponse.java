package com.redshifttech.crm.dto.response;

import com.redshifttech.crm.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNumber;

    private String address;

    private UserRole role;

    private Boolean active;

    private String message;
}
