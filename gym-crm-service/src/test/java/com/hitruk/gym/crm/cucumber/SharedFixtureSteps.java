package com.hitruk.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.crm.api.dto.request.TraineeRegistrationRequest;
import com.hitruk.gym.crm.api.dto.request.TrainerRegistrationRequest;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public class SharedFixtureSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Given("a registered trainer {string} {string} with specialization {string}")
    public void aRegisteredTrainer(String firstName, String lastName, String specialization) throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(firstName, lastName, specialization);
        ResponseEntity<String> response = post("/api/v1/trainers", request);
        JsonNode body = objectMapper.readTree(response.getBody());
        context.setTrainerCredentials(body.get("username").asText(), body.get("password").asText());
        context.setTrainerToken(body.get("token").asText());
    }

    @Given("a registered trainee {string} {string}")
    public void aRegisteredTrainee(String firstName, String lastName) throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(firstName, lastName, LocalDate.of(2001, 5, 5), "Lviv");
        ResponseEntity<String> response = post("/api/v1/trainees", request);
        JsonNode body = objectMapper.readTree(response.getBody());
        context.setTraineeUsername(body.get("username").asText());
    }

    private <T> ResponseEntity<String> post(String path, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return context.restTemplate().exchange(
                context.baseUrl() + path, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }
}
