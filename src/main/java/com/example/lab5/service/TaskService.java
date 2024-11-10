package com.example.lab5.service;

import com.example.lab5.model.Task;
import com.example.lab5.model.User;
import com.example.lab5.repository.TaskRepository;
import com.example.lab5.repository.TaskStatusRepository;
import com.example.lab5.repository.TaskPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    // Create a new task
    public Task createTask(Task task, User user) {
        task.setUser(user); // Associate the task with the user
        validateDueDate(task.getDueDate()); // Validate due date
        return taskRepository.save(task); // Save task to the database
    }

    // Retrieve all tasks for a specific user
    public List<Task> getAllTasksForUser(User user) {
        return taskRepository.findByUser(user);
    }

    // Retrieve a specific task by ID (ensuring it belongs to the authenticated user)
    public Optional<Task> getTaskByIdForUser(Long taskId, User user) {
        return taskRepository.findByIdAndUser(taskId, user);
    }

    // Update an existing task
    @Transactional
    public Task updateTask(Long taskId, Task updatedTask, User user) {
        Task existingTask = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found or unauthorized access"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(validateDueDate(updatedTask.getDueDate()));
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());

        return taskRepository.save(existingTask);
    }

    // Delete a task (only if it belongs to the authenticated user)
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found or unauthorized access"));
        taskRepository.delete(task);
    }

    // Sort tasks by due date for a specific user
    public List<Task> getTasksSortedByDueDate(User user) {
        return taskRepository.findByUserOrderByDueDateAsc(user);
    }

    // Filter tasks by status or category for a specific user
    public List<Task> filterTasksByStatusOrCategory(User user, Long statusId, Long categoryId) {
        return taskRepository.findByUserAndStatusIdOrCategoryId(user, statusId, categoryId);
    }

    // Retrieve all tasks for a specific user with pagination
    public Page<Task> getAllTasksForUser(User user, Pageable pageable) {
        return taskRepository.findByUser(user, pageable);
    }

    // Filter tasks by status or category for a specific user with pagination
    public Page<Task> filterTasksByStatusOrCategory(User user, Long statusId, Long categoryId, Pageable pageable) {
        return taskRepository.findByUserAndStatusIdOrCategoryId(user, statusId, categoryId, pageable);
    }

    // Retrieve all tasks for a specific user with sorting by status and due date
    public Page<Task> getAllTasksForUserSorted(User user, Pageable pageable) {
        return taskRepository.findByUserOrderByStatusStatusDescDueDateAsc(user, pageable);
    }

    // Utility method: Validate that the due date is not in the past
    private LocalDate validateDueDate(LocalDate dueDate) {
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        return dueDate;
    }
}
