package com.hitruk.gym.crm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.request.ActivateRequest;
import com.hitruk.gym.crm.api.dto.request.TraineeRegistrationRequest;
import com.hitruk.gym.crm.api.dto.request.UpdateTraineeRequest;
import com.hitruk.gym.crm.api.dto.request.UpdateTraineeTrainersRequest;
import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import com.hitruk.gym.crm.service.TraineeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeRestControllerTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeRestController controller;

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
        when(traineeService.create(any())).thenReturn(
                TraineeDto.builder()
                        .credentials(UserCredentials.of("John.Doe", "pass123"))
                        .build());

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TraineeRegistrationRequest("John", "Doe", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void register_missingFirstName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TraineeRegistrationRequest("", "Doe", null, null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfile_returnsProfile() throws Exception {
        when(traineeService.findByUsername("John.Doe")).thenReturn(
                TraineeDto.builder()
                        .credentials(UserCredentials.of("John.Doe", "pass"))
                        .firstName("John").lastName("Doe")
                        .isActive(true).trainers(List.of())
                        .build());

        mockMvc.perform(get("/api/v1/trainees/John.Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void updateProfile_returnsUpdatedProfile() throws Exception {
        when(traineeService.update(any())).thenReturn(
                TraineeDto.builder()
                        .credentials(UserCredentials.of("John.Doe", "pass"))
                        .firstName("John").lastName("Doe")
                        .isActive(true).trainers(List.of())
                        .build());

        mockMvc.perform(put("/api/v1/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateTraineeRequest("John", "Doe", null, null, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void deleteProfile_returns200() throws Exception {
        doNothing().when(traineeService).deleteByUsername("John.Doe");

        mockMvc.perform(delete("/api/v1/trainees/John.Doe"))
                .andExpect(status().isOk());
    }

    @Test
    void getUnassignedTrainers_returnsList() throws Exception {
        when(traineeService.getUnassignedTrainers("John.Doe")).thenReturn(
                List.of(TrainerSummary.builder()
                        .username("trainer1").firstName("Alice").lastName("Smith")
                        .specialization("YOGA").build()));

        mockMvc.perform(get("/api/v1/trainees/John.Doe/trainers/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer1"))
                .andExpect(jsonPath("$[0].specialization").value("YOGA"));
    }

    @Test
    void updateTrainers_returnsList() throws Exception {
        when(traineeService.updateTrainers(anyString(), any())).thenReturn(
                List.of(TrainerSummary.builder()
                        .username("trainer1").firstName("Alice").lastName("Smith")
                        .specialization("YOGA").build()));

        mockMvc.perform(put("/api/v1/trainees/John.Doe/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateTraineeTrainersRequest(List.of("trainer1")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer1"));
    }

    @Test
    void getTrainings_returnsList() throws Exception {
        when(traineeService.getTrainings(anyString(), any(), any(), any(), any())).thenReturn(
                List.of(TrainingDto.builder()
                        .name("Yoga session").date(LocalDate.now())
                        .trainingType("YOGA").duration(60).trainerUsername("trainer1")
                        .build()));

        mockMvc.perform(get("/api/v1/trainees/John.Doe/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yoga session"))
                .andExpect(jsonPath("$[0].trainerName").value("trainer1"));
    }

    @Test
    void setActive_returns200() throws Exception {
        doNothing().when(traineeService).setActive("John.Doe", true);

        mockMvc.perform(patch("/api/v1/trainees/John.Doe/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ActivateRequest(true))))
                .andExpect(status().isOk());
    }
}
