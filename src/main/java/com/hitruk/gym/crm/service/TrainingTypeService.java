package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.TrainingTypeDto;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeDto> findAll();
}
