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
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskCategoryService taskCategoryService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private TaskPriorityService taskPriorityService;

    @Autowired
    private UserService userService;

    // Get the list of tasks for the authenticated user
    @GetMapping
    public String getTasksList(Authentication authentication, Model model,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) Long statusId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String search) {
        User user = userService.getUserByUsername(authentication.getName());

        int fixedSize = 6; // Set a fixed size for the page
        Pageable pageable = PageRequest.of(page, fixedSize);
        Page<Task> taskPage;

        // Apply filtering if category, status IDs, or search query are provided
        if (search != null && !search.isEmpty()) {
            taskPage = taskService.searchTasks(user, search, pageable);
        } else if (categoryId != null || statusId != null) {
            taskPage = taskService.filterTasksByStatusOrCategory(user, statusId, categoryId, pageable);
        } else {
            taskPage = taskService.getAllTasksForUserSorted(user, pageable);
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("search", search);

        return "task/task-list";
    }

    // Get task details
    @GetMapping("/{id}")
    public String getTaskDetails(@PathVariable Long id, Authentication authentication, Model model) {
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


    // Get the form to create a new task
    @GetMapping("/new")
    public String getCreateTaskForm(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("username", user.getUsername());

        model.addAttribute("task", new Task());
        model.addAttribute("categories", taskCategoryService.getAllCategories());
        model.addAttribute("priorities", taskPriorityService.getAllPriorities());

        return "task/task-edit";
    }

    // Helper method to add form attributes
    private void addFormAttributes(Model model) {
        model.addAttribute("categories", taskCategoryService.getAllCategories());
        model.addAttribute("statuses", taskStatusService.getAllStatuses());
        model.addAttribute("priorities", taskPriorityService.getAllPriorities());
    }

    // Handle form submission for creating a new task
    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             Authentication authentication,
                             Model model) {
        if (result.hasErrors()) {
            addFormAttributes(model);
            return "task/task-edit";
        }

        User user = userService.getUserByUsername(authentication.getName());

        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate();

        if (task.getDueDate().isEqual(currentDate)) {
            if (task.getDueTime() != null) {
                LocalDateTime dueDateTime = LocalDateTime.of(task.getDueDate(), task.getDueTime());
                if (dueDateTime.isBefore(now)) {
                    result.rejectValue("dueTime", "error.task", "Due time must be in the future for today's tasks");
                    addFormAttributes(model);
                    return "task/task-edit";
                }
            }
        }
        else if (task.getDueDate().isBefore(currentDate)) {
            result.rejectValue("dueDate", "error.task", "Due date must be in the future");
            addFormAttributes(model);
            return "task/task-edit";
        }

        // Set default status for new tasks
        Optional<TaskStatus> defaultStatusOptional = taskStatusService.getStatusByName("In progress");
        if (defaultStatusOptional.isPresent()) {
            task.setStatus(defaultStatusOptional.get());
        } else {
            result.rejectValue("status", "error.task", "Default status not found");
            addFormAttributes(model);
            return "task/task-edit";
        }

        taskService.createTask(task, user);

        return "redirect:/tasks";
    }


    // Get the form to edit an updating task
    @GetMapping("/edit/{id}")
    public String getEditTaskForm(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        Task task = taskService.getTaskByIdForUser(id, user).orElse(null);

        if (task == null) {
            return "redirect:/tasks";
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("task", task);
        addFormAttributes(model);

        return "task/task-edit";
    }

    // Handle form submission for updating a task
    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Long id,
                           @Valid @ModelAttribute("task") Task updatedTask,
                           BindingResult result,
                           Authentication authentication,
                           Model model) {
        if (result.hasErrors()) {
            addFormAttributes(model);
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

    // Handle updating the status of a task
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
}
