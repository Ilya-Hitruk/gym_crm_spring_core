package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.model.dto.TrainerDto;

import java.util.List;

public interface TrainerService {
    TrainerDto create(TrainerDto dto);
    TrainerDto update(TrainerDto dto);
    TrainerDto findById(Long id);
    List<TrainerDto> findAll();
}
