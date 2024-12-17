package com.example.lab5.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class TwoFactorAuthService {

    private static final SecureRandom random = new SecureRandom();

    // Генерация 6-значного кода 2FA
    public String generate2FACode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // Проверка введённого пользователем кода
    public boolean verify2FACode(String inputCode, String actualCode) {
        return inputCode != null && inputCode.equals(actualCode);
    }

    // Отправка кода 2FA на email пользователя
    public void send2FACode(String email, String code) {
        // Здесь должна быть реализация отправки кода на email
        // Например, используя SendGrid, JavaMail или другой почтовый сервис
        System.out.println("Sending 2FA code " + code + " to email: " + email);
    }
}
