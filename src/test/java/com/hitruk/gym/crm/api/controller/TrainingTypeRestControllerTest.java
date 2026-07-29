package com.hitruk.gym.crm.api.controller;

import com.hitruk.gym.crm.api.dto.TrainingTypeDto;
import com.hitruk.gym.crm.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRestControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeRestController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAll_returnsTypeList() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(
                List.of(new TrainingTypeDto(1L, "YOGA"), new TrainingTypeDto(2L, "CARDIO")));

        mockMvc.perform(get("/api/v1/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("YOGA"))
                .andExpect(jsonPath("$[1].name").value("CARDIO"));
    }

    @Test
    void getAll_emptyList_returnsEmptyArray() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
