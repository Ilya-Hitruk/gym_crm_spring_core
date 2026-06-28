package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.model.dto.TraineeDto;

import java.util.List;

public interface TraineeService {
    TraineeDto create(TraineeDto dto);
    TraineeDto update(TraineeDto dto);
    boolean delete(Long id);
    TraineeDto findById(Long id);
    List<TraineeDto> findAll();
}
