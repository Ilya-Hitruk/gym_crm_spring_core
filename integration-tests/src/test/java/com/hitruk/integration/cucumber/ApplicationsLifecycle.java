package com.hitruk.integration.cucumber;

import com.hitruk.App;
import com.hitruk.gym.workload.TrainerWorkloadServiceApplication;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationsLifecycle {

    public static final int GYM_CRM_PORT = 18080;
    public static final int WORKLOAD_PORT = 18081;

    private static ConfigurableApplicationContext gymCrmContext;
    private static ConfigurableApplicationContext workloadContext;
    private static MongoDBContainer mongoDBContainer;

    @BeforeAll
    public static synchronized void startApplications() {
        if (gymCrmContext != null) {
            return;
        }

        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));
        mongoDBContainer.start();

        Map<String, String> gymCrmProperties = new LinkedHashMap<>();
        gymCrmProperties.put("server.port", String.valueOf(GYM_CRM_PORT));
        gymCrmProperties.put("spring.datasource.url", "jdbc:h2:mem:integrationtestdb;DB_CLOSE_DELAY=-1");
        gymCrmProperties.put("spring.datasource.driver-class-name", "org.h2.Driver");
        gymCrmProperties.put("spring.datasource.username", "sa");
        gymCrmProperties.put("spring.datasource.password", "");
        gymCrmProperties.put("spring.jpa.hibernate.ddl-auto", "update");
        gymCrmProperties.put("spring.data.mongodb.repositories.type", "none");
        gymCrmProperties.put("spring.autoconfigure.exclude", String.join(",",
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
                "org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration",
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
                "org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration",
                "org.springframework.boot.actuate.autoconfigure.data.mongo.MongoHealthContributorAutoConfiguration",
                "org.springframework.boot.actuate.autoconfigure.data.mongo.MongoReactiveHealthContributorAutoConfiguration"));
        gymCrmContext = runWithSystemProperties(App.class, gymCrmProperties);

        Map<String, String> workloadProperties = new LinkedHashMap<>();
        workloadProperties.put("server.port", String.valueOf(WORKLOAD_PORT));
        workloadProperties.put("spring.data.mongodb.uri", mongoDBContainer.getConnectionString() + "/integration_test_db");
        workloadContext = runWithSystemProperties(TrainerWorkloadServiceApplication.class, workloadProperties);
    }

    @AfterAll
    public static synchronized void stopApplications() {
        if (workloadContext != null) {
            workloadContext.close();
            workloadContext = null;
        }
        if (gymCrmContext != null) {
            gymCrmContext.close();
            gymCrmContext = null;
        }
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
            mongoDBContainer = null;
        }
    }

    private static ConfigurableApplicationContext runWithSystemProperties(Class<?> applicationClass, Map<String, String> properties) {
        properties.forEach(System::setProperty);
        try {
            return new SpringApplicationBuilder(applicationClass).run();
        } finally {
            properties.keySet().forEach(System::clearProperty);
        }
    }
}
