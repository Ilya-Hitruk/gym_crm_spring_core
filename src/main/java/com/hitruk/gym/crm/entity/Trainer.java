package com.hitruk.gym.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(exclude = {"trainees", "trainings"})
public class Trainer extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @Builder.Default
    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    private List<Trainee> trainees = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "trainer")
    private List<Training> trainings = new ArrayList<>();
}
