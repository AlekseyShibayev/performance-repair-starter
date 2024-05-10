package com.company.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {StarterConfiguration.class})
public class SpringBootApplicationMock {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplicationMock.class, args);
    }

}