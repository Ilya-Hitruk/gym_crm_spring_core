package com.hitruk.gym.crm.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationResponse {
    private String username;
    private String password;
    private String token;
}
