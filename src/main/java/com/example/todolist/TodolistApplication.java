package com.example.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main SpringBoot Application class for TodoList Application
 * Features:
 * - Add a task
 * - Mark task as done
 * - Remove task
 * - Edit task
 * - Display all tasks
 * - Sort tasks by date
 * - Sort tasks by project
 * - Save tasks to file
 * - Read from file
 */
@SpringBootApplication
public class TodolistApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodolistApplication.class, args);
        System.out.println("===========================================");
        System.out.println("🚀 TodoList Application Started Successfully!");
        System.out.println("🌐 Access at: http://localhost:8080");
        System.out.println("===========================================");
    }
}