package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.model.TaskDTO;
import com.example.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Main Controller for TodoList Application
 * Implements all features from the user requirements:
 * 1. Add a task
 * 2. Mark task as done
 * 3. Remove task
 * 4. Edit task
 * 5. Display all tasks
 * 6. Sort tasks by date
 * 7. Sort tasks by project
 * 8. Save tasks to file
 * 9. Read from file
 * 10. Exit functionality
 */
@Controller
@RequestMapping("/")
public class TaskController {

    private static final Logger logger = Logger.getLogger(TaskController.class.getName());

    @Autowired
    private TaskService taskService;

    /**
     * 5. Display all tasks - Main dashboard page
     */
    @GetMapping({"/", "/tasks"})
    public String showAllTasks(@RequestParam(required = false) String sort,
                              @RequestParam(required = false) String filter,
                              @RequestParam(required = false) String project,
                              @RequestParam(required = false) String search,
                              Model model) {

        logger.info("Displaying all tasks with sort: " + sort + ", filter: " + filter + ", project: " + project);

        List<Task> tasks;

        // Apply search if provided
        if (search != null && !search.trim().isEmpty()) {
            tasks = taskService.searchTasks(search.trim());
            model.addAttribute("searchKeyword", search);
        }
        // Apply filters
        else if ("completed".equals(filter)) {
            tasks = taskService.getTasksByStatus(true);
        } else if ("pending".equals(filter)) {
            tasks = taskService.getTasksByStatus(false);
        } else if ("overdue".equals(filter)) {
            tasks = taskService.getOverdueTasks();
        } else if (project != null && !project.trim().isEmpty()) {
            tasks = taskService.getTasksByProject(project);
        }
        // 6. Sort tasks by date
        else if ("date-asc".equals(sort)) {
            tasks = taskService.getAllTasksSortedByDate(true);
        } else if ("date-desc".equals(sort)) {
            tasks = taskService.getAllTasksSortedByDate(false);
        }
        // 7. Sort tasks by project
        else if ("project".equals(sort)) {
            tasks = taskService.getAllTasksSortedByProject();
        } else if ("priority".equals(sort)) {
            tasks = taskService.getAllTasksSortedByPriority();
        } else if ("created".equals(sort)) {
            tasks = taskService.getAllTasksSortedByCreated();
        } else {
            tasks = taskService.getAllTasks();
        }

        // Add model attributes
        model.addAttribute("tasks", tasks);
        model.addAttribute("taskDTO", new TaskDTO());
        model.addAttribute("projects", taskService.getAllProjects());
        model.addAttribute("priorities", Task.Priority.values());

        // Statistics
        model.addAttribute("totalTasks", taskService.getTotalTasksCount());
        model.addAttribute("completedTasks", taskService.getCompletedTasksCount());
        model.addAttribute("pendingTasks", taskService.getPendingTasksCount());
        model.addAttribute("overdueTasks", taskService.getOverdueTasksCount());

        // Current filter/sort info
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("currentProject", project);

        return "index";
    }

    /**
     * 1. Add a task - Handle form submission
     */
    @PostMapping("/tasks")
    public String addTask(@Valid @ModelAttribute TaskDTO taskDTO, 
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        logger.info("Adding new task: " + taskDTO.getTitle());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the form errors");
            return "redirect:/";
        }

        try {
            Task savedTask = taskService.saveTask(taskDTO);
            redirectAttributes.addFlashAttribute("success", "Task '" + savedTask.getTitle() + "' added successfully!");
            logger.info("Task added successfully with ID: " + savedTask.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding task: " + e.getMessage());
            logger.severe("Error adding task: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 2. Mark task as done - Toggle completion status
     */
    @PostMapping("/tasks/{id}/toggle")
    public String toggleTaskCompletion(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        logger.info("Toggling completion status for task ID: " + id);

        try {
            Task updatedTask = taskService.toggleTaskCompletion(id);
            String status = updatedTask.isCompleted() ? "completed" : "pending";
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + updatedTask.getTitle() + "' marked as " + status + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating task: " + e.getMessage());
            logger.severe("Error toggling task completion: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 3. Remove task - Delete task
     */
    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        logger.info("Deleting task with ID: " + id);

        try {
            Optional<Task> task = taskService.getTaskById(id);
            taskService.deleteTask(id);

            if (task.isPresent()) {
                redirectAttributes.addFlashAttribute("success", 
                    "Task '" + task.get().getTitle() + "' deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting task: " + e.getMessage());
            logger.severe("Error deleting task: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 4. Edit task - Show edit form
     */
    @GetMapping("/tasks/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        logger.info("Showing edit form for task ID: " + id);

        try {
            Optional<Task> taskOpt = taskService.getTaskById(id);
            if (taskOpt.isPresent()) {
                model.addAttribute("taskDTO", new TaskDTO(taskOpt.get()));
                model.addAttribute("projects", taskService.getAllProjects());
                model.addAttribute("priorities", Task.Priority.values());
                model.addAttribute("editMode", true);
                return "edit-task";
            } else {
                redirectAttributes.addFlashAttribute("error", "Task not found");
                return "redirect:/";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading task: " + e.getMessage());
            logger.severe("Error loading task for edit: " + e.getMessage());
            return "redirect:/";
        }
    }

    /**
     * 4. Edit task - Handle update form submission
     */
    @PostMapping("/tasks/{id}/edit")
    public String updateTask(@PathVariable Long id, 
                           @Valid @ModelAttribute TaskDTO taskDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        logger.info("Updating task with ID: " + id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the form errors");
            return "redirect:/tasks/" + id + "/edit";
        }

        try {
            Task updatedTask = taskService.updateTask(id, taskDTO);
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + updatedTask.getTitle() + "' updated successfully!");
            logger.info("Task updated successfully: " + id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating task: " + e.getMessage());
            logger.severe("Error updating task: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 8. Save tasks to file - Export tasks as JSON
     */
    @GetMapping("/tasks/export")
    public ResponseEntity<String> exportTasks() {

        logger.info("Exporting tasks to JSON file");

        try {
            String jsonData = taskService.exportTasksToJson();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "todolist_tasks_export.json");

            logger.info("Tasks exported successfully");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jsonData);

        } catch (Exception e) {
            logger.severe("Error exporting tasks: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Error exporting tasks: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 9. Read from file - Import tasks from JSON file
     */
    @PostMapping("/tasks/import")
    public String importTasks(@RequestParam("file") MultipartFile file, 
                             RedirectAttributes redirectAttributes) {

        logger.info("Importing tasks from file: " + file.getOriginalFilename());

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to import");
            return "redirect:/";
        }

        try {
            String jsonData = new String(file.getBytes());
            List<Task> importedTasks = taskService.importTasksFromJson(jsonData);

            redirectAttributes.addFlashAttribute("success", 
                "Successfully imported " + importedTasks.size() + " tasks!");
            logger.info("Successfully imported " + importedTasks.size() + " tasks");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error importing tasks: " + e.getMessage());
            logger.severe("Error importing tasks: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * Bulk operations
     */
    @PostMapping("/tasks/bulk/delete-completed")
    public String deleteCompletedTasks(RedirectAttributes redirectAttributes) {
        try {
            long completedCount = taskService.getCompletedTasksCount();
            taskService.deleteCompletedTasks();
            redirectAttributes.addFlashAttribute("success", 
                "Deleted " + completedCount + " completed tasks!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting completed tasks: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/tasks/bulk/mark-all-completed")
    public String markAllTasksCompleted(RedirectAttributes redirectAttributes) {
        try {
            List<Task> updatedTasks = taskService.markAllTasksAsCompleted();
            redirectAttributes.addFlashAttribute("success", 
                "Marked " + updatedTasks.size() + " tasks as completed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error marking all tasks as completed: " + e.getMessage());
        }
        return "redirect:/";
    }

    /**
     * 10. Exit functionality - Logout/Exit page
     */
    @GetMapping("/exit")
    public String showExitPage(Model model) {
        logger.info("Showing exit/logout page");

        // Add final statistics
        model.addAttribute("totalTasks", taskService.getTotalTasksCount());
        model.addAttribute("completedTasks", taskService.getCompletedTasksCount());
        model.addAttribute("pendingTasks", taskService.getPendingTasksCount());

        return "exit";
    }

    @PostMapping("/exit")
    public String processExit() {
        logger.info("Processing application exit");
        return "redirect:/";
    }

    /**
     * REST API Endpoints for AJAX calls
     */
    @GetMapping("/api/tasks")
    @ResponseBody
    public List<Task> getTasksApi() {
        return taskService.getAllTasks();
    }

    @GetMapping("/api/tasks/stats")
    @ResponseBody
    public Object getTaskStats() {
        return new Object() {
            public final long total = taskService.getTotalTasksCount();
            public final long completed = taskService.getCompletedTasksCount();
            public final long pending = taskService.getPendingTasksCount();
            public final long overdue = taskService.getOverdueTasksCount();
        };
    }

    @PostMapping("/api/tasks/{id}/toggle")
    @ResponseBody
    public Task toggleTaskApi(@PathVariable Long id) {
        return taskService.toggleTaskCompletion(id);
    }

    /**
     * Error handling
     */
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model, RedirectAttributes redirectAttributes) {
        logger.severe("Controller error: " + e.getMessage());
        redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        return "redirect:/";
    }
}