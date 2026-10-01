package com.hitruk.gym.workload.cucumber;

import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommonSteps {

    @Autowired
    private TestContext context;

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) {
        assertNotNull(context.lastResponse(), "No response recorded for this scenario");
        assertEquals(expectedStatus, context.lastResponse().getStatusCode().value());
    }
}
