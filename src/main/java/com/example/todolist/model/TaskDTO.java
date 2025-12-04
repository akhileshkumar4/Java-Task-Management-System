package com.example.todolist.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object for Task form handling
 */
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Size(max = 100, message = "Project name cannot exceed 100 characters")
    private String project;

    private Task.Priority priority = Task.Priority.MEDIUM;

    private boolean completed = false;

    // Constructors
    public TaskDTO() {}

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate();
        this.project = task.getProject();
        this.priority = task.getPriority();
        this.completed = task.isCompleted();
    }

    // Convert to Task entity
    public Task toTask() {
        Task task = new Task();
        task.setId(this.id);
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setDueDate(this.dueDate);
        task.setProject(this.project);
        task.setPriority(this.priority);
        task.setCompleted(this.completed);
        return task;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public Task.Priority getPriority() { return priority; }
    public void setPriority(Task.Priority priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}