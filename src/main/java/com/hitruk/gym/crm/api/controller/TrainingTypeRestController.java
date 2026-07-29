package com.hitruk.gym.crm.api.controller;

import com.hitruk.gym.crm.api.dto.TrainingTypeDto;
import com.hitruk.gym.crm.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/training-types")
@RequiredArgsConstructor
@Tag(name = "TrainingType", description = "Training type endpoints")
public class TrainingTypeRestController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    @Operation(summary = "Get all training types")
    public ResponseEntity<List<TrainingTypeDto>> getAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }

}
