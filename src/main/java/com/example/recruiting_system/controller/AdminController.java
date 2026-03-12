package com.example.recruiting_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.recruiting_system.repository.UserRepository;
import com.example.recruiting_system.repository.JobPositionRepository;
import com.example.recruiting_system.repository.ApplicationRepository;
import com.example.recruiting_system.security.User;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * Check if user is admin
     */
    private boolean isAdmin(HttpSession session) {
        Object rolesObj = session.getAttribute("roles");
        if (rolesObj instanceof List) {
            List<String> roles = (List<String>) rolesObj;
            return roles.contains("ROLE_ADMIN");
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Get statistics
        long totalUsers = userRepository.count();
        long totalJobs = jobPositionRepository.count();
        long totalApplications = applicationRepository.count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("activeJobs", jobPositionRepository.count()); // Can be filtered by status

        return "admin_dashboard";
    }

    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", Arrays.asList("ROLE_ADMIN", "ROLE_HR", "ROLE_RECRUITER", "ROLE_HIRING_MANAGER", "ROLE_ONBOARDING", "ROLE_APPLICANT"));

        return "admin_user_management";
    }

    @GetMapping("/jobs")
    public String jobManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("jobs", jobPositionRepository.findAll());
        return "admin_jobs_list";
    }

    @GetMapping("/roles-permissions")
    public String rolesAndPermissions(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("roles", Arrays.asList(
            "ROLE_ADMIN", "ROLE_HR", "ROLE_RECRUITER", 
            "ROLE_HIRING_MANAGER", "ROLE_ONBOARDING", "ROLE_APPLICANT"
        ));

        return "admin_roles_permissions";
    }

    @GetMapping("/system-config")
    public String systemConfig(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        return "admin_system_config";
    }

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("totalApplications", applicationRepository.count());
        model.addAttribute("totalJobs", jobPositionRepository.count());
        model.addAttribute("totalUsers", userRepository.count());

        return "admin_reports";
    }

    @GetMapping("/announcements")
    public String announcements(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("roles", Arrays.asList(
            "ROLE_ADMIN", "ROLE_HR", "ROLE_RECRUITER", 
            "ROLE_HIRING_MANAGER", "ROLE_ONBOARDING", "ROLE_APPLICANT"
        ));

        return "admin_announcements";
    }

    @GetMapping("/logs")
    public String activityLogs(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Placeholder for logs - would need an ActivityLog entity
        model.addAttribute("logs", Arrays.asList());

        return "admin_logs";
    }

    @GetMapping("/users/{username}/edit")
    public String editUser(@PathVariable String username, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user.get());
        model.addAttribute("roles", Arrays.asList("ROLE_ADMIN", "ROLE_HR", "ROLE_RECRUITER", "ROLE_HIRING_MANAGER", "ROLE_ONBOARDING", "ROLE_APPLICANT"));

        return "admin_user_edit";
    }

    @PostMapping("/users/{username}/update")
    public String updateUser(@PathVariable String username, 
                            @RequestParam String email,
                            @RequestParam(required = false) String[] roles,
                            HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setEmail(email);
            if (roles != null) {
                foundUser.setRoles(Arrays.asList(roles));
            }
            userRepository.save(foundUser);
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/{username}/reset-password")
    public String resetPasswordPage(@PathVariable String username, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user.get());
        return "admin_user_reset_password";
    }

    @PostMapping("/users/{username}/reset-password")
    public String resetPassword(@PathVariable String username, 
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            var user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }
            return "admin_user_reset_password";
        }

        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setPassword(newPassword); // Should be hashed in production
            userRepository.save(foundUser);
        }

        return "redirect:/admin/users";
    }
}

