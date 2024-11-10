package com.example.lab5.controller;

import com.example.lab5.model.Task;
import com.example.lab5.model.TaskStatus;
import com.example.lab5.model.User;
import com.example.lab5.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private TaskPriorityService taskPriorityService;

    @Autowired
    private UserService userService;

    // Display the list of tasks for the authenticated user
    @GetMapping
    public String listTasks(Authentication authentication, Model model,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) Long statusId,
                            @RequestParam(defaultValue = "0") int page) {
        User user = userService.getUserByUsername(authentication.getName());

        int fixedSize = 6; // Set a fixed size for the page
        Pageable pageable = PageRequest.of(page, fixedSize);
        Page<Task> taskPage;

        // Apply filtering if category or status IDs are provided
        if (categoryId != null || statusId != null) {
            taskPage = taskService.filterTasksByStatusOrCategory(user, statusId, categoryId, pageable);
        } else {
            taskPage = taskService.getAllTasksForUserSorted(user, pageable);
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        return "task/task-list";
    }

    // Show the form to create a new task
    @GetMapping("/new")
    public String showCreateTaskForm(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("username", user.getUsername());

        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.getAllCategories());
//        model.addAttribute("statuses", taskStatusService.getAllStatuses());
        model.addAttribute("priorities", taskPriorityService.getAllPriorities());

        return "task/task-edit";
    }

    // Handle form submission for creating a new task
    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             Authentication authentication,
                             Model model) {
        if (result.hasErrors()) {
            // Add necessary attributes for the form
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("statuses", taskStatusService.getAllStatuses());
            model.addAttribute("priorities", taskPriorityService.getAllPriorities());
            return "task/task-edit";
        }
        User user = userService.getUserByUsername(authentication.getName());
        if (task.getDueDate().isBefore(LocalDate.now())) {
            result.rejectValue("dueDate", "error.task", "Due date must be in the future");
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("statuses", taskStatusService.getAllStatuses());
            model.addAttribute("priorities", taskPriorityService.getAllPriorities());
            return "task/task-edit";
        }

        // Set default status for new tasks
        Optional<TaskStatus> defaultStatusOptional = taskStatusService.getStatusByName("In progress");
        if (defaultStatusOptional.isPresent()) {
            task.setStatus(defaultStatusOptional.get());
        } else {
            result.rejectValue("status", "error.task", "Default status not found");
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("statuses", taskStatusService.getAllStatuses());
            model.addAttribute("priorities", taskPriorityService.getAllPriorities());
            return "task/task-edit";
        }

        taskService.createTask(task, user);

        return "redirect:/tasks";
    }

    // Show the form to edit an existing task
    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        Task task = taskService.getTaskByIdForUser(id, user).orElse(null);

        if (task == null) {
            return "redirect:/tasks";
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", taskStatusService.getAllStatuses());
        model.addAttribute("priorities", taskPriorityService.getAllPriorities());

        return "task/task-edit";
    }


    // Handle form submission for editing a task
    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Long id,
                           @Valid @ModelAttribute("task") Task updatedTask,
                           BindingResult result,
                           Authentication authentication,
                           Model model) {
        if (result.hasErrors()) {
            // Add necessary attributes for the form
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("statuses", taskStatusService.getAllStatuses());
            model.addAttribute("priorities", taskPriorityService.getAllPriorities());
            return "task/task-edit";
        }

        User user = userService.getUserByUsername(authentication.getName());
        Task existingTask = taskService.getTaskByIdForUser(id, user).orElse(null);

        // Ensure that the task belongs to the authenticated user
        if (existingTask == null || existingTask.getUser() == null || !existingTask.getUser().equals(user)) {
            return "redirect:/tasks";
        }

        // Update task details and save
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());

        taskService.updateTask(id, existingTask, user);

        return "redirect:/tasks";
    }

    @PostMapping("/{action}/{id}")
    public String updateTaskStatus(@PathVariable String action, @PathVariable Long id, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Task task = taskService.getTaskByIdForUser(id, user).orElse(null);

        // Ensure that the task belongs to the authenticated user
        if (task != null && task.getUser() != null && task.getUser().equals(user)) {
            String statusName = action.equals("complete") ? "Completed" : "In progress";
            Optional<TaskStatus> statusOptional = taskStatusService.getStatusByName(statusName);
            if (statusOptional.isPresent()) {
                TaskStatus status = statusOptional.get();
                task.setStatus(status);
                taskService.updateTask(id, task, user);
            }
        }

        return "redirect:/tasks";
    }

    // Delete a task
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Task task = taskService.getTaskByIdForUser(id, user).orElse(null);


        // Ensure that the task belongs to the authenticated user
        if (task != null && task.getUser() != null && task.getUser().equals(user)) {
            taskService.deleteTask(id, user);
        }

        return "redirect:/tasks";
    }

    // Display task details
    @GetMapping("/{id}")
    public String viewTaskDetails(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        Task task = taskService.getTaskByIdForUser(id, user).orElse(null);

        // Ensure that the task belongs to the authenticated user
        if (task == null || task.getUser() == null || !task.getUser().equals(user)) {
            return "redirect:/tasks";
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("task", task);
        return "task/task-details";
    }
}
