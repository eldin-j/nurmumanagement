package com.example.lab5.controller;

import com.example.lab5.model.Task;
import com.example.lab5.model.User;
import com.example.lab5.service.EmailService;
import com.example.lab5.service.TaskService;
import com.example.lab5.service.UserService;
import com.example.lab5.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    // Send password recovery email
    @PostMapping("/password-recovery")
    public String handlePasswordRecovery(@RequestParam("email") String email, Model model, HttpSession session) {
        String token = TokenGenerator.generateToken();
        session.setAttribute("resetToken", token);
        session.setAttribute("resetEmail", email);
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        String subject = "Password Recovery";
        String body = "Click the following link to reset your password: " + resetLink;

        try {
            emailService.sendEmail(email, subject, body);
            model.addAttribute("message", "Password recovery email sent.");
        } catch (IOException e) {
            model.addAttribute("error", "Failed to send email.");
        }

        return "auth/password-recovery";
    }

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    public void sendReminders() {
        List<User> users = userService.getAllUsers();
        LocalDateTime now = LocalDateTime.now();

        for (User user : users) {
            List<Task> tasks = taskService.getAllTasksForUser(user);
            for (Task task : tasks) {
                LocalDateTime dueDateTime = LocalDateTime.of(
                        task.getDueDate(), task.getDueTime() != null ? task.getDueTime() : LocalTime.MIDNIGHT);

                LocalDateTime oneWeekBefore = dueDateTime.minusWeeks(1);
                LocalDateTime oneDayBefore = dueDateTime.minusDays(1);
                LocalDateTime oneHourBefore = dueDateTime.minusHours(1);

                sendReminder(task, oneWeekBefore, now, "One week");
                sendReminder(task, oneDayBefore, now, "One day");
                sendReminder(task, oneHourBefore, now, "One hour");
                sendReminder(task, dueDateTime, now, "Due now");
            }
        }
    }

    private void sendReminder(Task task, LocalDateTime reminderTime, LocalDateTime now, String reminderType) {
        if (reminderTime.isBefore(now) && reminderTime.plusMinutes(1).isAfter(now)) {
            String email = task.getUser().getEmail();
            String subject = "Task Reminder: " + task.getTitle();
            String body = "This is a reminder that your task \"" + task.getTitle() + "\" is due in " + reminderType + ".";
            try {
                System.out.println("Sending reminder email to " + email);
                emailService.sendEmail(email, subject, body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}