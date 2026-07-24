package com.hitruk.gym.crm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingDto {
    private Long id;
    private String traineeUsername;
    private String trainerUsername;
    private String name;
    private String trainingType;
    private LocalDate date;
    private Integer duration;
}
