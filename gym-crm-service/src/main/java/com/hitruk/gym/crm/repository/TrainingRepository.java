package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.entity.Training;

import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);

    Optional<Training> findById(Long id);

    void delete(Training training);
}
