package com.example.lab5.repository;

import com.example.lab5.model.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {

    // Find a category by its name (useful for ensuring uniqueness)
    Optional<TaskCategory> findByName(String name);
}
