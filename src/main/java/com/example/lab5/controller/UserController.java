package com.example.lab5.controller;

import com.example.lab5.model.User;
import com.example.lab5.repository.UserRepository;
import com.example.lab5.service.UserService;
import com.example.lab5.service.TwoFactorAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    // Регистрация пользователя
    @GetMapping("/signup")
    public String getRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/signup";
    }

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

    // Страница успеха
    @GetMapping("/success")
    public String getSuccessPage() {
        return "auth/success";
    }

    // Страница логина
    @GetMapping("/login")
    public String getLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        User user = userService.getUserByUsername(username);

        if (user != null && userService.checkPassword(user, password)) {
            if (user.isTwoFactorEnabled()) {
                // Генерируем временный код и сохраняем в сессии
                String generatedCode = twoFactorAuthService.generate2FACode();
                session.setAttribute("2faCode", generatedCode);
                session.setAttribute("2faUsername", username);

                // Отправляем код пользователю (e.g., email/SMS)
                twoFactorAuthService.send2FACode(user.getEmail(), generatedCode);

                return "redirect:/2fa"; // Перенаправление на страницу ввода кода
            }
            // Если 2FA выключен, пользователь сразу входит
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        }
    }

    // Страница для ввода 2FA-кода
    @GetMapping("/2fa")
    public String getTwoFactorPage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("2faUsername");
        if (username != null) {
            model.addAttribute("username", username);
            return "auth/2fa";
        }
        return "redirect:/login";
    }

    @PostMapping("/verify-2fa")
    public String verifyTwoFactorCode(@RequestParam String twoFactorCode, HttpSession session, Model model) {
        String generatedCode = (String) session.getAttribute("2faCode");
        String username = (String) session.getAttribute("2faUsername");

        if (generatedCode != null && generatedCode.equals(twoFactorCode)) {
            session.removeAttribute("2faCode");
            session.removeAttribute("2faUsername");
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Invalid 2FA code");
            model.addAttribute("username", username);
            return "auth/2fa";
        }
    }

    // Профиль пользователя
    @GetMapping("/profile")
    public String getProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getUserByUsername(authentication.getName());
            model.addAttribute("user", user);
            model.addAttribute("avatarUrl", "/avatar/" + user.getUsername());
            model.addAttribute("twoFactorStatus", user.isTwoFactorEnabled()); // Передача статуса 2FA
        }
        return "user/profile";
    }

    // Включение 2FA
    @PostMapping("/enable-2fa")
    public String enableTwoFactorAuthentication(Authentication authentication) {
        userService.enableTwoFactorAuthentication(authentication.getName());
        return "redirect:/profile";
    }

    // Отключение 2FA
    @PostMapping("/disable-2fa")
    public String disableTwoFactorAuthentication(Authentication authentication) {
        userService.disableTwoFactorAuthentication(authentication.getName());
        return "redirect:/profile";
    }

    // Проверка статуса 2FA
    @GetMapping("/2fa-status")
    @ResponseBody
    public String getTwoFactorStatus(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return user.isTwoFactorEnabled() ? "Two-Factor Authentication is ENABLED" : "Two-Factor Authentication is DISABLED";
    }

    // Загрузка аватара
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, Authentication authentication) {
        try {
            User user = userService.getUserByUsername(authentication.getName());
            user.setAvatar(file.getBytes());
            userService.saveUser(user);
            return "redirect:/profile";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    @GetMapping("/avatar/{username}")
    @ResponseBody
    public byte[] getAvatar(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return user.getAvatar();
    }
}
