package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper implements Mapper<Training, TrainingDto> {

    @Override
    public Training toEntity(TrainingDto dto) {
        return Training.builder()
                .id(dto.getId())
                .traineeId(dto.getTraineeId())
                .trainerId(dto.getTrainerId())
                .name(dto.getName())
                .type(TrainingType.valueOf(dto.getTrainingType()))
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();
    }

    @Override
    public TrainingDto toDto(Training entity) {
        return TrainingDto.builder()
                .id(entity.getId())
                .traineeId(entity.getTraineeId())
                .trainerId(entity.getTrainerId())
                .name(entity.getName())
                .trainingType(entity.getType().name())
                .date(entity.getDate())
                .duration(entity.getDuration())
                .build();
    }
}
