package com.example.recruiting_system.config;

import com.example.recruiting_system.security.User;
import com.example.recruiting_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            // Seed admin user - update password if exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_HR"));
                userRepository.save(admin);
            } else {
                // Update existing admin with new password
                User admin = userRepository.findByUsername("admin").get();
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_HR"));
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("hradmin").isEmpty()) {
                User hr = new User();
                hr.setUsername("hradmin");
                hr.setPassword(passwordEncoder.encode("hr123"));
                hr.setRoles(Arrays.asList("ROLE_HR"));
                userRepository.save(hr);
            }

            if (userRepository.findByUsername("recruiter1").isEmpty()) {
                User rec = new User();
                rec.setUsername("recruiter1");
                rec.setPassword(passwordEncoder.encode("rec123"));
                rec.setRoles(Arrays.asList("ROLE_RECRUITER"));
                userRepository.save(rec);
            }

            if (userRepository.findByUsername("hiringmanager").isEmpty()) {
                User hm = new User();
                hm.setUsername("hiringmanager");
                hm.setPassword(passwordEncoder.encode("hire123"));
                hm.setRoles(Arrays.asList("ROLE_HIRING_MANAGER"));
                userRepository.save(hm);
            }

            if (userRepository.findByUsername("onboard1").isEmpty()) {
                User ob = new User();
                ob.setUsername("onboard1");
                ob.setPassword(passwordEncoder.encode("onboard123"));
                ob.setRoles(Arrays.asList("ROLE_ONBOARDING"));
                userRepository.save(ob);
            }

            if (userRepository.findByUsername("testuser").isEmpty()) {
                User test = new User();
                test.setUsername("testuser");
                test.setPassword(passwordEncoder.encode("test123"));
                test.setRoles(Arrays.asList("ROLE_APPLICANT"));
                userRepository.save(test);
            }
        };
    }
}
