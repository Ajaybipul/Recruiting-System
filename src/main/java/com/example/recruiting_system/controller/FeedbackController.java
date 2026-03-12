package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FeedbackController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/recruiter/interview/feedback/{applicationId}")
    public String showFeedbackForm(@PathVariable String applicationId, Model model) {
        Application app = applicationService.findById(applicationId);
        model.addAttribute("application", app);
        return "interview_feedback";
    }

    @PostMapping("/recruiter/interview/feedback")
    public String submitFeedback(
            @RequestParam String applicationId,
            @RequestParam(required = false) Integer technicalScore,
            @RequestParam(required = false) Integer communicationScore,
            @RequestParam(required = false) Integer cultureFitScore,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) String recommendation
    ) {
        applicationService.submitFeedback(applicationId, technicalScore, communicationScore, cultureFitScore, notes, recommendation);
        return "redirect:/recruiter/applicant/" + applicationId;
    }

    // Old interview feedback endpoints - redirect to the correct hiring manager feedback system
    // These legacy endpoints came from the old interview_details.html page
    @GetMapping("/interview/{id}/feedback")
    public String interviewFeedbackRedirect(@PathVariable String id) {
        // The old endpoint doesn't have applicationId, so we can't redirect directly
        // Instead, redirect back to the interview details to inform user
        return "redirect:/hiring-manager/dashboard";
    }

    @PostMapping("/interview/{id}/feedback")
    public String interviewFeedbackSubmitRedirect(
            @PathVariable String id,
            @RequestParam(required = false) String notes) {
        System.out.println("Legacy interview feedback endpoint called with interview ID: " + id);
        // The old endpoint doesn't provide applicationId, so redirect to dashboard
        // User should use the proper feedback form on candidate detail page instead
        return "redirect:/hiring-manager/dashboard";
    }
}
