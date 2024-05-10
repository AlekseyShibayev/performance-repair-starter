package com.company.app;

import com.company.app.configuration.SpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


class SmokeApplicationTest extends SpringBootTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void spring_application_context_can_rise() {
        Assertions.assertNotNull(applicationContext);
    }

}