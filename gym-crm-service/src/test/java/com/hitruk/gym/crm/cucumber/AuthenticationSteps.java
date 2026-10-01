package com.hitruk.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("the trainer logs in with the correct password")
    public void theTrainerLogsInWithCorrectPassword() {
        login(context.trainerUsername(), context.trainerPassword());
    }

    @When("the trainer logs in with password {string}")
    public void theTrainerLogsInWithPassword(String password) {
        login(context.trainerUsername(), password);
    }

    @Then("the login response contains a token")
    public void theLoginResponseContainsAToken() throws Exception {
        JsonNode body = objectMapper.readTree(context.lastResponse().getBody());
        assertTrue(body.hasNonNull("token"));
        assertFalse(body.get("token").asText().isBlank());
    }

    private void login(String username, String password) {
        UserCredentials credentials = UserCredentials.of(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = context.restTemplate().exchange(
                context.baseUrl() + "/api/v1/auth/login", HttpMethod.POST,
                new HttpEntity<>(credentials, headers), String.class);
        context.setLastResponse(response);
    }
}
