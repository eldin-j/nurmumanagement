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

    // Retrieve all task priorities
    public List<TaskPriority> getAllPriorities() {
        return taskPriorityRepository.findAll();
    }

    // Retrieve a task priority by ID
    public Optional<TaskPriority> getPriorityById(Long priorityId) {
        return taskPriorityRepository.findById(priorityId);
    }

    // Add a new task priority
    public TaskPriority addPriority(TaskPriority taskPriority) {
        // Ensure priority name is unique before saving
        if (taskPriorityRepository.findByPriority(taskPriority.getPriority()).isPresent()) {
            throw new IllegalArgumentException("Task priority already exists");
        }
        return taskPriorityRepository.save(taskPriority);
    }

    // Delete a task priority by ID (optional functionality)
    public void deletePriority(Long priorityId) {
        taskPriorityRepository.deleteById(priorityId);
    }
}
