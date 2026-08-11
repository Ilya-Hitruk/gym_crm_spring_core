package com.hitruk.gym.crm.api.controller;

import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.request.ActivateRequest;
import com.hitruk.gym.crm.api.dto.request.TrainerRegistrationRequest;
import com.hitruk.gym.crm.api.dto.request.UpdateTrainerRequest;
import com.hitruk.gym.crm.api.dto.response.RegistrationResponse;
import com.hitruk.gym.crm.api.dto.response.TraineeSummary;
import com.hitruk.gym.crm.api.dto.response.TrainerProfileResponse;
import com.hitruk.gym.crm.api.dto.response.TrainerTrainingResponse;
import com.hitruk.gym.crm.security.JwtService;
import com.hitruk.gym.crm.service.TrainerService;
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
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainer", description = "Trainer management endpoints")
public class TrainerRestController {
    private final TrainerService trainerService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Register new trainer")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TrainerRegistrationRequest request) {
        TrainerDto dto = TrainerDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .build();
        TrainerDto created = trainerService.create(dto);
        String token = jwtService.generateToken(created.getCredentials().getUsername());
        return ResponseEntity.ok(new RegistrationResponse(
                created.getCredentials().getUsername(),
                created.getCredentials().getPassword(),
                token));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @Parameter(description = "Trainer username") @PathVariable String username) {
        TrainerDto trainer = trainerService.findByUsername(username);
        List<TraineeSummary> trainees = buildTraineeSummaries(trainerService.getTrainees(username));
        return ResponseEntity.ok(buildProfileResponse(trainer, trainees));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainer profile")
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Valid @RequestBody UpdateTrainerRequest request) {
        TrainerDto dto = TrainerDto.builder()
                .credentials(UserCredentials.of(username, null))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.getIsActive())
                .build();
        TrainerDto updated = trainerService.update(dto);
        List<TraineeSummary> trainees = buildTraineeSummaries(trainerService.getTrainees(username));
        return ResponseEntity.ok(buildProfileResponse(updated, trainees));
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer's trainings with optional filters")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName) {
        List<TrainerTrainingResponse> trainings = trainerService
                .getTrainings(username, periodFrom, periodTo, traineeName)
                .stream()
                .map(t -> TrainerTrainingResponse.builder()
                        .name(t.getName())
                        .date(t.getDate())
                        .trainingType(t.getTrainingType())
                        .duration(t.getDuration())
                        .traineeName(t.getTraineeUsername())
                        .build())
                .toList();
        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/{username}/active")
    @Operation(summary = "Activate or deactivate trainer")
    public ResponseEntity<Void> setActive(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Valid @RequestBody ActivateRequest request) {
        trainerService.setActive(username, request.getIsActive());
        return ResponseEntity.ok().build();
    }

    private List<TraineeSummary> buildTraineeSummaries(List<TraineeDto> trainees) {
        return trainees.stream()
                .map(t -> TraineeSummary.builder()
                        .username(t.getCredentials() != null ? t.getCredentials().getUsername() : null)
                        .firstName(t.getFirstName())
                        .lastName(t.getLastName())
                        .build())
                .toList();
    }

    private TrainerProfileResponse buildProfileResponse(TrainerDto trainer, List<TraineeSummary> trainees) {
        return TrainerProfileResponse.builder()
                .username(trainer.getCredentials() != null ? trainer.getCredentials().getUsername() : null)
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(trainer.getSpecialization())
                .isActive(trainer.getIsActive())
                .trainees(trainees)
                .build();
    }
}
