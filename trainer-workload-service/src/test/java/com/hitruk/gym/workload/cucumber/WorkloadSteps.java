package com.hitruk.gym.workload.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkloadSteps {

    private static final String QUEUE_NAME = "trainer-workload-queue";

    @Autowired
    private TestContext context;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Given("a workload {word} event for trainer {string} {string} {string} active {word}, date {string}, duration {int}")
    public void aWorkloadEvent(String actionType, String username, String firstName, String lastName,
                               String active, String date, int duration) throws Exception {
        Map<String, Object> payload = Map.of(
                "trainerUsername", username,
                "trainerFirstName", firstName,
                "trainerLastName", lastName,
                "isActive", Boolean.parseBoolean(active),
                "trainingDate", date,
                "trainingDuration", duration,
                "actionType", actionType
        );
        jmsTemplate.convertAndSend(QUEUE_NAME, objectMapper.writeValueAsString(payload));
        waitForProcessing();
    }

    @Given("a malformed workload message is sent to the queue")
    public void aMalformedWorkloadMessage() {
        jmsTemplate.convertAndSend(QUEUE_NAME, "not-a-json-payload");
        waitForProcessing();
    }

    @When("I request the workload summary for trainer {string}")
    public void iRequestTheWorkloadSummary(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(context.generateTestToken());
        ResponseEntity<String> response = context.restTemplate().exchange(
                context.baseUrl() + "/api/v1/workloads/" + username, HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        context.setLastResponse(response);
    }

    @When("I request the workload summary for trainer {string} without authorization")
    public void iRequestTheWorkloadSummaryWithoutAuth(String username) {
        ResponseEntity<String> response = context.restTemplate().exchange(
                context.baseUrl() + "/api/v1/workloads/" + username, HttpMethod.GET,
                HttpEntity.EMPTY, String.class);
        context.setLastResponse(response);
    }

    @Then("the summary for year {int} month {int} shows duration {int}")
    public void theSummaryShowsDuration(int year, int month, int expectedDuration) throws Exception {
        JsonNode body = objectMapper.readTree(context.lastResponse().getBody());
        boolean found = false;
        for (JsonNode year_ : body.get("years")) {
            if (year_.get("year").asInt() != year) {
                continue;
            }
            for (JsonNode month_ : year_.get("months")) {
                if (month_.get("month").asInt() == month) {
                    assertEquals(expectedDuration, month_.get("summaryDuration").asInt());
                    found = true;
                }
            }
        }
        assertTrue(found, "No month entry found for " + year + "-" + month);
    }

    private void waitForProcessing() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
