package com.hitruk.gym.crm.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class TestContext {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private ResponseEntity<String> lastResponse;
    private String trainerUsername;
    private String trainerPassword;
    private String trainerToken;
    private String traineeUsername;
    private Long lastTrainingId;

    public String baseUrl() {
        return "http://localhost:" + port;
    }

    public TestRestTemplate restTemplate() {
        return restTemplate;
    }

    public void setLastResponse(ResponseEntity<String> response) {
        this.lastResponse = response;
    }

    public ResponseEntity<String> lastResponse() {
        return lastResponse;
    }

    public void setTrainerCredentials(String username, String password) {
        this.trainerUsername = username;
        this.trainerPassword = password;
    }

    public String trainerUsername() {
        return trainerUsername;
    }

    public String trainerPassword() {
        return trainerPassword;
    }

    public void setTrainerToken(String token) {
        this.trainerToken = token;
    }

    public String trainerToken() {
        return trainerToken;
    }

    public void setTraineeUsername(String username) {
        this.traineeUsername = username;
    }

    public String traineeUsername() {
        return traineeUsername;
    }

    public void setLastTrainingId(Long id) {
        this.lastTrainingId = id;
    }

    public Long lastTrainingId() {
        return lastTrainingId;
    }
}
