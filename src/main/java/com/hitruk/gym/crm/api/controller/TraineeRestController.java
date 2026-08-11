package com.hitruk.gym.crm.api.controller;

import com.hitruk.gym.crm.api.dto.*;
import com.hitruk.gym.crm.api.dto.request.ActivateRequest;
import com.hitruk.gym.crm.api.dto.request.TraineeRegistrationRequest;
import com.hitruk.gym.crm.api.dto.request.UpdateTraineeRequest;
import com.hitruk.gym.crm.api.dto.request.UpdateTraineeTrainersRequest;
import com.hitruk.gym.crm.api.dto.response.RegistrationResponse;
import com.hitruk.gym.crm.api.dto.response.TraineeProfileResponse;
import com.hitruk.gym.crm.api.dto.response.TraineeTrainingResponse;
import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import com.hitruk.gym.crm.security.JwtService;
import com.hitruk.gym.crm.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee", description = "Trainee management endpoints")
public class TraineeRestController {
    private final TraineeService traineeService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Register new trainee")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TraineeRegistrationRequest request) {
        TraineeDto dto = TraineeDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();
        TraineeDto created = traineeService.create(dto);

        String token = jwtService.generateToken(created.getCredentials().getUsername());

        return ResponseEntity.ok(new RegistrationResponse(
                created.getCredentials().getUsername(),
                created.getCredentials().getPassword(),
                token));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee profile")
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @Parameter(description = "Trainee username") @PathVariable String username) {
        return ResponseEntity.ok(buildProfileResponse(traineeService.findByUsername(username)));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainee profile")
    public ResponseEntity<TraineeProfileResponse> updateProfile(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody UpdateTraineeRequest request) {
        TraineeDto dto = TraineeDto.builder()
                .credentials(UserCredentials.of(username, null))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .isActive(request.getIsActive())
                .build();
        return ResponseEntity.ok(buildProfileResponse(traineeService.update(dto)));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Trainee username") @PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainers/unassigned")
    @Operation(summary = "Get unassigned active trainers for trainee")
    public ResponseEntity<List<TrainerSummary>> getUnassignedTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username) {
        return ResponseEntity.ok(traineeService.getUnassignedTrainers(username));
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update trainee's trainer list")
    public ResponseEntity<List<TrainerSummary>> updateTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        return ResponseEntity.ok(traineeService.updateTrainers(username, request.getTrainerUsernames()));
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee's trainings with optional filters")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
        List<TraineeTrainingResponse> trainings = traineeService
                .getTrainings(username, periodFrom, periodTo, trainerName, trainingType)
                .stream()
                .map(t -> TraineeTrainingResponse.builder()
                        .name(t.getName())
                        .date(t.getDate())
                        .trainingType(t.getTrainingType())
                        .duration(t.getDuration())
                        .trainerName(t.getTrainerUsername())
                        .build())
                .toList();
        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/{username}/active")
    @Operation(summary = "Activate or deactivate trainee")
    public ResponseEntity<Void> setActive(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody ActivateRequest request) {
        traineeService.setActive(username, request.getIsActive());
        return ResponseEntity.ok().build();
    }

    private TraineeProfileResponse buildProfileResponse(TraineeDto dto) {
        return TraineeProfileResponse.builder()
                .username(dto.getCredentials() != null ? dto.getCredentials().getUsername() : null)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .isActive(dto.getIsActive())
                .trainers(dto.getTrainers())
                .build();
    }
}
