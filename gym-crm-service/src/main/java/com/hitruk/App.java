package com.hitruk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hitruk.gym.crm")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
