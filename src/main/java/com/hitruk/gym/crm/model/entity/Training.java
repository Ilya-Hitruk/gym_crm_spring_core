package com.hitruk.gym.crm.model.entity;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Training implements BaseEntity<Long> {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private LocalDateTime date;
    private Duration duration;
}
