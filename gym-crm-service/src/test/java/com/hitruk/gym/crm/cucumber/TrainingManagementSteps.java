package com.hitruk.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.crm.api.dto.request.AddTrainingRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingManagementSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("the trainer schedules a training named {string} in {int} days with duration {int}")
    public void theTrainerSchedulesATraining(String name, int daysFromNow, int duration) {
        scheduleTraining(context.traineeUsername(), name, LocalDate.now().plusDays(daysFromNow), duration);
    }

    @When("the trainer schedules a training for unknown trainee {string} named {string} in {int} days with duration {int}")
    public void theTrainerSchedulesATrainingForUnknownTrainee(String traineeUsername, String name, int daysFromNow, int duration) {
        scheduleTraining(traineeUsername, name, LocalDate.now().plusDays(daysFromNow), duration);
    }

    @When("the trainer schedules a training named {string} in {int} days with no duration")
    public void theTrainerSchedulesATrainingWithNoDuration(String name, int daysFromNow) {
        scheduleTraining(context.traineeUsername(), name, LocalDate.now().plusDays(daysFromNow), null);
    }

    @Given("the trainer has scheduled a training named {string} in {int} days with duration {int}")
    public void theTrainerHasScheduledATrainingInFuture(String name, int daysFromNow, int duration) throws Exception {
        ResponseEntity<String> response = scheduleTraining(context.traineeUsername(), name, LocalDate.now().plusDays(daysFromNow), duration);
        context.setLastTrainingId(objectMapper.readTree(response.getBody()).get("id").asLong());
    }

    @Given("the trainer has scheduled a training named {string} {int} days ago with duration {int}")
    public void theTrainerHasScheduledATrainingInPast(String name, int daysAgo, int duration) throws Exception {
        ResponseEntity<String> response = scheduleTraining(context.traineeUsername(), name, LocalDate.now().minusDays(daysAgo), duration);
        context.setLastTrainingId(objectMapper.readTree(response.getBody()).get("id").asLong());
    }

    @When("the trainer cancels that training")
    public void theTrainerCancelsThatTraining() {
        cancelTraining(context.lastTrainingId());
    }

    @When("the trainer cancels training with id {long}")
    public void theTrainerCancelsTrainingWithId(long id) {
        cancelTraining(id);
    }

    @Then("the training response contains an id")
    public void theTrainingResponseContainsAnId() throws Exception {
        JsonNode body = objectMapper.readTree(context.lastResponse().getBody());
        assertTrue(body.hasNonNull("id"));
        assertNotNull(body.get("id").asLong());
    }

    private ResponseEntity<String> scheduleTraining(String traineeUsername, String name, LocalDate date, Integer duration) {
        AddTrainingRequest request = new AddTrainingRequest(traineeUsername, context.trainerUsername(), name, date, duration);
        HttpHeaders headers = authorizedHeaders();
        ResponseEntity<String> response = context.restTemplate().exchange(
                context.baseUrl() + "/api/v1/trainings", HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        context.setLastResponse(response);
        return response;
    }

    private void cancelTraining(long id) {
        HttpHeaders headers = authorizedHeaders();
        ResponseEntity<String> response = context.restTemplate().exchange(
                context.baseUrl() + "/api/v1/trainings/" + id, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        context.setLastResponse(response);
    }

    private HttpHeaders authorizedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(context.trainerToken());
        return headers;
    }
}
