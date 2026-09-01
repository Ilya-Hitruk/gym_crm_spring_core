package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.TrainingType;
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
                .username(dto.getCredentials() == null ? null : dto.getCredentials().getUsername())
                .password(dto.getCredentials() == null ? null : dto.getCredentials().getPassword())
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
                .credentials(UserCredentials.of(entity.getUsername(), entity.getPassword()))
                .isActive(entity.getIsActive())
                .specialization(entity.getSpecialization() == null ? null :
                        entity.getSpecialization().getName())
                .build();
    }

    public TrainerSummary toSummary(Trainer entity) {
        return TrainerSummary.builder()
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .specialization(entity.getSpecialization() == null ? null :
                        entity.getSpecialization().getName())
                .build();
    }
}
