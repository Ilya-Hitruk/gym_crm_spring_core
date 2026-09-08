package com.hitruk.gym.workload.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerWorkloadRequest {

    @NotNull
    private String trainerUsername;

    @NotNull
    private String trainerFirstName;

    @NotNull
    private String trainerLastName;

    @NotNull
    private Boolean isActive;

    @NotNull
    private LocalDate trainingDate;

    @NotNull
    private Integer trainingDuration;

    @NotNull
    private ActionType actionType;
}
