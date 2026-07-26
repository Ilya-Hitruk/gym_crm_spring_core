package com.hitruk.gym.crm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.crm.api.dto.request.ChangePasswordRequest;
import com.hitruk.gym.crm.service.UserService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        when(userService.matchCredentials("user", "pass")).thenReturn(true);

        mockMvc.perform(get("/api/v1/users/login")
                        .param("username", "user")
                        .param("password", "pass"))
                .andExpect(status().isOk());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(userService.matchCredentials("user", "wrong")).thenReturn(false);

        mockMvc.perform(get("/api/v1/users/login")
                        .param("username", "user")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeLogin_validRequest_returns200() throws Exception {
        doNothing().when(userService).changePassword("user", "old", "new");

        mockMvc.perform(put("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ChangePasswordRequest("user", "old", "new"))))
                .andExpect(status().isOk());
    }

    @Test
    void changeLogin_missingField_returnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ChangePasswordRequest("", "old", "new"))))
                .andExpect(status().isBadRequest());
    }
}