package com.redshifttech.crm.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ForgotPasswordResponse {

    private String email;
    private String message;


}

