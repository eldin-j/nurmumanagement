package com.example.lab5.service;

import com.example.lab5.model.User;
import com.example.lab5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by username
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Register a new user
    @Transactional
    public void register(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    // Update user details
    @Transactional
    public void update(User user) {
        userRepository.save(user);
    }

    // Delete a user by ID
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // Enable two-factor authentication
    @Transactional
    public void enableTwoFactorAuthentication(String username) {
        User user = getUserByUsername(username);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    // Disable two-factor authentication
    @Transactional
    public void disableTwoFactorAuthentication(String username) {
        User user = getUserByUsername(username);
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
    }

    // Load user by username
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    // Helper method to update password by email (used in the forgot password feature)
    @Transactional
    public void updatePasswordByEmail(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
    }

    // Check if username already exists
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Check if email already exists
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Check if the provided password matches the user's current password
    @Transactional(readOnly = true)
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // Encode a raw password
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
