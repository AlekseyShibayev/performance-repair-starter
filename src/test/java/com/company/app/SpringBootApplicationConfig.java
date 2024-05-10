package com.company.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {StarterConfiguration.class})
public class SpringBootApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplicationConfig.class, args);
    }

}