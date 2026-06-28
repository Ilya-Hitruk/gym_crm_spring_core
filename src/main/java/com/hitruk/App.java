package com.hitruk;


import com.hitruk.gym.crm.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main( String[] args ) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

    }
}
