package com.example.lab5.service;

import com.example.lab5.model.Category;
import com.example.lab5.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Retrieve all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Retrieve a category by ID
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    // Add a new category
    public Category addCategory(Category category) {
        // Ensure category name is unique before saving
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new IllegalArgumentException("Category name already exists");
        }
        return categoryRepository.save(category);
    }

    // Delete a category by ID (optional functionality)
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
