package com.example.lab5.controller;

import com.example.lab5.service.EmailService;
import com.example.lab5.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

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
}