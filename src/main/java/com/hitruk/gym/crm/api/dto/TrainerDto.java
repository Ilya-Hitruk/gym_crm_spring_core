package com.hitruk.gym.crm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private UserCredentials credentials;
    private Boolean isActive;
    private String specialization;
}
