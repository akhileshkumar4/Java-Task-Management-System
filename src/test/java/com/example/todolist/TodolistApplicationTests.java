package com.example.todolist;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic integration test for TodoList Application
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TodolistApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    void applicationStarts() {
        // This test verifies that the main application can start
        TodolistApplication.main(new String[] {});
    }
}