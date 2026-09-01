package com.hitruk.gym.crm.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateRequest {
    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
