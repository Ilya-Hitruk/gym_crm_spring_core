package com.hitruk.gym.workload.repository;

import com.hitruk.gym.workload.entity.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, String> {
}
