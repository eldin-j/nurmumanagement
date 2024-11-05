package com.example.lab5.repository;

import com.example.lab5.model.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {

    // Find a priority by its name (useful for ensuring uniqueness)
    Optional<TaskPriority> findByPriority(String priority);
}
