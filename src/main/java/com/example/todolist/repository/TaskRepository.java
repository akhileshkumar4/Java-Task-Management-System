package com.example.todolist.repository;

import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by completion status
    List<Task> findByCompleted(boolean completed);

    // Find tasks by project
    List<Task> findByProjectIgnoreCase(String project);

    // Find tasks by title containing (case-insensitive search)
    List<Task> findByTitleContainingIgnoreCase(String title);

    // Find tasks by project and completion status
    List<Task> findByProjectIgnoreCaseAndCompleted(String project, boolean completed);

    // Find overdue tasks (due date before today and not completed)
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.completed = false")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    // Find tasks due today
    @Query("SELECT t FROM Task t WHERE t.dueDate = :today")
    List<Task> findTasksDueToday(@Param("today") LocalDate today);

    // Find tasks due within next N days
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.completed = false")
    List<Task> findTasksDueWithinDays(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find all tasks ordered by due date
    @Query("SELECT t FROM Task t ORDER BY t.dueDate ASC, t.createdAt ASC")
    List<Task> findAllOrderByDueDateAsc();

    // Find all tasks ordered by due date descending
    @Query("SELECT t FROM Task t ORDER BY t.dueDate DESC, t.createdAt DESC")
    List<Task> findAllOrderByDueDateDesc();

    // Find all tasks ordered by project
    @Query("SELECT t FROM Task t ORDER BY t.project ASC, t.dueDate ASC")
    List<Task> findAllOrderByProjectAsc();

    // Find all tasks ordered by priority
    @Query("SELECT t FROM Task t ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findAllOrderByPriorityDesc();

    // Find all tasks ordered by creation date
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findAllOrderByCreatedAtDesc();

    // Count tasks by completion status
    long countByCompleted(boolean completed);

    // Count overdue tasks
    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :today AND t.completed = false")
    long countOverdueTasks(@Param("today") LocalDate today);

    // Get distinct project names
    @Query("SELECT DISTINCT t.project FROM Task t WHERE t.project IS NOT NULL AND t.project != '' ORDER BY t.project")
    List<String> findDistinctProjects();

    // Search tasks by keyword in title or description
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchTasksByKeyword(@Param("keyword") String keyword);

    // Find tasks by multiple criteria
    @Query("SELECT t FROM Task t WHERE " +
           "(:project IS NULL OR t.project = :project) AND " +
           "(:completed IS NULL OR t.completed = :completed) AND " +
           "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> findTasksByCriteria(@Param("project") String project, 
                                   @Param("completed") Boolean completed, 
                                   @Param("keyword") String keyword);
}