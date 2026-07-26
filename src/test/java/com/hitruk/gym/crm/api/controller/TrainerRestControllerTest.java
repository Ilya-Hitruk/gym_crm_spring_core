package com.hitruk.gym.crm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.request.ActivateRequest;
import com.hitruk.gym.crm.api.dto.request.TrainerRegistrationRequest;
import com.hitruk.gym.crm.api.dto.request.UpdateTrainerRequest;
import com.hitruk.gym.crm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerRestControllerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerRestController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void register_returnsCredentials() throws Exception {
        when(trainerService.create(any())).thenReturn(
                TrainerDto.builder().credentials(UserCredentials.of("Alice.Smith", "pass456")).build());

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TrainerRegistrationRequest("Alice", "Smith", "YOGA"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Alice.Smith"))
                .andExpect(jsonPath("$.password").value("pass456"));
    }

    @Test
    void register_missingSpecialization_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TrainerRegistrationRequest("Alice", "Smith", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfile_returnsProfile() throws Exception {
        when(trainerService.findByUsername("Alice.Smith")).thenReturn(
                TrainerDto.builder().credentials(UserCredentials.of("Alice.Smith", "pass"))
                        .firstName("Alice").lastName("Smith")
                        .specialization("YOGA").isActive(true).build());
        when(trainerService.getTrainees("Alice.Smith")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/trainers/Alice.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Alice.Smith"))
                .andExpect(jsonPath("$.specialization").value("YOGA"));
    }

    @Test
    void getProfile_withTrainees() throws Exception {
        when(trainerService.findByUsername("Alice.Smith")).thenReturn(
                TrainerDto.builder().credentials(UserCredentials.of("Alice.Smith", "pass"))
                        .firstName("Alice").lastName("Smith")
                        .specialization("YOGA").isActive(true).build());
        when(trainerService.getTrainees("Alice.Smith")).thenReturn(
                List.of(TraineeDto.builder()
                        .credentials(UserCredentials.of("John.Doe", null))
                        .firstName("John").lastName("Doe").build()));

        mockMvc.perform(get("/api/v1/trainers/Alice.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainees[0].username").value("John.Doe"));
    }

    @Test
    void updateProfile_returnsUpdatedProfile() throws Exception {
        when(trainerService.update(any())).thenReturn(
                TrainerDto.builder().credentials(UserCredentials.of("Alice.Smith", "pass"))
                        .firstName("Alice").lastName("Smith")
                        .specialization("YOGA").isActive(false).build());
        when(trainerService.getTrainees("Alice.Smith")).thenReturn(List.of());

        mockMvc.perform(put("/api/v1/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateTrainerRequest("Alice", "Smith", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void getTrainings_returnsList() throws Exception {
        when(trainerService.getTrainings(anyString(), any(), any(), any())).thenReturn(
                List.of(TrainingDto.builder()
                        .name("Cardio").date(LocalDate.now())
                        .trainingType("CARDIO").duration(45).traineeUsername("John.Doe")
                        .build()));

        mockMvc.perform(get("/api/v1/trainers/Alice.Smith/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cardio"))
                .andExpect(jsonPath("$[0].traineeName").value("John.Doe"));
    }

    @Test
    void setActive_returns200() throws Exception {
        doNothing().when(trainerService).setActive("Alice.Smith", false);

        mockMvc.perform(patch("/api/v1/trainers/Alice.Smith/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ActivateRequest(false))))
                .andExpect(status().isOk());
    }
}
