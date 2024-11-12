package com.example.lab5.controller;

import com.example.lab5.model.User;
import com.example.lab5.repository.UserRepository;
import com.example.lab5.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Show the registration form
    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/signup";
    }

    // Handle form submission for user registration
    @PostMapping("/signup")
    public String registerUser(User user, RedirectAttributes redirectAttributes) {
        if (userService.usernameExists(user.getUsername())) {
            redirectAttributes.addFlashAttribute("usernameError", "Username already exists");
            return "redirect:/signup";
        } else if (userService.emailExists(user.getEmail())) {
            redirectAttributes.addFlashAttribute("emailError", "Email already exists");
            return "redirect:/signup";
        }
        userService.register(user);
        redirectAttributes.addFlashAttribute("message", "Registration successful!");
        return "redirect:/success";
    }

    // Show the success page after registration
    @GetMapping("/success")
    public String showSuccessPage() {
        return "auth/success";
    }

    // Show the login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    // For testing session management
    @GetMapping("/test-session")
    public String testSession(Model model, HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("createdAt", user.getCreatedAt());
            model.addAttribute("sessionId", session.getId());
            model.addAttribute("sessionCreationTime", new Date(session.getCreationTime()));
            model.addAttribute("sessionLastAccessedTime", new Date(session.getLastAccessedTime()));
        }
        return "auth/test-session";
    }

    // Show the user profile
    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getUserByUsername(authentication.getName());
            model.addAttribute("user", user);
        }
        return "user/profile";
    }

    // Handle username update
    @PostMapping("/profile/edit-username")
    public String updateUsername(String newUsername, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User existingUser = userService.getUserByUsername(authentication.getName());

            existingUser.setUsername(newUsername);
            userService.update(existingUser);
        }
        return "redirect:/login?editSuccess=true";
    }

    // Handle email update
    @PostMapping("/profile/edit-email")
    public String updateEmail(String newEmail, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User existingUser = userService.getUserByUsername(authentication.getName());

            existingUser.setEmail(newEmail);
            userService.update(existingUser);
        }
        return "redirect:/profile?editSuccess=true";
    }

    // Handle password update
    @PostMapping("/profile/edit-password")
    public String updatePassword(String newPassword, String currentPassword, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User existingUser = userService.getUserByUsername(authentication.getName());

            if (!userService.checkPassword(existingUser, currentPassword)) {
                return "redirect:/profile?invalidPassword=true";
            }

            if (newPassword != null && !newPassword.isEmpty()) {
                String encodedPassword = userService.encodePassword(newPassword);
                existingUser.setPassword(encodedPassword);
                userService.update(existingUser);
            }
        }
        return "redirect:/login?editSuccess=true";
    }
}
