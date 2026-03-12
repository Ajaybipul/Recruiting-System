package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.JobPosition;
import com.example.recruiting_system.service.JobPositionService;
import com.example.recruiting_system.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("")
    public String list(Model model, HttpSession session) {
        List<JobPosition> jobs = jobPositionService.findPublished();
        model.addAttribute("jobs", jobs);
        
        // Get applied job IDs for current user (if logged in as applicant)
        List<String> appliedJobIds = null;
        Object username = session.getAttribute("username");
        if (username != null) {
            appliedJobIds = applicationService.findByApplicant((String) username)
                    .stream()
                    .map(app -> app.getJobId())
                    .collect(Collectors.toList());
        }
        model.addAttribute("appliedJobIds", appliedJobIds != null ? appliedJobIds : List.of());
        
        return "jobs";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable String id, Model model, HttpSession session) {
        JobPosition pos = jobPositionService.findById(id).orElse(new JobPosition());
        model.addAttribute("job", pos);
        
        // Get applied job IDs for current user
        List<String> appliedJobIds = null;
        Object username = session.getAttribute("username");
        if (username != null) {
            appliedJobIds = applicationService.findByApplicant((String) username)
                    .stream()
                    .map(app -> app.getJobId())
                    .collect(Collectors.toList());
        }
        model.addAttribute("appliedJobIds", appliedJobIds != null ? appliedJobIds : List.of());
        
        return "job_details";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        JobPosition pos = jobPositionService.findById(id).orElse(new JobPosition());
        model.addAttribute("position", pos);
        return "job_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable String id, JobPosition jobPosition) {
        jobPosition.setId(id);
        jobPositionService.save(jobPosition);
        return "redirect:/recruiter/my-posts";
    }
}
