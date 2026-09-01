package com.hitruk.gym.crm.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTraineeTrainersRequest {
    @NotNull(message = "Trainer usernames list is required")
    private List<String> trainerUsernames;
}
