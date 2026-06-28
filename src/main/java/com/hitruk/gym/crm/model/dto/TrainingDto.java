package com.hitruk.gym.crm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingDto {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private String trainingType;
    private LocalDateTime date;
    private Duration duration;
}
