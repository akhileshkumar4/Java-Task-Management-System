package com.example.todolist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Task Entity representing a TodoList item
 * Contains all necessary fields for TodoList functionality
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Column(name = "project", length = 100)
    @Size(max = 100, message = "Project name cannot exceed 100 characters")
    private String project;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Task() {}

    public Task(String title, String description, LocalDate dueDate, String project) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.project = project;
        this.completed = false;
        this.priority = Priority.MEDIUM;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && !completed;
    }

    public String getStatusText() {
        return completed ? "Completed" : "Pending";
    }

    public String getStatusClass() {
        if (completed) return "success";
        if (isOverdue()) return "danger";
        return "primary";
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", dueDate=" + dueDate +
                ", project='" + project + '\'' +
                '}';
    }

    // Priority enum
    public enum Priority {
        LOW("Low", "success"),
        MEDIUM("Medium", "warning"),
        HIGH("High", "danger");

        private final String displayName;
        private final String cssClass;

        Priority(String displayName, String cssClass) {
            this.displayName = displayName;
            this.cssClass = cssClass;
        }

        public String getDisplayName() { return displayName; }
        public String getCssClass() { return cssClass; }
    }
}