package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Override
    public boolean matchCredentials(String username, String password) {
        return traineeService.matchCredentials(username, password)
                || trainerService.matchCredentials(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (traineeService.matchCredentials(username, oldPassword)) {
            traineeService.changePassword(username, oldPassword, newPassword);
        } else if (trainerService.matchCredentials(username, oldPassword)) {
            trainerService.changePassword(username, oldPassword, newPassword);
        } else {
            throw new InvalidCredentialsException("Invalid credentials for user: " + username);
        }
    }
}
