package com.hitruk.integration.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MicroservicesIntegrationSteps {

    private static final String JWT_SECRET = "bn9ICmNW9efG/F/587MQ1JNlqio/LvmVe2iWH7ZZUVc=";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final RestTemplate REST = buildLenientRestTemplate();

    private String trainerUsername;
    private String trainerToken;
    private String traineeUsername;
    private LocalDate trainingDate;
    private Long lastTrainingId;
    private ResponseEntity<String> lastResponse;

    @Given("a registered trainer {string} {string} with specialization {string} on the live gym-crm service")
    public void aRegisteredTrainer(String firstName, String lastName, String specialization) throws Exception {
        Map<String, String> request = Map.of("firstName", firstName, "lastName", lastName, "specialization", specialization);
        ResponseEntity<String> response = REST.exchange(gymCrmUrl("/api/v1/trainers"), HttpMethod.POST,
                jsonEntity(request), String.class);
        JsonNode body = MAPPER.readTree(response.getBody());
        trainerUsername = body.get("username").asText();
        trainerToken = body.get("token").asText();
    }

    @Given("a registered trainee {string} {string} on the live gym-crm service")
    public void aRegisteredTrainee(String firstName, String lastName) throws Exception {
        Map<String, Object> request = Map.of("firstName", firstName, "lastName", lastName,
                "dateOfBirth", "2000-01-01", "address", "Kyiv");
        ResponseEntity<String> response = REST.exchange(gymCrmUrl("/api/v1/trainees"), HttpMethod.POST,
                jsonEntity(request), String.class);
        JsonNode body = MAPPER.readTree(response.getBody());
        traineeUsername = body.get("username").asText();
    }

    @Given("the trainer has scheduled a training named {string} in {int} days with duration {int}")
    public void theTrainerHasScheduledATraining(String name, int daysFromNow, int duration) throws Exception {
        scheduleTraining(traineeUsername, name, daysFromNow, duration);
        lastTrainingId = MAPPER.readTree(lastResponse.getBody()).get("id").asLong();
    }

    @When("the trainer schedules a training named {string} in {int} days with duration {int}")
    public void theTrainerSchedulesATraining(String name, int daysFromNow, int duration) throws Exception {
        scheduleTraining(traineeUsername, name, daysFromNow, duration);
        if (lastResponse.getStatusCode().is2xxSuccessful()) {
            lastTrainingId = MAPPER.readTree(lastResponse.getBody()).get("id").asLong();
        }
    }

    @When("the trainer schedules a training for unknown trainee {string} named {string} in {int} days with duration {int}")
    public void theTrainerSchedulesATrainingForUnknownTrainee(String unknownTrainee, String name, int daysFromNow, int duration) {
        scheduleTraining(unknownTrainee, name, daysFromNow, duration);
    }

    @When("the trainer cancels that training")
    public void theTrainerCancelsThatTraining() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(trainerToken);
        lastResponse = REST.exchange(gymCrmUrl("/api/v1/trainings/" + lastTrainingId), HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);
    }

    @Then("the training response status is {int}")
    public void theTrainingResponseStatusIs(int expected) {
        assertEquals(expected, lastResponse.getStatusCode().value());
    }

    @Then("within {int} seconds the trainer-workload-service reports duration {int} for that training's month")
    public void theWorkloadServiceReportsDuration(int timeoutSeconds, int expectedDuration) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        JsonNode lastBody = null;
        while (System.currentTimeMillis() < deadline) {
            ResponseEntity<String> response = fetchWorkload();
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    lastBody = MAPPER.readTree(response.getBody());
                    if (durationMatches(lastBody, expectedDuration)) {
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
            Thread.sleep(300);
        }
        fail("trainer-workload-service never reported duration " + expectedDuration +
                " for trainer " + trainerUsername + " within " + timeoutSeconds + "s. Last seen: " + lastBody);
    }

    @Then("the trainer-workload-service has no record for that trainer")
    public void theWorkloadServiceHasNoRecordForThatTrainer() {
        ResponseEntity<String> response = fetchWorkload();
        assertEquals(404, response.getStatusCode().value());
    }

    private ResponseEntity<String> fetchWorkload() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(generateWorkloadToken());
        return REST.exchange(workloadUrl("/api/v1/workloads/" + trainerUsername), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
    }

    private boolean durationMatches(JsonNode body, int expectedDuration) {
        if (body == null || body.get("years") == null) {
            return false;
        }
        for (JsonNode year : body.get("years")) {
            if (year.get("year").asInt() != trainingDate.getYear()) {
                continue;
            }
            for (JsonNode month : year.get("months")) {
                if (month.get("month").asInt() == trainingDate.getMonthValue()) {
                    return month.get("summaryDuration").asInt() == expectedDuration;
                }
            }
        }
        return false;
    }

    private void scheduleTraining(String traineeUsername, String name, int daysFromNow, Integer duration) {
        trainingDate = LocalDate.now().plusDays(daysFromNow);
        Map<String, Object> request = new HashMap<>();
        request.put("traineeUsername", traineeUsername);
        request.put("trainerUsername", trainerUsername);
        request.put("name", name);
        request.put("date", trainingDate.toString());
        request.put("duration", duration);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(trainerToken);
        lastResponse = REST.exchange(gymCrmUrl("/api/v1/trainings"), HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);
    }

    private HttpEntity<Object> jsonEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private String generateWorkloadToken() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("integration-test-client")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }

    private String gymCrmUrl(String path) {
        return "http://localhost:" + ApplicationsLifecycle.GYM_CRM_PORT + path;
    }

    private String workloadUrl(String path) {
        return "http://localhost:" + ApplicationsLifecycle.WORKLOAD_PORT + path;
    }

    private static RestTemplate buildLenientRestTemplate() {
        RestTemplate template = new RestTemplate();
        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        return template;
    }
}
