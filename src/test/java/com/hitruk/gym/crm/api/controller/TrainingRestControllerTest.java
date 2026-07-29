package com.hitruk.gym.crm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.request.AddTrainingRequest;
import com.hitruk.gym.crm.service.TrainerService;
import com.hitruk.gym.crm.service.TrainingService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingRestControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingRestController controller;

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
    void addTraining_validRequest_returns200() throws Exception {
        when(trainerService.findByUsername("trainer1")).thenReturn(
                TrainerDto.builder().credentials(UserCredentials.of("trainer1", null)).specialization("YOGA").build());
        when(trainingService.create(any())).thenReturn(TrainingDto.builder().build());

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AddTrainingRequest("trainee1", "trainer1", "Yoga", LocalDate.now(), 60))))
                .andExpect(status().isOk());
    }

    @Test
    void addTraining_missingField_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AddTrainingRequest("", "trainer1", "Yoga", LocalDate.now(), 60))))
                .andExpect(status().isBadRequest());
    }
}