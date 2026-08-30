package com.hitruk.gym.crm.api.controller;

import com.hitruk.gym.crm.api.dto.request.ChangePasswordRequest;
import com.hitruk.gym.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User authentication endpoints")
public class UserRestController {
    private final UserService userService;

    @PutMapping("/login")
    @Operation(summary = "Change user password")
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
