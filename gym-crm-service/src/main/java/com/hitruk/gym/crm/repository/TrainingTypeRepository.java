package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    TrainingType save(TrainingType trainingType);

    Optional<TrainingType> findByName(String name);

    List<TrainingType> findAll();
}
