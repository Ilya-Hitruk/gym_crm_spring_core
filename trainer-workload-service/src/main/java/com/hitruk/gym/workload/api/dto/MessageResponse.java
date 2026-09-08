package com.hitruk.gym.workload.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class MessageResponse {
    private String message;
}
