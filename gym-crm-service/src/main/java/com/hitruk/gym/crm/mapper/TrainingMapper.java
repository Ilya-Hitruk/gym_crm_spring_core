package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.entity.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper implements Mapper<Training, TrainingDto> {

    @Override
    public Training toEntity(TrainingDto dto) {
        return Training.builder()
                .id(dto.getId())
                .name(dto.getName())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();
    }

    @Override
    public TrainingDto toDto(Training entity) {
        return TrainingDto.builder()
                .id(entity.getId())
                .traineeUsername(entity.getTrainee().getUsername())
                .trainerUsername(entity.getTrainer().getUsername())
                .name(entity.getName())
                .trainingType(entity.getType().getName())
                .date(entity.getDate())
                .duration(entity.getDuration())
                .build();
    }
}
