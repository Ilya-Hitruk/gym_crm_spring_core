package com.hitruk.gym.workload.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer_workloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerWorkload {

    @Id
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder.Default
    @OneToMany(mappedBy = "trainerWorkload", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<YearSummary> years = new ArrayList<>();
}
