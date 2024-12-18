package com.example.lab5.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "task_priority")
public class TaskPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String priority;

    // Relationship with tasks for querying purposes
    @OneToMany(mappedBy = "priority", cascade = CascadeType.ALL)
    private List<Task> tasks;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return priority;
    }
}
