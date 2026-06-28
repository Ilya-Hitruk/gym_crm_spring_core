package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper implements Mapper<Trainee, TraineeDto> {

    @Override
    public Trainee toEntity(TraineeDto dto) {
        return Trainee.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .isActive(dto.getIsActive())
                .build();
    }

    @Override
    public TraineeDto toDto(Trainee entity) {
        return TraineeDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .isActive(entity.getIsActive())
                .dateOfBirth(entity.getDateOfBirth())
                .address(entity.getAddress())
                .build();
    }
}
