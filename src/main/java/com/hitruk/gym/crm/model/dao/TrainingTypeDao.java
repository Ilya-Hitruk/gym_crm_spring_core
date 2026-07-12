package com.hitruk.gym.crm.model.dao;

import com.hitruk.gym.crm.model.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao {
    TrainingType save(TrainingType trainingType);

    Optional<TrainingType> findByName(String name);

    List<TrainingType> findAll();
}
