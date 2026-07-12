package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper implements Mapper<Trainer, TrainerDto> {

    @Override
    public Trainer toEntity(TrainerDto dto) {
        TrainingType specialization = dto.getSpecialization() == null ? null :
                TrainingType.builder().name(dto.getSpecialization()).build();
        return Trainer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .isActive(dto.getIsActive())
                .specialization(specialization)
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
                .specialization(entity.getSpecialization() == null ? null :
                        entity.getSpecialization().getName())
                .build();
    }
}
