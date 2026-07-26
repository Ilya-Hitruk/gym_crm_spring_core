package com.hitruk.gym.crm.api.controller;

import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.api.dto.request.AddTrainingRequest;
import com.hitruk.gym.crm.service.TrainerService;
import com.hitruk.gym.crm.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training management endpoints")
public class TrainingRestController {

    private final TrainingService trainingService;
    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Add a new training")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest request) {
        TrainerDto trainer = trainerService.findByUsername(request.getTrainerUsername());
        TrainingDto dto = TrainingDto.builder()
                .traineeUsername(request.getTraineeUsername())
                .trainerUsername(request.getTrainerUsername())
                .name(request.getName())
                .date(request.getDate())
                .duration(request.getDuration())
                .trainingType(trainer.getSpecialization())
                .build();
        trainingService.create(dto);
        return ResponseEntity.ok().build();
    }
}
