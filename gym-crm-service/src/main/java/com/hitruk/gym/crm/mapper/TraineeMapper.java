package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import com.hitruk.gym.crm.entity.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TraineeMapper implements Mapper<Trainee, TraineeDto> {
    private final TrainerMapper trainerMapper;

    @Override
    public Trainee toEntity(TraineeDto dto) {
        return Trainee.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getCredentials().getUsername())
                .password(dto.getCredentials().getPassword())
                .isActive(dto.getIsActive())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .build();
    }

    @Override
    public TraineeDto toDto(Trainee entity) {
        List<TrainerSummary> trainers = entity.getTrainers() == null ? List.of() :
                entity.getTrainers().stream()
                        .map(trainerMapper::toSummary)
                        .toList();
        return TraineeDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .credentials(UserCredentials.of(entity.getUsername(), entity.getPassword()))
                .isActive(entity.getIsActive())
                .dateOfBirth(entity.getDateOfBirth())
                .address(entity.getAddress())
                .trainers(trainers)
                .build();
    }
}
