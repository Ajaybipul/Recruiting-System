package com.example.recruiting_system.controller;

import com.example.recruiting_system.security.User;
import com.example.recruiting_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // Validate user credentials
        Optional<User> userOpt = userService.validateUser(username, password);
        
        if (userOpt.isEmpty()) {
            // Invalid credentials
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        User user = userOpt.get();
        
        // Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Save user in session
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("roles", user.getRoles());
        
        // Store Spring Security context in session
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        // Redirect based on primary role
        String redirect = getRoleBasedRedirect(user.getRoles());
        return "redirect:" + redirect;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Determine redirect URL based on user roles
     */
    private String getRoleBasedRedirect(java.util.List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return "/admin/dashboard";
        }
        if (roles.contains("ROLE_HR")) {
            return "/hr/dashboard";
        }
        if (roles.contains("ROLE_HIRING_MANAGER")) {
            return "/hiring-manager/dashboard";
        }
        if (roles.contains("ROLE_RECRUITER")) {
            return "/recruiter/dashboard";
        }
        if (roles.contains("ROLE_ONBOARDING")) {
            return "/onboarding/dashboard";
        }
        if (roles.contains("ROLE_APPLICANT")) {
            return "/jobs";
        }
        // default fallback
        return "/";
    }
}
