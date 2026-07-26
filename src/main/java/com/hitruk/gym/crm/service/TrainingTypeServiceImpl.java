package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.TrainingTypeDto;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public List<TrainingTypeDto> findAll() {
        log.info("Finding all training types");
        return trainingTypeRepository.findAll().stream()
                .map(type -> new TrainingTypeDto(type.getId(), type.getName()))
                .toList();
    }
}
