package com.hitruk.gym.crm.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerTrainingResponse {
    private String name;
    private LocalDate date;
    private String trainingType;
    private Integer duration;
    private String traineeName;
}
