package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper implements Mapper<Trainer, TrainerDto> {

    @Override
    public Trainer toEntity(TrainerDto dto) {
        return Trainer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .isActive(dto.getIsActive())
                .specialization(TrainerSpecialization.valueOf(dto.getSpecialization()))
                .build();
    }

    @Override
    public TrainerDto toDto(Trainer entity) {
        return TrainerDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .isActive(entity.getIsActive())
                .specialization(entity.getSpecialization().name())
                .build();
    }
}
