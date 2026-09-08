package com.hitruk.gym.workload.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadSummaryResponse;
import com.hitruk.gym.workload.exception.TrainerWorkloadNotFoundException;
import com.hitruk.gym.workload.handler.GlobalExceptionHandler;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @InjectMocks
    private TrainerWorkloadController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void getWorkload_existingTrainer_returnsSummary() throws Exception {
        when(trainerWorkloadService.getSummary("Alex.Coach")).thenReturn(
                TrainerWorkloadSummaryResponse.builder()
                        .trainerUsername("Alex.Coach")
                        .trainerFirstName("Alex")
                        .trainerLastName("Coach")
                        .isActive(true)
                        .years(List.of())
                        .build());

        mockMvc.perform(get("/api/v1/workloads/{username}", "Alex.Coach"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("Alex.Coach"));
    }

    @Test
    void getWorkload_unknownTrainer_returnsNotFound() throws Exception {
        when(trainerWorkloadService.getSummary("unknown"))
                .thenThrow(new TrainerWorkloadNotFoundException("No workload data for trainer: unknown"));

        mockMvc.perform(get("/api/v1/workloads/{username}", "unknown"))
                .andExpect(status().isNotFound());
    }
}
