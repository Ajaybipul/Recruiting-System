package com.example.recruiting_system.service;

import com.example.recruiting_system.repository.UserRepository;
import com.example.recruiting_system.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Validate user credentials (username and password).
     * Returns an Optional with the User if valid, empty if invalid.
     * 
     * @param username the provided username
     * @param password the provided plaintext password
     * @return Optional<User> if credentials match, empty otherwise
     */
    public Optional<User> validateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Check if provided password matches stored BCrypt hash
        if (passwordEncoder.matches(password, user.getPassword())) {
            return Optional.of(user);
        }
        
        return Optional.empty();
    }

    public User createDefaultUsers() {
        // create an admin user if not exists
        Optional<User> admin = userRepository.findByUsername("admin");
        if (admin.isPresent()) return admin.get();
        User u = new User();
        u.setUsername("admin");
        u.setPassword(passwordEncoder.encode("admin123"));
        u.setRoles(Arrays.asList("ROLE_ADMIN","ROLE_RECRUITER","ROLE_HR"));
        return userRepository.save(u);
    }
}
