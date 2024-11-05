package com.example.lab5.service;

import com.example.lab5.model.TaskStatus;
import com.example.lab5.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    // Retrieve all task statuses
    public List<TaskStatus> getAllStatuses() {
        return taskStatusRepository.findAll();
    }

    // Retrieve a task status by ID
    public Optional<TaskStatus> getStatusById(Long statusId) {
        return taskStatusRepository.findById(statusId);
    }

    // Add a new task status
    public TaskStatus addStatus(TaskStatus taskStatus) {
        // Ensure status name is unique before saving
        if (taskStatusRepository.findByStatus(taskStatus.getStatus()).isPresent()) {
            throw new IllegalArgumentException("Task status already exists");
        }
        return taskStatusRepository.save(taskStatus);
    }

    // Delete a task status by ID (optional functionality)
    public void deleteStatus(Long statusId) {
        taskStatusRepository.deleteById(statusId);
    }
}
