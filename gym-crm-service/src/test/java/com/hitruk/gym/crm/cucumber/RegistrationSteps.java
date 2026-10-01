package com.hitruk.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.crm.api.dto.request.TraineeRegistrationRequest;
import com.hitruk.gym.crm.api.dto.request.TrainerRegistrationRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("I register a new trainee with first name {string} and last name {string}")
    public void iRegisterANewTrainee(String firstName, String lastName) {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(firstName, lastName, LocalDate.of(2000, 1, 1), "Kyiv");
        context.setLastResponse(post("/api/v1/trainees", request));
    }

    @When("I register a new trainer with first name {string} and last name {string} and specialization {string}")
    public void iRegisterANewTrainer(String firstName, String lastName, String specialization) {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(firstName, lastName, specialization);
        context.setLastResponse(post("/api/v1/trainers", request));
    }

    @Then("the registration response contains a username and a password")
    public void theRegistrationResponseContainsCredentials() throws Exception {
        JsonNode body = objectMapper.readTree(context.lastResponse().getBody());
        assertTrue(body.hasNonNull("username"));
        assertFalse(body.get("username").asText().isBlank());
        assertTrue(body.hasNonNull("password"));
        assertFalse(body.get("password").asText().isBlank());
    }

    private <T> org.springframework.http.ResponseEntity<String> post(String path, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return context.restTemplate().exchange(
                context.baseUrl() + path, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }
}
