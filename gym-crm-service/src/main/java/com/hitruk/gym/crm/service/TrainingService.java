package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.TrainingDto;

public interface TrainingService {
    TrainingDto create(TrainingDto dto);
    void delete(Long id);
}
