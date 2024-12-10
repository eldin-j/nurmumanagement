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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Get the registration form
    @GetMapping("/signup")
    public String getRegistrationForm(Model model) {
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

    // Get the success page after registration
    @GetMapping("/success")
    public String getSuccessPage() {
        return "auth/success";
    }

    // Get the login form
    @GetMapping("/login")
    public String getLoginForm() {
        return "auth/login";
    }


    // Get the user profile
    @GetMapping("/profile")
    public String getProfile(Model model, Authentication authentication) {
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

    // Get the password recovery form
    @GetMapping("/password-recovery")
    public String getPasswordRecoveryForm() {
        return "auth/password-recovery";
    }

    // Get the reset password form (accessed via email link)
    @GetMapping("/reset-password")
    public String getResetPasswordForm(@RequestParam("token") String token, Model model, HttpSession session) {
        String sessionToken = (String) session.getAttribute("resetToken");
        if (sessionToken == null || !sessionToken.equals(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "auth/reset-password";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    // Handle password reset request
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model, HttpSession session) {
        String sessionToken = (String) session.getAttribute("resetToken");
        String email = (String) session.getAttribute("resetEmail");
        if (sessionToken == null || !sessionToken.equals(token) || email == null) {
            model.addAttribute("error", "Invalid or expired token.");
            return "auth/reset-password";
        }

        if (password != null && !password.isEmpty()) {
            userService.updatePasswordByEmail(email, password);
            session.removeAttribute("resetToken");
            session.removeAttribute("resetEmail");
            model.addAttribute("message", "Password has been reset successfully.");
        } else {
            model.addAttribute("error", "Password cannot be empty.");
        }

        return "auth/reset-password";
    }
}
