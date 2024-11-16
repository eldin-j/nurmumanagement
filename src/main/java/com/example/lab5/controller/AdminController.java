package com.example.lab5.controller;

import com.example.lab5.model.*;
import com.example.lab5.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Get the admin dashboard
    @GetMapping
    public String getAdminDashboard() {
        return "admin/admin-dashboard";
    }


    // Get the manage users page
    @GetMapping("/manage-users")
    public String getManageUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/manage-users";
    }

    // Handle deleting a user
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/manage-users";
    }


    // Get the manage task categories page
    @GetMapping("/manage-task-categories")
    public String getManageTaskCategories(Model model) {
        model.addAttribute("categories", adminService.getAllTaskCategories());
        model.addAttribute("taskCategory", new TaskCategory());
        return "admin/manage-task-categories";
    }

    // Handle creating a new task category
    @PostMapping("/task-categories/add")
    public String createTaskCategory(@ModelAttribute TaskCategory taskCategory) {
        adminService.createTaskCategory(taskCategory);
        return "redirect:/admin/manage-task-categories";
    }

    // Handle updating an existing task category
    @PostMapping("/task-categories/update/{id}")
    public String updateTaskCategory(@PathVariable Long id, @ModelAttribute TaskCategory taskCategory) {
        adminService.updateTaskCategory(id, taskCategory);
        return "redirect:/admin/manage-task-categories";
    }

    // Handle deleting an existing task category
    @GetMapping("/task-categories/delete/{id}")
    public String deleteTaskCategory(@PathVariable Long id) {
        adminService.deleteTaskCategory(id);
        return "redirect:/admin/manage-task-categories";
    }


    // Get the manage task priorities page
    @GetMapping("/manage-task-priorities")
    public String getManageTaskPriorities(Model model) {
        model.addAttribute("priorities", adminService.getAllTaskPriorities());
        model.addAttribute("taskPriority", new TaskPriority());
        return "admin/manage-task-priorities";
    }

    // Handle creating a new task priority
    @PostMapping("/task-priorities/add")
    public String createTaskPriority(@ModelAttribute TaskPriority taskPriority) {
        adminService.createTaskPriority(taskPriority);
        return "redirect:/admin/manage-task-priorities";
    }

    // Handle updating an existing task priority
    @PostMapping("/task-priorities/update/{id}")
    public String updateTaskPriority(@PathVariable Long id, @ModelAttribute TaskPriority taskPriority) {
        adminService.updateTaskPriority(id, taskPriority);
        return "redirect:/admin/manage-task-priorities";
    }

    // Handle deleting an existing task priority
    @GetMapping("/task-priorities/delete/{id}")
    public String deleteTaskPriority(@PathVariable Long id) {
        adminService.deleteTaskPriority(id);
        return "redirect:/admin/manage-task-priorities";
    }
}