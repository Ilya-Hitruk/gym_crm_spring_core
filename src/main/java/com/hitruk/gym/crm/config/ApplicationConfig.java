package com.hitruk.gym.crm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.hitruk.gym.crm")
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
}
