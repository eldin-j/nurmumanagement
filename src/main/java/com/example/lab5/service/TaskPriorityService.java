package com.example.lab5.service;

import com.example.lab5.model.TaskPriority;
import com.example.lab5.repository.TaskPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskPriorityService {

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    // Get all task priorities
    public List<TaskPriority> getAllPriorities() {
        return taskPriorityRepository.findAll();
    }

    // Get a task priority by ID
    public Optional<TaskPriority> getPriorityById(Long priorityId) {
        return taskPriorityRepository.findById(priorityId);
    }

    // Create a new task priority
    public TaskPriority createPriority(TaskPriority taskPriority) {
        // Ensure priority name is unique before saving
        if (taskPriorityRepository.findByPriority(taskPriority.getPriority()).isPresent()) {
            throw new IllegalArgumentException("Task priority already exists");
        }
        return taskPriorityRepository.save(taskPriority);
    }

    // Update an existing task priority
    public TaskPriority updatePriority(Long id, TaskPriority updatedTaskPriority) {
        TaskPriority taskPriority = taskPriorityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid priority ID"));
        taskPriority.setPriority(updatedTaskPriority.getPriority());
        return taskPriorityRepository.save(taskPriority);
    }

    // Delete a task priority by ID
    public void deletePriority(Long priorityId) {
        taskPriorityRepository.deleteById(priorityId);
    }
}
