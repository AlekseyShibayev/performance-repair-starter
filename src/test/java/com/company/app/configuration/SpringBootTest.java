package com.company.app.configuration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import com.company.app.infrastructure.jpa.entityfinder.EntityFinder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;


@Slf4j
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource("/test.properties")
@org.springframework.boot.test.context.SpringBootTest(
    classes = {DbTestConfiguration.class}
//    , webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers(disabledWithoutDocker = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SpringBootTest {

    @Autowired
    protected TransactionTemplate transactionTemplate;
    @Autowired
    protected EntityManager entityManager;
    @Autowired
    protected EntityFinder entityFinder;

    @PostConstruct
    void init() {
        log.debug("**********     run spring boot test context     **********");
    }

    @BeforeEach
    protected void doBeforeEach() {
    }

    @AfterEach
    protected void doAfterEach() {
    }

}