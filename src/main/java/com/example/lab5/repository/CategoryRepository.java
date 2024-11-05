package com.example.lab5.repository;

import com.example.lab5.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find a category by its name (useful for ensuring uniqueness)
    Optional<Category> findByName(String name);
}
