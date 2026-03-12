package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.JobPosition;
import com.example.recruiting_system.security.User;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class ApplicationController {

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ApplicationService applicationService;

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    // Show apply form (applicant only)
    @GetMapping("/apply/{jobId}")
    public String applyForm(@PathVariable String jobId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRoles().contains("ROLE_APPLICANT")) {
            return "redirect:/access-denied";
        }

        JobPosition pos = jobPositionService.findById(jobId).orElse(new JobPosition());
        model.addAttribute("position", pos);
        model.addAttribute("username", user.getUsername());
        return "apply";
    }

    // Handle application submission
    @PostMapping("/apply/{jobId}")
    public String submitApplication(@PathVariable String jobId,
                                    @RequestParam String fullName,
                                    @RequestParam String email,
                                    @RequestParam String phone,
                                    @RequestParam(required = false) MultipartFile resume,
                                    @RequestParam String experience,
                                    @RequestParam String skills,
                                    HttpSession session,
                                    Model model) throws IOException {

        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRoles().contains("ROLE_APPLICANT")) {
            return "redirect:/access-denied";
        }

        Optional<JobPosition> posOpt = jobPositionService.findById(jobId);
        JobPosition pos = posOpt.orElse(new JobPosition());

        // Ensure upload directory exists
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String resumePath = null;
        if (resume != null && !resume.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + resume.getOriginalFilename();
                Path target = uploadDir.resolve(filename);
                resume.transferTo(target.toFile());
                resumePath = "/uploads/" + filename;  // Store as web-path
            } catch (IOException e) {
                e.printStackTrace();
                // Continue without resume if upload fails
                resumePath = null;
            }
        }

        Application app = new Application();
        app.setJobId(jobId);
        app.setJobTitle(pos.getTitle());
        app.setApplicantUsername(user.getUsername());
        app.setUserId(user.getId()); // Add userId for ApplicantProfile lookup
        app.setFullName(fullName);
        app.setEmail(email);
        app.setPhone(phone);
        app.setResumePath(resumePath);
        app.setExperience(experience);
        app.setSkills(skills);
        app.setStatus("Submitted");
        app.setAppliedDate(new java.util.Date());

        applicationService.create(app);

        return "redirect:/application-status";
    }

    // Show applications for current applicant
    @GetMapping("/application-status")
    public String statusPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRoles().contains("ROLE_APPLICANT")) {
            return "redirect:/access-denied";
        }
        List<Application> apps = applicationService.findByApplicant(user.getUsername());
        model.addAttribute("applications", apps);
        return "application_status";
    }
}
