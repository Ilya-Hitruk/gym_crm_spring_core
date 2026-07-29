package com.hitruk.gym.crm.api.dto;

import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraineeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private UserCredentials credentials;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
    private List<TrainerSummary> trainers;
}
