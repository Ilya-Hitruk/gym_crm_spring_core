package com.hitruk.gym.crm.security;

import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean locked = loginAttemptService.isLocked(username);
        return traineeRepository.findByUsername(username)
                .<UserDetails>map(it -> new UserPrincipal(it, Role.TRAINEE, locked))
                .or(() -> trainerRepository.findByUsername(username)
                        .map(it -> new UserPrincipal(it, Role.TRAINER, locked)))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
