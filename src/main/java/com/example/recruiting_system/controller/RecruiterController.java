package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.JobPosition;
import com.example.recruiting_system.model.Interview;
import com.example.recruiting_system.model.Offer;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.JobPositionService;
import com.example.recruiting_system.service.InterviewService;
import com.example.recruiting_system.service.OfferService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private OfferService offerService;

   @GetMapping("/dashboard")
public String dashboard(Model model) {

    model.addAttribute("totalJobs", jobPositionService.count());
    model.addAttribute("totalApplicants", applicationService.count());
    model.addAttribute("shortlisted", applicationService.countByStatus("Shortlisted"));
    model.addAttribute("pendingInterviews", applicationService.countByStatus("Interview Scheduled"));

    model.addAttribute("recentApps", applicationService.findRecent(5));

    return "recruiter_dashboard";
}

    // View all applicants across all jobs
    @GetMapping("/applicants")
    public String allApplicants(Model model) {
        List<Application> apps = applicationService.findAll();
        model.addAttribute("applications", apps);
        model.addAttribute("jobId", null);
        return "recruiter_applicants";
    }

    @GetMapping("/job/{jobId}/applicants")
    public String applicantsForJob(@PathVariable String jobId, Model model) {
        // Handle empty or null jobId
        if (jobId == null || jobId.isEmpty()) {
            return "redirect:/recruiter/my-posts";
        }
        
        List<Application> apps = applicationService.findByJobId(jobId);
        model.addAttribute("applications", apps);
        model.addAttribute("jobId", jobId);
        return "recruiter_applicants";
    }

    @GetMapping("/applicant/{applicationId}")
    public String applicantDetail(@PathVariable String applicationId, Model model) {
        // Fetch application by ID
        Application app = applicationService.findById(applicationId);
        
        System.out.println("\n=== APPLICANT DETAIL DEBUG ===");
        if (app != null) {
            System.out.println("Application ID: " + app.getId());
            System.out.println("Full Name: " + app.getFullName());
            System.out.println("Email: " + app.getEmail());
            System.out.println("Phone: " + app.getPhone());
            System.out.println("Skills: " + app.getSkills());
            System.out.println("Resume Path: " + app.getResumePath());
            System.out.println("Experience: " + app.getExperience());
        } else {
            System.out.println("ERROR: Application not found!");
        }
        
        if (app == null) {
            return "redirect:/recruiter/dashboard";
        }
        
        // Fetch job details if jobId exists
        JobPosition job = null;
        if (app.getJobId() != null && !app.getJobId().isEmpty()) {
            job = jobPositionService.findById(app.getJobId()).orElse(null);
        }
        
        // Add application data to model explicitly
        model.addAttribute("application", app);
        System.out.println("Model attribute 'application' set: " + (model.asMap().get("application") != null));
        
        // Add all fields explicitly as well for debugging
        model.addAttribute("fullName", app.getFullName());
        model.addAttribute("email", app.getEmail());
        model.addAttribute("phone", app.getPhone());
        model.addAttribute("skills", app.getSkills());
        model.addAttribute("resumePath", app.getResumePath());
        model.addAttribute("experience", app.getExperience());
        
        // Add job data to model (if available)
        if (job != null) {
            model.addAttribute("job", job);
            model.addAttribute("jobTitle", job.getTitle());
            model.addAttribute("jobLocation", job.getLocation());
            model.addAttribute("jobDepartment", job.getDepartment());
            model.addAttribute("jobSalaryRange", job.getSalaryRange());
        }
        
        return "recruiter_applicant_detail";
    }
    // Show create job form
@GetMapping("/create")
public String createJobForm(Model model) {
    model.addAttribute("position", new JobPosition());
    return "job_create";
}

// Show only jobs created by the recruiter
@GetMapping("/my-posts")
public String myJobPosts(Model model) {
    model.addAttribute("positions", jobPositionService.findAll());
    return "recruiter_my_posts";  // <-- Your HTML page
}


    @PostMapping("/applicant/{id}/shortlist")
    public String shortlist(@PathVariable String id, @RequestParam(required = false) String jobId) {
        applicationService.shortlistApplicant(id);
        // If jobId not provided, fetch from application
        if (jobId == null || jobId.isEmpty()) {
            Application app = applicationService.findById(id);
            jobId = (app != null && app.getJobId() != null) ? app.getJobId() : "";
        }
        if (jobId == null || jobId.isEmpty()) {
            return "redirect:/recruiter/dashboard";
        }
        return "redirect:/recruiter/job/" + jobId + "/applicants";
    }

    @PostMapping("/applicant/{id}/reject")
    public String reject(@PathVariable String id, @RequestParam(required = false) String jobId) {
        applicationService.rejectApplicant(id);
        // If jobId not provided, fetch from application
        if (jobId == null || jobId.isEmpty()) {
            Application app = applicationService.findById(id);
            jobId = (app != null && app.getJobId() != null) ? app.getJobId() : "";
        }
        if (jobId == null || jobId.isEmpty()) {
            return "redirect:/recruiter/dashboard";
        }
        return "redirect:/recruiter/job/" + jobId + "/applicants";
    }

    @GetMapping("/applicant/{applicationId}/schedule")
    public String scheduleForm(@PathVariable String applicationId, Model model, HttpServletResponse response) {
        System.out.println("\n==== GET /recruiter/applicant/{applicationId}/schedule ====");
        System.out.println("Received applicationId: '" + applicationId + "'");
        
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        Application app = applicationService.findById(applicationId);
        
        if (app == null) {
            System.out.println("ERROR: Application not found for id=" + applicationId + ", redirecting to dashboard");
            return "redirect:/recruiter/dashboard";
        }
        
        model.addAttribute("candidateApplication", app);
        
        return "interview_schedule";
    }

    @PostMapping("/applicant/{applicationId}/schedule")
    public String scheduleInterview(
            @PathVariable String applicationId,
            @RequestParam(name = "date") String dateStr,
            @RequestParam(name = "time") String timeStr) {
        
        System.out.println("\n==== POST /recruiter/applicant/{applicationId}/schedule ====");
        System.out.println("Received applicationId: '" + applicationId + "'");
        System.out.println("Received date: " + dateStr);
        System.out.println("Received time: " + timeStr);

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

        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
            
            // Create interview
            com.example.recruiting_system.model.Interview interview = new com.example.recruiting_system.model.Interview();
            interview.setCandidateId(app.getId());
            interview.setJobPositionId(app.getJobId());
            
            // Convert LocalDate + LocalTime → Date
            java.util.Date scheduledAt = java.util.Date.from(
                    date.atTime(time)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant()
            );
            interview.setScheduledAt(scheduledAt);
            
            com.example.recruiting_system.model.Interview saved = interviewService.schedule(interview);
            
            // Update application status
            app.setInterviewId(saved.getId());
            app.setInterviewDate(date);
            app.setInterviewTime(time);
            app.setStatus("Interview Scheduled");
            applicationService.updateApplication(app);
            
            System.out.println("Interview saved with ID: " + saved.getId());
        } catch (Exception e) {
            System.out.println("ERROR scheduling interview: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/recruiter/dashboard";
    }



    @PostMapping("/applicant/{id}/forward")
    public String forward(@PathVariable String id, @RequestParam(required = false) String jobId) {
        applicationService.forwardToHiringManager(id);
        // If jobId not provided, fetch from application
        if (jobId == null || jobId.isEmpty()) {
            Application app = applicationService.findById(id);
            jobId = (app != null && app.getJobId() != null) ? app.getJobId() : "";
        }
        if (jobId == null || jobId.isEmpty()) {
            return "redirect:/recruiter/dashboard";
        }
        return "redirect:/recruiter/job/" + jobId + "/applicants";
    }

    @PostMapping("/applicant/{id}")
    public String updateApplicantFromList(@PathVariable String id,
                                         @RequestParam(required = false) String screeningNotes,
                                         @RequestParam(required = false) String action,
                                         @RequestParam(required = false) String jobId) {
        Application app = applicationService.findById(id);
        if (app != null) {
            // Save screening notes
            if (screeningNotes != null && !screeningNotes.isEmpty()) {
                app.setScreeningNotes(screeningNotes);
            }
            
            // Handle action (shortlist or reject)
            if ("shortlist".equals(action)) {
                applicationService.shortlistApplicant(id);
            } else if ("reject".equals(action)) {
                app.setStatus("Rejected");
                applicationService.updateApplication(app);
            } else {
                // Just save notes
                applicationService.updateApplication(app);
            }
        }
        
        // Get jobId from request or from application object
        String redirectJobId = jobId;
        if (redirectJobId == null || redirectJobId.isEmpty()) {
            redirectJobId = (app != null && app.getJobId() != null) ? app.getJobId() : "";
        }
        
        // If still no jobId, redirect to applicants list instead
        if (redirectJobId == null || redirectJobId.isEmpty()) {
            return "redirect:/recruiter/applicants";
        }
        
        return "redirect:/recruiter/job/" + redirectJobId + "/applicants";
    }

    @PostMapping("/applicant/{id}/update")
    public String updateApplicant(@PathVariable String id,
                                  @RequestParam(required = false) String screeningNotes,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) String jobId) {
        Application app = applicationService.findById(id);
        if (app != null) {
            if (screeningNotes != null) app.setScreeningNotes(screeningNotes);
            if (status != null) app.setStatus(status);
            applicationService.updateApplication(app);
        }
        
        // Get jobId from request or from application object
        String redirectJobId = jobId;
        if (redirectJobId == null || redirectJobId.isEmpty()) {
            redirectJobId = (app != null && app.getJobId() != null) ? app.getJobId() : "";
        }
        
        // If still no jobId, redirect to applicants list instead
        if (redirectJobId == null || redirectJobId.isEmpty()) {
            return "redirect:/recruiter/applicants";
        }
        
        return "redirect:/recruiter/job/" + redirectJobId + "/applicants";
    }
    @PostMapping("/create")
public String createJob(@ModelAttribute JobPosition position) {

    jobPositionService.create(position);

    return "redirect:/recruiter/my-posts";
}

    // ============================================
    // OFFER MANAGEMENT (Recruiter)
    // ============================================
    @GetMapping("/offers")
    public String myOffers(Model model) {
        List<Offer> offers = offerService.findAll();
        model.addAttribute("offers", offers);
        return "recruiter_offers_list";
    }

    @GetMapping("/offer/create/{applicationId}")
    public String createOfferForm(@PathVariable String applicationId, Model model) {
        Application app = applicationService.findById(applicationId);
        if (app == null || !app.getStatus().equals("Shortlisted")) {
            return "redirect:/recruiter/applicants";
        }
        model.addAttribute("candidateApplication", app);
        model.addAttribute("offer", new Offer());
        return "recruiter_offer_create";
    }

    @PostMapping("/offer/create")
    public String createOffer(@RequestParam String applicationId,
                              @RequestParam String position,
                              @RequestParam String department,
                              @RequestParam Double salary,
                              @RequestParam String startDate,
                              @RequestParam(required = false) String notes) {
        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/recruiter/applicants";
        }

        // Only shortlisted candidates can receive offers
        if (!app.getStatus().equals("Shortlisted")) {
            return "redirect:/recruiter/applicant/" + applicationId;
        }

        Offer offer = new Offer();
        offer.setApplicationId(applicationId);
        offer.setCandidateName(app.getFullName());
        offer.setCandidateEmail(app.getEmail());
        offer.setPosition(position);
        offer.setDepartment(department);
        offer.setSalary(salary);
        offer.setNotes(notes);
        offer.setStatus("Created");
        offer.setOfferType("DRAFT");  // Mark as DRAFT offer from recruiter
        offer.setCreatedBy("RECRUITER");
        offer.setCreatedDate(new java.util.Date());

        offerService.save(offer);
        app.setOfferId(offer.getId());
        app.setOfferStatus("Draft Offer");
        applicationService.updateApplication(app);

        return "redirect:/recruiter/offers";
    }

    @PostMapping("/offer/{offerId}/send")
    public String sendOffer(@PathVariable String offerId) {
        Offer offer = offerService.findById(offerId).orElse(null);
        if (offer != null) {
            offer.setStatus("Sent");
            offer.setSentDate(new java.util.Date());
            offerService.save(offer);

            // Update application status
            Application app = applicationService.findById(offer.getApplicationId());
            if (app != null) {
                app.setOfferStatus("Sent");
                applicationService.updateApplication(app);
            }
        }
        return "redirect:/recruiter/offers";
    }

    @PostMapping("/offer/{offerId}/delete")
    public String deleteOffer(@PathVariable String offerId) {
        Offer offer = offerService.findById(offerId).orElse(null);
        if (offer != null) {
            // Revert application status
            Application app = applicationService.findById(offer.getApplicationId());
            if (app != null) {
                app.setOfferId(null);
                app.setOfferStatus(null);
                app.setStatus("Shortlisted");
                applicationService.updateApplication(app);
            }
            offerService.delete(offerId);
        }
        return "redirect:/recruiter/offers";
    }

}
