package com.example.lab5.repository;

import com.example.lab5.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    // Find a status by its name (useful for ensuring uniqueness)
    Optional<TaskStatus> findByStatus(String status);
}
