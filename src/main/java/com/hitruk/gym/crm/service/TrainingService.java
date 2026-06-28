package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.model.dto.TrainingDto;

import java.util.List;

public interface TrainingService {
    TrainingDto create(TrainingDto dto);
    TrainingDto findById(Long id);
    List<TrainingDto> findAll();
}
