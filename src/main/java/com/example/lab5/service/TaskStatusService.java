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

    // Get all task statuses
    public List<TaskStatus> getAllStatuses() {
        return taskStatusRepository.findAll();
    }

    // Get a task status by ID
    public Optional<TaskStatus> getStatusById(Long statusId) {
        return taskStatusRepository.findById(statusId);
    }

    // Get a task status by name
    public Optional<TaskStatus> getStatusByName(String status) {
        return taskStatusRepository.findByStatus(status);
    }

    // Create a new task status
    public TaskStatus createStatus(TaskStatus taskStatus) {
        if (taskStatusRepository.findByStatus(taskStatus.getStatus()).isPresent()) {
            throw new IllegalArgumentException("Task status already exists");
        }
        return taskStatusRepository.save(taskStatus);
    }

    // Update an existing task status
    public TaskStatus updateStatus(Long id, TaskStatus updatedTaskStatus) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
        taskStatus.setStatus(updatedTaskStatus.getStatus());
        return taskStatusRepository.save(taskStatus);
    }

    // Delete a task status by ID
    public void deleteStatus(Long statusId) {
        taskStatusRepository.deleteById(statusId);
    }
}
