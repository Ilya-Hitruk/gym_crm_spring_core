package com.hitruk.gym.crm.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeSummary {
    private String username;
    private String firstName;
    private String lastName;
}
