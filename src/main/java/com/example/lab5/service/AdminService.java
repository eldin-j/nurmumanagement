package com.example.lab5.service;

import com.example.lab5.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskCategoryService taskCategoryService;

    @Autowired
    private TaskPriorityService taskPriorityService;

    @Autowired
    private TaskStatusService taskStatusService;

    // Get all users
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Delete a user by ID
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }


    // Get all task categories
    public List<TaskCategory> getAllTaskCategories() {
        return taskCategoryService.getAllCategories();
    }

    // Create a new task category
    public TaskCategory createTaskCategory(TaskCategory taskCategory) {
        return taskCategoryService.createCategory(taskCategory);
    }

    // Edit an existing task category
    public TaskCategory updateTaskCategory(Long id, TaskCategory updatedTaskCategory) {
        return taskCategoryService.updateCategory(id, updatedTaskCategory);
    }

    // Delete a task category by ID
    public void deleteTaskCategory(Long id) {
        taskCategoryService.deleteCategory(id);
    }


    // Get all task priorities
    public List<TaskPriority> getAllTaskPriorities() {
        return taskPriorityService.getAllPriorities();
    }

    // Create a new task priority
    public TaskPriority createTaskPriority(TaskPriority taskPriority) {
        return taskPriorityService.createPriority(taskPriority);
    }

    // Edit an existing task priority
    public TaskPriority updateTaskPriority(Long id, TaskPriority updatedTaskPriority) {
        return taskPriorityService.updatePriority(id, updatedTaskPriority);
    }

    // Delete a task priority by ID
    public void deleteTaskPriority(Long id) {
        taskPriorityService.deletePriority(id);
    }
}