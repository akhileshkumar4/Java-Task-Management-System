package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.model.TaskDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Task operations
 * Defines all business logic methods for TodoList functionality
 */
public interface TaskService {

    // Basic CRUD operations
    List<Task> getAllTasks();
    Optional<Task> getTaskById(Long id);
    Task saveTask(TaskDTO taskDTO);
    Task updateTask(Long id, TaskDTO taskDTO);
    void deleteTask(Long id);

    // Status operations
    Task toggleTaskCompletion(Long id);
    Task markTaskAsCompleted(Long id);
    Task markTaskAsIncomplete(Long id);

    // Search and filter operations
    List<Task> getTasksByStatus(boolean completed);
    List<Task> getTasksByProject(String project);
    List<Task> searchTasks(String keyword);
    List<Task> getTasksByCriteria(String project, Boolean completed, String keyword);

    // Date-based queries
    List<Task> getOverdueTasks();
    List<Task> getTasksDueToday();
    List<Task> getTasksDueWithinDays(int days);

    // Sorting operations
    List<Task> getAllTasksSortedByDate(boolean ascending);
    List<Task> getAllTasksSortedByProject();
    List<Task> getAllTasksSortedByPriority();
    List<Task> getAllTasksSortedByCreated();

    // Statistics
    long getTotalTasksCount();
    long getCompletedTasksCount();
    long getPendingTasksCount();
    long getOverdueTasksCount();

    // Project management
    List<String> getAllProjects();

    // File operations
    String exportTasksToJson();
    List<Task> importTasksFromJson(String jsonData);

    // Bulk operations
    void deleteAllTasks();
    void deleteCompletedTasks();
    List<Task> markAllTasksAsCompleted();
    List<Task> markAllTasksAsIncomplete();
}