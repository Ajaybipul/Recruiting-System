package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.Interview;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Controller
public class InterviewController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InterviewService interviewService;

    @GetMapping("/recruiter/interview/schedule/{applicationId}")
    public String showScheduleForm(@PathVariable String applicationId, Model model) {
        Application app = applicationService.findById(applicationId);
        model.addAttribute("application", app);
        return "interview_schedule";
    }

  @PostMapping("/recruiter/interview/schedule")
  public String scheduleInterview(
          @RequestParam String applicationId,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
  ) {
      System.out.println("\n==== POST /recruiter/interview/schedule ====");
      System.out.println("Received applicationId: '" + applicationId + "'");
      System.out.println("Received date: " + date);
      System.out.println("Received time: " + time);

      if (applicationId == null || applicationId.trim().isEmpty()) {
          System.out.println("ERROR: applicationId is empty/null!");
          return "redirect:/recruiter/dashboard";
      }

      Application app = applicationService.findById(applicationId);
      if (app == null) {
          System.out.println("ERROR: Application not found for id: " + applicationId);
          return "redirect:/recruiter/dashboard";
      }
      System.out.println("SUCCESS: Found application - " + app.getFullName());

    // Create interview
    Interview interview = new Interview();
   interview.setCandidateId(app.getId());   // Use Application ID

    interview.setJobPositionId(app.getJobId());

    // Convert LocalDate + LocalTime → Date
    Date scheduledAt = Date.from(
            date.atTime(time)
                .atZone(ZoneId.systemDefault())
                .toInstant()
    );
    interview.setScheduledAt(scheduledAt);

    Interview saved = interviewService.schedule(interview);

    // Update application status
    app.setInterviewId(saved.getId());
    app.setInterviewDate(date);
    app.setInterviewTime(time);
    app.setStatus("Interview Scheduled");
    applicationService.updateApplication(app);

    System.out.println("Interview saved with ID: " + saved.getId());

    return "redirect:/recruiter/dashboard";
}


    @GetMapping("/recruiter/interview/{interviewId}/details")
    public String viewInterviewDetails(@PathVariable String interviewId, Model model) {
        Interview i = interviewService.findById(interviewId).orElse(null);
        model.addAttribute("interview", i);
        return "interview_details";
    }
}
