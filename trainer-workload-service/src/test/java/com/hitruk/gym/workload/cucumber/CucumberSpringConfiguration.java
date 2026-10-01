package com.hitruk.gym.workload.cucumber;

import com.hitruk.gym.workload.TrainerWorkloadServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class CucumberSpringConfiguration {
}
