package com.example.lab5.service;

import com.example.lab5.model.TaskCategory;
import com.example.lab5.repository.TaskCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskCategoryService {

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;

    // Get all task categories
    public List<TaskCategory> getAllCategories() {
        return taskCategoryRepository.findAll();
    }

    // Get a task category by ID
    public Optional<TaskCategory> getCategoryById(Long categoryId) {
        return taskCategoryRepository.findById(categoryId);
    }

    // Create a new task category
    public TaskCategory createCategory(TaskCategory taskCategory) {
        if (taskCategoryRepository.findByName(taskCategory.getName()).isPresent()) {
            throw new IllegalArgumentException("Category name already exists");
        }
        return taskCategoryRepository.save(taskCategory);
    }

    // Update an existing task category
    public TaskCategory updateCategory(Long id, TaskCategory updatedTaskCategory) {
        TaskCategory taskCategory = taskCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        taskCategory.setName(updatedTaskCategory.getName());
        return taskCategoryRepository.save(taskCategory);
    }

    // Delete a task category by ID
    public void deleteCategory(Long categoryId) {
        taskCategoryRepository.deleteById(categoryId);
    }
}
