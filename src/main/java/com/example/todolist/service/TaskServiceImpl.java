package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.model.TaskDTO;
import com.example.todolist.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of TaskService interface
 * Contains all business logic for TodoList operations
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = Logger.getLogger(TaskServiceImpl.class.getName());

    @Autowired
    private TaskRepository taskRepository;

    private final ObjectMapper objectMapper;

    public TaskServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<Task> getAllTasks() {
        logger.info("Retrieving all tasks");
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        logger.info("Retrieving task with ID: " + id);
        return taskRepository.findById(id);
    }

    @Override
    public Task saveTask(TaskDTO taskDTO) {
        logger.info("Saving new task: " + taskDTO.getTitle());
        Task task = taskDTO.toTask();
        Task savedTask = taskRepository.save(task);
        logger.info("Task saved with ID: " + savedTask.getId());
        return savedTask;
    }

    @Override
    public Task updateTask(Long id, TaskDTO taskDTO) {
        logger.info("Updating task with ID: " + id);
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskDTO.getTitle());
                    existingTask.setDescription(taskDTO.getDescription());
                    existingTask.setDueDate(taskDTO.getDueDate());
                    existingTask.setProject(taskDTO.getProject());
                    existingTask.setPriority(taskDTO.getPriority());
                    existingTask.setCompleted(taskDTO.isCompleted());
                    Task updatedTask = taskRepository.save(existingTask);
                    logger.info("Task updated successfully: " + id);
                    return updatedTask;
                })
                .orElseThrow(() -> {
                    logger.severe("Task not found with ID: " + id);
                    return new RuntimeException("Task not found with ID: " + id);
                });
    }

    @Override
    public void deleteTask(Long id) {
        logger.info("Deleting task with ID: " + id);
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
        logger.info("Task deleted successfully: " + id);
    }

    @Override
    public Task toggleTaskCompletion(Long id) {
        logger.info("Toggling completion status for task ID: " + id);
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(!task.isCompleted());
                    Task updatedTask = taskRepository.save(task);
                    logger.info("Task completion toggled: " + id + " -> " + updatedTask.isCompleted());
                    return updatedTask;
                })
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }

    @Override
    public Task markTaskAsCompleted(Long id) {
        logger.info("Marking task as completed: " + id);
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(true);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }

    @Override
    public Task markTaskAsIncomplete(Long id) {
        logger.info("Marking task as incomplete: " + id);
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(false);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(boolean completed) {
        logger.info("Retrieving tasks by completion status: " + completed);
        return taskRepository.findByCompleted(completed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(String project) {
        logger.info("Retrieving tasks by project: " + project);
        return taskRepository.findByProjectIgnoreCase(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> searchTasks(String keyword) {
        logger.info("Searching tasks with keyword: " + keyword);
        return taskRepository.searchTasksByKeyword(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByCriteria(String project, Boolean completed, String keyword) {
        logger.info("Retrieving tasks by criteria - Project: " + project + ", Completed: " + completed + ", Keyword: " + keyword);
        return taskRepository.findTasksByCriteria(project, completed, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        logger.info("Retrieving overdue tasks");
        return taskRepository.findOverdueTasks(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksDueToday() {
        logger.info("Retrieving tasks due today");
        return taskRepository.findTasksDueToday(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksDueWithinDays(int days) {
        logger.info("Retrieving tasks due within " + days + " days");
        LocalDate today = LocalDate.now();
        return taskRepository.findTasksDueWithinDays(today, today.plusDays(days));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksSortedByDate(boolean ascending) {
        logger.info("Retrieving all tasks sorted by date (ascending: " + ascending + ")");
        return ascending ? taskRepository.findAllOrderByDueDateAsc() : taskRepository.findAllOrderByDueDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksSortedByProject() {
        logger.info("Retrieving all tasks sorted by project");
        return taskRepository.findAllOrderByProjectAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksSortedByPriority() {
        logger.info("Retrieving all tasks sorted by priority");
        return taskRepository.findAllOrderByPriorityDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksSortedByCreated() {
        logger.info("Retrieving all tasks sorted by creation date");
        return taskRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalTasksCount() {
        return taskRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getCompletedTasksCount() {
        return taskRepository.countByCompleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingTasksCount() {
        return taskRepository.countByCompleted(false);
    }

    @Override
    @Transactional(readOnly = true)
    public long getOverdueTasksCount() {
        return taskRepository.countOverdueTasks(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllProjects() {
        logger.info("Retrieving all project names");
        return taskRepository.findDistinctProjects();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportTasksToJson() {
        logger.info("Exporting all tasks to JSON");
        try {
            List<Task> tasks = taskRepository.findAll();
            return objectMapper.writeValueAsString(tasks);
        } catch (JsonProcessingException e) {
            logger.severe("Error exporting tasks to JSON: " + e.getMessage());
            throw new RuntimeException("Error exporting tasks to JSON", e);
        }
    }

    @Override
    public List<Task> importTasksFromJson(String jsonData) {
        logger.info("Importing tasks from JSON");
        try {
            Task[] tasksArray = objectMapper.readValue(jsonData, Task[].class);
            List<Task> tasks = Arrays.asList(tasksArray);

            // Clear existing IDs to avoid conflicts
            tasks.forEach(task -> task.setId(null));

            List<Task> savedTasks = taskRepository.saveAll(tasks);
            logger.info("Successfully imported " + savedTasks.size() + " tasks");
            return savedTasks;
        } catch (JsonProcessingException e) {
            logger.severe("Error importing tasks from JSON: " + e.getMessage());
            throw new RuntimeException("Error importing tasks from JSON", e);
        }
    }

    @Override
    public void deleteAllTasks() {
        logger.info("Deleting all tasks");
        taskRepository.deleteAll();
        logger.info("All tasks deleted successfully");
    }

    @Override
    public void deleteCompletedTasks() {
        logger.info("Deleting completed tasks");
        List<Task> completedTasks = taskRepository.findByCompleted(true);
        taskRepository.deleteAll(completedTasks);
        logger.info("Deleted " + completedTasks.size() + " completed tasks");
    }

    @Override
    public List<Task> markAllTasksAsCompleted() {
        logger.info("Marking all tasks as completed");
        List<Task> allTasks = taskRepository.findAll();
        allTasks.forEach(task -> task.setCompleted(true));
        List<Task> updatedTasks = taskRepository.saveAll(allTasks);
        logger.info("Marked " + updatedTasks.size() + " tasks as completed");
        return updatedTasks;
    }

    @Override
    public List<Task> markAllTasksAsIncomplete() {
        logger.info("Marking all tasks as incomplete");
        List<Task> allTasks = taskRepository.findAll();
        allTasks.forEach(task -> task.setCompleted(false));
        List<Task> updatedTasks = taskRepository.saveAll(allTasks);
        logger.info("Marked " + updatedTasks.size() + " tasks as incomplete");
        return updatedTasks;
    }
}