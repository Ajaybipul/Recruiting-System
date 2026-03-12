package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.ApplicantProfile;
import com.example.recruiting_system.model.Candidate;
import com.example.recruiting_system.model.Employee;
import com.example.recruiting_system.model.Interview;
import com.example.recruiting_system.model.JobPosition;
import com.example.recruiting_system.model.Offer;
import com.example.recruiting_system.repository.ApplicantProfileRepository;
import com.example.recruiting_system.repository.ApplicationRepository;
import com.example.recruiting_system.repository.JobPositionRepository;
import com.example.recruiting_system.repository.UserRepository;
import com.example.recruiting_system.security.User;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.CandidateService;
import com.example.recruiting_system.service.EmployeeService;
import com.example.recruiting_system.service.InterviewService;
import com.example.recruiting_system.service.JobPositionService;
import com.example.recruiting_system.service.OfferService;
import com.example.recruiting_system.service.OnboardingService;
import com.example.recruiting_system.model.OnboardingChecklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hr")
public class HRController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicantProfileRepository applicantProfileRepository;

    @Autowired
    private OnboardingService onboardingService;

    private boolean isHRManager(HttpSession session) {
        Object rolesObj = session.getAttribute("roles");
        if (!(rolesObj instanceof List)) return false;
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) rolesObj;
        return roles.contains("ROLE_HR");
    }

    // ============================================
    // DASHBOARD
    // ============================================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        // Job overview
        List<JobPosition> allJobs = jobPositionRepository.findAll();
        long totalJobs = allJobs.size();
        long openJobs = allJobs.stream().filter(j -> "Open".equalsIgnoreCase(j.getStatus())).count();
        long closedJobs = allJobs.stream().filter(j -> "Closed".equalsIgnoreCase(j.getStatus())).count();
        long pendingAppJobs = allJobs.stream()
                .filter(j -> applicationRepository.countByJobIdAndStatus(j.getId(), "Submitted") > 0)
                .count();

        // Applicant overview
        List<Application> allApps = applicationRepository.findAll();
        long totalApplicants = allApps.size();
        long shortlisted = applicationRepository.countByStatus("Shortlisted");
        long interviewScheduled = applicationRepository.countByStatus("Interview Scheduled");
        long selected = applicationRepository.countByStatus("Selected");
        long rejected = applicationRepository.countByStatus("Rejected");
        long onHold = applicationRepository.countByStatus("On Hold");

        // Recruiter performance (get all recruiters and their stats)
        List<User> recruiters = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains("ROLE_RECRUITER"))
                .collect(Collectors.toList());

        List<Map<String, Object>> recruiterStats = new ArrayList<>();
        for (User recruiter : recruiters) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("username", recruiter.getUsername());
            long reviewed = applicationRepository.findAll().stream()
                    .filter(a -> a.getScreeningNotes() != null && !a.getScreeningNotes().isEmpty())
                    .count();
            stat.put("reviewed", reviewed);
            recruiterStats.add(stat);
        }

        // Onboarding overview
        List<OnboardingChecklist> allChecklists = onboardingService.findAllChecklists();
        long totalOnboarding = allChecklists.size();
        long onboardingInProgress = allChecklists.stream()
                .filter(c -> "In Progress".equals(c.getOverallStatus()))
                .count();
        long onboardingCompleted = allChecklists.stream()
                .filter(c -> "Completed".equals(c.getOverallStatus()))
                .count();

        // Legacy attributes for old template compatibility
        List<Candidate> candidates = candidateService.findAll();
        List<Interview> interviews = interviewService.findAll();
        List<Offer> offers = offerService.findAll();

        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("openJobs", openJobs);
        model.addAttribute("closedJobs", closedJobs);
        model.addAttribute("pendingAppJobs", pendingAppJobs);
        model.addAttribute("totalApplicants", totalApplicants);
        model.addAttribute("shortlisted", shortlisted);
        model.addAttribute("interviewScheduled", interviewScheduled);
        model.addAttribute("selected", selected);
        model.addAttribute("rejected", rejected);
        model.addAttribute("onHold", onHold);
        model.addAttribute("recruiterStats", recruiterStats);
        
        // Onboarding attributes
        model.addAttribute("totalOnboarding", totalOnboarding);
        model.addAttribute("onboardingInProgress", onboardingInProgress);
        model.addAttribute("onboardingCompleted", onboardingCompleted);
        
        model.addAttribute("candidateCount", candidates.size());
        model.addAttribute("interviewCount", interviews.size());
        model.addAttribute("offerCount", offers.size());
        model.addAttribute("candidates", candidates);
        model.addAttribute("interviews", interviews);
        model.addAttribute("offers", offers);

        return "hr_dashboard";
    }

    // ============================================
    // JOB MANAGEMENT
    // ============================================
    @GetMapping("/jobs")
    public String listJobs(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        List<JobPosition> jobs = jobPositionRepository.findAll();
        model.addAttribute("jobs", jobs);
        return "hr_jobs_list";
    }

    @GetMapping("/jobs/create")
    public String createJobForm(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        List<User> recruiters = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains("ROLE_RECRUITER"))
                .collect(Collectors.toList());

        List<User> hmList = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains("ROLE_HIRING_MANAGER"))
                .collect(Collectors.toList());

        model.addAttribute("recruiters", recruiters);
        model.addAttribute("hiringManagers", hmList);

        return "hr_job_create";
    }

    @PostMapping("/jobs/create")
    public String createJob(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String skills,
            @RequestParam String department,
            @RequestParam String location,
            @RequestParam String salaryRange,
            @RequestParam(required = false, defaultValue = "1") Integer openings,
            @RequestParam(required = false) String assignedRecruiter,
            @RequestParam(required = false) String assignedHiringManager,
            HttpSession session
    ) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        JobPosition job = new JobPosition();
        job.setTitle(title);
        job.setDescription(description);
        job.setDepartment(department);
        job.setLocation(location);
        job.setSalaryRange(salaryRange);
        job.setOpenings(openings);
        job.setStatus("Open");
        job.setPublished(true);
        job.setCreatedDate(new Date());
        job.setAssignedRecruiter(assignedRecruiter);
        job.setAssignedHiringManager(assignedHiringManager);
        
        // Parse skills from comma-separated string
        List<String> skillsList = Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        job.setSkills(skillsList);

        jobPositionRepository.save(job);

        return "redirect:/hr/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable String id, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        JobPosition job = jobPositionRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/hr/jobs";
        }

        List<User> recruiters = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains("ROLE_RECRUITER"))
                .collect(Collectors.toList());

        List<User> hmList = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains("ROLE_HIRING_MANAGER"))
                .collect(Collectors.toList());

        model.addAttribute("job", job);
        model.addAttribute("recruiters", recruiters);
        model.addAttribute("hiringManagers", hmList);

        return "hr_job_edit";
    }

    @PostMapping("/jobs/{id}/update")
    public String updateJob(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String skills,
            @RequestParam String department,
            @RequestParam String location,
            @RequestParam String salaryRange,
            @RequestParam String status,
            @RequestParam(required = false, defaultValue = "1") Integer openings,
            @RequestParam(required = false) String assignedRecruiter,
            @RequestParam(required = false) String assignedHiringManager,
            HttpSession session
    ) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        JobPosition job = jobPositionRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/hr/jobs";
        }

        job.setTitle(title);
        job.setDescription(description);
        job.setDepartment(department);
        job.setLocation(location);
        job.setSalaryRange(salaryRange);
        job.setStatus(status);
        job.setOpenings(openings);
        job.setUpdatedDate(new Date());
        job.setAssignedRecruiter(assignedRecruiter);
        job.setAssignedHiringManager(assignedHiringManager);
        
        // Parse skills from comma-separated string
        if (skills != null && !skills.isEmpty()) {
            List<String> skillsList = Arrays.stream(skills.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            job.setSkills(skillsList);
        }

        jobPositionRepository.save(job);

        return "redirect:/hr/jobs";
    }

    @PostMapping("/jobs/{id}/close")
    public String closeJob(@PathVariable String id, HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        JobPosition job = jobPositionRepository.findById(id).orElse(null);
        if (job != null) {
            job.setStatus("Closed");
            jobPositionRepository.save(job);
        }

        return "redirect:/hr/jobs";
    }

    // ============================================
    // APPLICANTS
    // ============================================
    @GetMapping("/applicants")
    public String listApplicants(
            @RequestParam(required = false) String jobId,
            @RequestParam(required = false) String recruiter,
            @RequestParam(required = false) String hiringManager,
            @RequestParam(required = false) String status,
            HttpSession session,
            Model model
    ) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        List<Application> applicants = applicationRepository.findAll();

        // Filter by job if specified
        if (jobId != null && !jobId.isEmpty()) {
            applicants = applicants.stream()
                    .filter(a -> a.getJobId().equals(jobId))
                    .collect(Collectors.toList());
        }

        // Filter by status if specified
        if (status != null && !status.isEmpty()) {
            applicants = applicants.stream()
                    .filter(a -> a.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        // Get all jobs for filter dropdown
        List<JobPosition> allJobs = jobPositionRepository.findAll();

        model.addAttribute("applicants", applicants);
        model.addAttribute("jobs", allJobs);
        model.addAttribute("filterJobId", jobId);
        model.addAttribute("filterStatus", status);

        return "hr_applicants_list";
    }

    @GetMapping("/applicants/{id}")
    public String viewApplicant(@PathVariable String id, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        System.out.println("\n========== HR CONTROLLER: viewApplicant() ==========");
        System.out.println("Received ID parameter: " + id);

        // 1. Load the FRESH application object from DB
        Application app = applicationRepository.findById(id).orElse(null);
        if (app == null) {
            System.out.println("ERROR: Application not found!");
            return "redirect:/hr/applicants";
        }

        System.out.println("✓ Found Application: " + app.getFullName());
        System.out.println("\n=== PROFILE DATA (FROM GETTERS) ===");
        System.out.println("  fullName: " + app.getFullName());
        System.out.println("  email: " + app.getEmail());
        System.out.println("  phone: " + app.getPhone());
        System.out.println("  skills: " + app.getSkills());
        System.out.println("  experience: " + app.getExperience());
        System.out.println("  appliedDate: " + app.getAppliedDate());
        System.out.println("  status: " + app.getStatus());
        System.out.println("  jobTitle: " + app.getJobTitle());
        System.out.println("\n=== INTERVIEW DATA ===");
        System.out.println("  interviewDate value: " + app.getInterviewDate());
        System.out.println("  interviewTime value: " + app.getInterviewTime());
        System.out.println("  interviewMode: " + app.getInterviewMode());
        System.out.println("  interviewLocation: " + app.getInterviewLocation());
        System.out.println("  interviewMeetingLink: " + app.getInterviewMeetingLink());
        System.out.println("  interviewNotes: " + app.getInterviewNotes());
        System.out.println("\n=== HM FEEDBACK DATA ===");
        System.out.println("  hmFeedback: " + (app.getHmFeedback() != null ? app.getHmFeedback().substring(0, Math.min(40, app.getHmFeedback().length())) + "..." : "null"));
        System.out.println("  hmRating: " + app.getHmRating());
        System.out.println("  hmDecision: " + app.getHmDecision());
        System.out.println("  technicalSkillsAssessment: " + app.getTechnicalSkillsAssessment());
        System.out.println("  communicationAssessment: " + app.getCommunicationAssessment());
        System.out.println("  problemSolvingAssessment: " + app.getProblemSolvingAssessment());
        System.out.println("  culturalFitAssessment: " + app.getCulturalFitAssessment());
        System.out.println("  hmNotes: " + app.getHmNotes());

        // 2. Fetch the actual user (applicant) details
        ApplicantProfile applicant = null;
        System.out.println("  Trying to load applicant with userId: " + app.getUserId());
        if (app.getUserId() != null && !app.getUserId().isEmpty()) {
            applicant = applicantProfileRepository.findByUserId(app.getUserId()).orElse(null);
            if (applicant != null) {
                System.out.println("  ✓ Found ApplicantProfile: " + applicant.getEmail());
                System.out.println("    email: " + applicant.getEmail());
                System.out.println("    phone: " + applicant.getPhone());
                System.out.println("    skills: " + applicant.getSkills());
            } else {
                System.out.println("  ✗ ApplicantProfile NOT FOUND for userId: " + app.getUserId());
            }
        } else {
            System.out.println("  ✗ userId is null or empty!");
        }

        // Load job position if jobId exists
        JobPosition job = null;
        if (app.getJobId() != null && !app.getJobId().isEmpty()) {
            job = jobPositionRepository.findById(app.getJobId()).orElse(null);
        }

        // 3. Load offer if offerId exists
        Offer offer = null;
        if (app.getOfferId() != null && !app.getOfferId().isEmpty()) {
            offer = offerService.findById(app.getOfferId()).orElse(null);
            if (offer != null) {
                app.setOffer(offer);
                System.out.println("✓ Loaded Offer: " + offer.getPositionTitle());
            }
        }

        // 4. Send objects to the model - THIS IS KEY!
        System.out.println("\n→ ADDING TO MODEL:");
        System.out.println("  BEFORE addAttribute - app.getInterviewDate() = " + app.getInterviewDate());
        System.out.println("  BEFORE addAttribute - app.getInterviewTime() = " + app.getInterviewTime());
        System.out.println("  BEFORE addAttribute - app.getHmFeedback() = " + app.getHmFeedback());
        
        model.addAttribute("application", app);
        System.out.println("  ✓ Added 'application' to model");
        
        // EXPLICIT FIELD ATTRIBUTES: Add each field individually for Thymeleaf
        model.addAttribute("appId", app.getId());
        model.addAttribute("appFullName", app.getFullName());
        model.addAttribute("appEmail", app.getEmail());
        model.addAttribute("appPhone", app.getPhone());
        model.addAttribute("appSkills", app.getSkills());
        model.addAttribute("appExperience", app.getExperience());
        model.addAttribute("appStatus", app.getStatus());
        model.addAttribute("appJobTitle", app.getJobTitle());
        model.addAttribute("appAppliedDate", app.getAppliedDate());
        model.addAttribute("appResumePath", app.getResumePath());
        model.addAttribute("appInterviewDate", app.getInterviewDate());
        model.addAttribute("appInterviewTime", app.getInterviewTime());
        model.addAttribute("appInterviewMode", app.getInterviewMode());
        model.addAttribute("appInterviewLocation", app.getInterviewLocation());
        model.addAttribute("appInterviewMeetingLink", app.getInterviewMeetingLink());
        model.addAttribute("appInterviewNotes", app.getInterviewNotes());
        model.addAttribute("appHmFeedback", app.getHmFeedback());
        model.addAttribute("appHmRating", app.getHmRating());
        model.addAttribute("appHmDecision", app.getHmDecision());
        model.addAttribute("appTechnicalSkillsAssessment", app.getTechnicalSkillsAssessment());
        model.addAttribute("appCommunicationAssessment", app.getCommunicationAssessment());
        model.addAttribute("appProblemSolvingAssessment", app.getProblemSolvingAssessment());
        model.addAttribute("appCulturalFitAssessment", app.getCulturalFitAssessment());
        model.addAttribute("appHmNotes", app.getHmNotes());
        model.addAttribute("appOfferId", app.getOfferId());
        model.addAttribute("appOfferStatus", app.getOfferStatus());
        
        // Add offer object for template
        if (offer != null) {
            model.addAttribute("offer", offer);
            System.out.println("  ✓ Added 'offer' to model: " + offer.getPositionTitle());
        }
        
        // IMPORTANT: Also pass formatted strings for LocalDate/LocalTime since Thymeleaf may have issues
        if (app.getInterviewDate() != null) {
            model.addAttribute("interviewDateFormatted", app.getInterviewDate().toString());
        }
        if (app.getInterviewTime() != null) {
            model.addAttribute("interviewTimeFormatted", app.getInterviewTime().toString());
        }
        
        model.addAttribute("applicant", applicant);
        System.out.println("  ✓ Added 'applicant' to model");

        // Add job details to model
        if (job != null) {
            model.addAttribute("job", job);
            model.addAttribute("jobTitleFromJob", job.getTitle());
            model.addAttribute("jobLocation", job.getLocation());
            model.addAttribute("jobDepartment", job.getDepartment());
            model.addAttribute("jobSalaryRange", job.getSalaryRange());
            System.out.println("  ✓ Added job details to model");
        }

        System.out.println("\n========== ABOUT TO RENDER TEMPLATE ==========\n");

        return "hr_applicant_detail";
    }

    // ============================================
    // OFFER MANAGEMENT
    // ============================================
    
    // Show offer creation form
    @GetMapping("/applicants/{id}/offer")
    public String showOfferForm(@PathVariable String id, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(id).orElse(null);
        if (app == null || !app.getStatus().equals("READY_FOR_OFFER")) {
            return "redirect:/hr/applicants";
        }
        model.addAttribute("application", app);
        // Add explicit attributes for Thymeleaf
        model.addAttribute("appId", app.getId());
        model.addAttribute("appFullName", app.getFullName());
        model.addAttribute("appEmail", app.getEmail());
        model.addAttribute("appJobTitle", app.getJobTitle());
        model.addAttribute("appDepartment", app.getJobTitle());
        return "hr_create_offer";
    }

    // Create and send offer
    @PostMapping("/applicants/{id}/offer")
    public String createAndSendOffer(
            @PathVariable String id,
            @RequestParam String positionTitle,
            @RequestParam String department,
            @RequestParam String salary,
            @RequestParam String reportTo,
            @RequestParam String employmentTerms,
            @RequestParam Integer paidTimeOff,
            @RequestParam(required = false) String signOnBonus,
            @RequestParam(required = false) String healthInsurance,
            @RequestParam(required = false) String fourOhOneK,
            @RequestParam String startDate,
            @RequestParam Integer offerValidity,
            @RequestParam(required = false) String additionalNotes,
            HttpSession session) {
        
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        Application app = applicationRepository.findById(id).orElse(null);
        if (app == null) {
            return "redirect:/hr/applicants";
        }

        try {
            // Create offer
            Offer offer = new Offer();
            offer.setApplicationId(id);
            offer.setCandidateId(app.getId());
            offer.setCandidateName(app.getFullName());
            offer.setCandidateEmail(app.getEmail());
            offer.setPositionTitle(positionTitle);
            offer.setDepartment(department);
            offer.setSalary(Double.parseDouble(salary.replaceAll("[^0-9.]", "")));
            offer.setReportTo(reportTo);
            offer.setEmploymentTerms(employmentTerms);
            offer.setPaidTimeOff(paidTimeOff);
            if (signOnBonus != null && !signOnBonus.isEmpty()) {
                offer.setSignOnBonus(Double.parseDouble(signOnBonus.replaceAll("[^0-9.]", "")));
            }
            offer.setHealthInsurance(healthInsurance != null && !healthInsurance.isEmpty());
            offer.setFourOhOneK(fourOhOneK != null && !fourOhOneK.isEmpty());
            offer.setAdditionalNotes(additionalNotes);
            offer.setStatus("SENT");
            offer.setOfferType("FINAL");
            offer.setCreatedBy("HR");
            offer.setCreatedDate(new Date());
            offer.setSentDate(new Date());
            
            // Parse start date and offer validity
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedStartDate = dateFormat.parse(startDate);
            offer.setStartDate(parsedStartDate);
            
            // Calculate expiration date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, offerValidity);
            offer.setExpirationDate(calendar.getTime());

            Offer savedOffer = offerService.save(offer);
            
            // Update application status and store offer reference
            app.setStatus("OFFER_SENT");
            app.setOfferId(savedOffer.getId());
            applicationService.updateApplication(app);

            // Log for debugging
            System.out.println("✓ Offer created and sent to: " + app.getEmail());
            System.out.println("  Position: " + positionTitle);
            System.out.println("  Salary: " + salary);
            System.out.println("  Start Date: " + startDate);
            System.out.println("  Offer Status: SENT");

            // Redirect to applicant detail with success message
            return "redirect:/hr/applicants/" + id + "?success=Offer created and sent to candidate";

        } catch (Exception e) {
            System.err.println("❌ Error creating offer: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/hr/applicants/" + id + "?error=Failed to create offer";
        }
    }
    
    @GetMapping("/offers")
    public String offersPage(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        List<Offer> allOffers = offerService.findAll();
        
        // Separate DRAFT and FINAL offers
        List<Offer> draftOffers = allOffers.stream()
                .filter(o -> "DRAFT".equals(o.getOfferType()))
                .collect(Collectors.toList());
        
        List<Offer> finalOffers = allOffers.stream()
                .filter(o -> "FINAL".equals(o.getOfferType()))
                .collect(Collectors.toList());
        
        model.addAttribute("draftOffers", draftOffers);
        model.addAttribute("finalOffers", finalOffers);
        return "hr_offers_list";
    }

    @GetMapping("/offer/create/{applicationId}")
    public String createOfferForm(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null || !app.getStatus().equals("Selected")) {
            return "redirect:/hr/applicants";
        }
        model.addAttribute("application", app);
        model.addAttribute("offer", new Offer());
        return "hr_offer_create";
    }

    @PostMapping("/offer/create")
    public String createOffer(@RequestParam String applicationId,
                              @RequestParam String position,
                              @RequestParam String department,
                              @RequestParam Double salary,
                              @RequestParam String startDate,
                              @RequestParam(required = false) String notes,
                              HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return "redirect:/hr/applicants";
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
        offer.setOfferType("FINAL");  // Mark as FINAL offer from HR
        offer.setCreatedBy("HR");
        offer.setCreatedDate(new Date());

        offerService.save(offer);
        app.setOfferId(offer.getId());
        app.setOfferStatus("Final Offer Created");
        applicationService.updateApplication(app);

        return "redirect:/hr/offers";
    }

    @PostMapping("/offer/{offerId}/approve")
    public String approveDraftOffer(@PathVariable String offerId,
                                   @RequestParam(required = false) String notes,
                                   HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Offer offer = offerService.findById(offerId).orElse(null);
        if (offer != null && "DRAFT".equals(offer.getOfferType())) {
            offer.setStatus("Approved");
            offer.setApprovedBy("HR");
            offer.setApprovedDate(new Date());
            offer.setApprovalNotes(notes);
            offerService.save(offer);

            Application app = applicationRepository.findById(offer.getApplicationId()).orElse(null);
            if (app != null) {
                app.setOfferStatus("Draft Approved");
                applicationService.updateApplication(app);
            }
        }
        return "redirect:/hr/offers";
    }

    @PostMapping("/offer/{offerId}/reject")
    public String rejectDraftOffer(@PathVariable String offerId,
                                  @RequestParam(required = false) String notes,
                                  HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Offer offer = offerService.findById(offerId).orElse(null);
        if (offer != null && "DRAFT".equals(offer.getOfferType())) {
            offer.setStatus("Rejected");
            offer.setApprovalNotes(notes);
            offerService.save(offer);

            Application app = applicationRepository.findById(offer.getApplicationId()).orElse(null);
            if (app != null) {
                app.setOfferStatus("Draft Rejected");
                applicationService.updateApplication(app);
            }
        }
        return "redirect:/hr/offers";
    }

    @PostMapping("/offer/{offerId}/send")
    public String sendFinalOffer(@PathVariable String offerId, HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Offer offer = offerService.findById(offerId).orElse(null);
        if (offer != null && "FINAL".equals(offer.getOfferType())) {
            offer.setStatus("Sent");
            offer.setSentDate(new Date());
            offerService.save(offer);

            // Update application status
            Application app = applicationRepository.findById(offer.getApplicationId()).orElse(null);
            if (app != null) {
                app.setOfferStatus("Offer Sent");
                applicationService.updateApplication(app);
            }
        }
        return "redirect:/hr/offers";
    }

    // ============================================
    // REPORTS & ANALYTICS
    // ============================================
    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        List<Application> allApps = applicationRepository.findAll();
        List<JobPosition> allJobs = jobPositionRepository.findAll();

        // Hiring Pipeline
        Map<String, Long> pipeline = new HashMap<>();
        pipeline.put("Submitted", applicationRepository.countByStatus("Submitted"));
        pipeline.put("Under Review", applicationRepository.countByStatus("Under Review"));
        pipeline.put("Shortlisted", applicationRepository.countByStatus("Shortlisted"));
        pipeline.put("Interview Scheduled", applicationRepository.countByStatus("Interview Scheduled"));
        pipeline.put("Selected", applicationRepository.countByStatus("Selected"));
        pipeline.put("Rejected", applicationRepository.countByStatus("Rejected"));

        // Time to hire calculation
        List<Application> selected = allApps.stream()
                .filter(a -> "Selected".equals(a.getStatus()) && a.getAppliedDate() != null)
                .collect(Collectors.toList());
        
        double avgTimeToHire = selected.isEmpty() ? 0 : selected.stream()
                .mapToLong(a -> (new Date().getTime() - a.getAppliedDate().getTime()) / (1000 * 60 * 60 * 24))
                .average()
                .orElse(0);

        // Department-wise hiring
        Map<String, Long> deptHiring = allJobs.stream()
                .collect(Collectors.groupingBy(
                        job -> job.getDepartment() != null ? job.getDepartment() : "Unknown",
                        Collectors.counting()
                ));

        model.addAttribute("pipeline", pipeline);
        model.addAttribute("avgTimeToHire", Math.round(avgTimeToHire));
        model.addAttribute("deptHiring", deptHiring);
        model.addAttribute("totalApplications", allApps.size());
        model.addAttribute("selectedCount", selected.size());

        return "hr_reports";
    }

    // ============================================
    // BACKGROUND CHECK TRACKING
    // ============================================
    @GetMapping("/background-checks")
    public String backgroundChecksPage(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        List<Application> selected = applicationRepository.findByStatus("Selected");
        model.addAttribute("candidates", selected);
        return "hr_background_checks";
    }

    @GetMapping("/background-check/{applicationId}")
    public String viewBackgroundCheck(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return "redirect:/hr/background-checks";
        }
        model.addAttribute("application", app);
        return "hr_background_check_detail";
    }

    @PostMapping("/background-check/{applicationId}/initiate")
    public String initiateBackgroundCheck(@PathVariable String applicationId, HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app != null) {
            app.setBackgroundCheckStatus("In Progress");
            app.setBackgroundCheckInitiatedDate(new Date());
            applicationService.updateApplication(app);
        }
        return "redirect:/hr/background-check/" + applicationId;
    }

    @PostMapping("/background-check/{applicationId}/update")
    public String updateBackgroundCheck(@PathVariable String applicationId,
                                       @RequestParam String bgCheckStatus,
                                       @RequestParam String refCheckStatus,
                                       @RequestParam String eduVerification,
                                       @RequestParam String empVerification,
                                       @RequestParam String criminalCheck,
                                       @RequestParam(required = false) String notes,
                                       @RequestParam(required = false, defaultValue = "false") boolean passed,
                                       HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app != null) {
            app.setBackgroundCheckStatus(bgCheckStatus);
            app.setReferenceCheckStatus(refCheckStatus);
            app.setEducationVerificationStatus(eduVerification);
            app.setEmploymentVerificationStatus(empVerification);
            app.setCriminalCheckStatus(criminalCheck);
            app.setBackgroundCheckNotes(notes);
            app.setBackgroundCheckPassed(passed);
            
            if ("Completed".equals(bgCheckStatus)) {
                app.setBackgroundCheckCompletedDate(new Date());
            }
            
            applicationService.updateApplication(app);
        }
        return "redirect:/hr/background-check/" + applicationId;
    }

    // ==================== HR DECISION & FINAL HIRING WORKFLOW ====================

    /**
     * View candidates approved by Hiring Manager (HM Decision: Approved)
     */
    @GetMapping("/candidates-for-offer")
    public String candidatesForOffer(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        // Get applications where HM decision is "Approved"
        List<Application> candidates = applicationRepository.findAll().stream()
                .filter(app -> "Approved".equals(app.getHmDecision()))
                .collect(Collectors.toList());
        
        model.addAttribute("candidates", candidates);
        model.addAttribute("pageTitle", "Candidates for Offer - HM Approved");
        return "hr_candidates_for_offer";
    }

    /**
     * View candidate profile for HR review
     */
    @GetMapping("/candidate/{applicationId}/review")
    public String reviewCandidate(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return "redirect:/hr/candidates-for-offer";
        }

        // Get interview details if available
        Interview interview = null;
        if (app.getInterviewId() != null) {
            interview = interviewService.findById(app.getInterviewId()).orElse(null);
        }

        model.addAttribute("candidateApplication", app);
        model.addAttribute("interview", interview);
        return "hr_candidate_review";
    }

    /**
     * Make final hiring decision (Hired, Offer Declined, Withdrawn)
     */
    @PostMapping("/candidate/{applicationId}/hiring-decision")
    public String makeHiringDecision(
            @PathVariable String applicationId,
            @RequestParam String decision, // Hired, Offer Declined, Withdrawn
            @RequestParam(required = false) String notes,
            HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }
        
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return "redirect:/hr/candidates-for-offer";
        }

        app.setHrFinalDecision(decision);
        app.setHrDecisionNotes(notes);
        app.setHrDecisionDate(new Date());
        
        if ("Hired".equals(decision)) {
            app.setStatus("Hired");
        } else if ("Offer Declined".equals(decision)) {
            app.setStatus("Rejected");
        } else if ("Withdrawn".equals(decision)) {
            app.setStatus("Withdrawn");
        }

        applicationService.updateApplication(app);
        return "redirect:/hr/candidate/" + applicationId + "/review";
    }

    /**
     * Record offer acceptance from candidate
     */
    @PostMapping("/offer/{offerId}/accept")
    public String acceptOffer(
            @PathVariable String offerId,
            @RequestParam String applicationId,
            HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        Offer offer = offerService.findById(offerId).orElse(null);
        if (offer != null) {
            offer.setStatus("Accepted");
            offerService.save(offer);
        }

        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app != null) {
            app.setOfferStatus("Accepted");
            app.setOfferAcceptedDate(new Date());
            app.setStatus("Offer Accepted");
            applicationService.updateApplication(app);
        }

        return "redirect:/hr/candidate/" + applicationId + "/create-employee";
    }

    /**
     * Create employee record after offer acceptance
     */
    @GetMapping("/candidate/{applicationId}/create-employee")
    public String createEmployeeForm(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return "redirect:/hr/candidates-for-offer";
        }

        model.addAttribute("candidateApplication", app);
        return "hr_create_employee";
    }

    /**
     * Save employee record
     */
    @PostMapping("/candidate/{applicationId}/create-employee")
    public String saveEmployee(
            @PathVariable String applicationId,
            @RequestParam String employmentType,
            @RequestParam(required = false) String reportsTo,
            @RequestParam(required = false) String officeLocation,
            @RequestParam String joiningDateStr,
            HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return "redirect:/hr/candidates-for-offer";
        }

        // Get offer details
        Offer offer = null;
        if (app.getOfferId() != null) {
            offer = offerService.findById(app.getOfferId()).orElse(null);
        }

        // Create employee record
        Employee employee = employeeService.createEmployee(
                applicationId,
                app.getFullName().split(" ")[0],
                app.getFullName().contains(" ") ? app.getFullName().substring(app.getFullName().indexOf(" ") + 1) : "",
                app.getEmail(),
                app.getPhone(),
                app.getJobTitle(),
                offer != null ? offer.getDepartment() : "N/A",
                offer != null ? offer.getSalary() : 0.0
        );

        employee.setEmploymentType(employmentType);
        employee.setReportsTo(reportsTo);
        employee.setOfficeLocation(officeLocation);
        
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            employee.setJoiningDate(sdf.parse(joiningDateStr));
        } catch (Exception e) {
            employee.setJoiningDate(new Date());
        }

        employee = employeeService.updateEmployee(employee);

        // Update application with employee ID
        app.setEmployeeId(employee.getEmployeeId());
        app.setJoiningDate(employee.getJoiningDate());
        app.setStatus("Onboarding");
        applicationService.updateApplication(app);

        return "redirect:/hr/employees/" + employee.getEmployeeId();
    }

    /**
     * View employee details
     */
    @GetMapping("/employees/{employeeId}")
    public String viewEmployee(@PathVariable String employeeId, HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "redirect:/hr/employees";
        }

        Application app = applicationRepository.findById(employee.getApplicationId()).orElse(null);
        model.addAttribute("employee", employee);
        model.addAttribute("candidateApplication", app);
        return "hr_employee_detail";
    }

    /**
     * View all employees
     */
    @GetMapping("/employees")
    public String listEmployees(HttpSession session, Model model) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        List<Employee> employees = employeeService.getAllEmployees();
        long activeCount = employeeService.countActiveEmployees();

        model.addAttribute("employees", employees);
        model.addAttribute("activeCount", activeCount);
        return "hr_employees_list";
    }

    /**
     * Update employee onboarding status
     */
    @PostMapping("/employees/{employeeId}/onboarding-status")
    public String updateOnboardingStatus(
            @PathVariable String employeeId,
            @RequestParam String status, // Not Started, In Progress, Completed
            HttpSession session) {
        if (!isHRManager(session)) {
            return "redirect:/login";
        }

        Employee employee = employeeService.updateOnboardingStatus(employeeId, status);
        if (employee != null && "Completed".equals(status)) {
            // Update application status
            Application app = applicationRepository.findById(employee.getApplicationId()).orElse(null);
            if (app != null) {
                app.setStatus("Completed");
                app.setOnboardingStatus("Completed");
                applicationService.updateApplication(app);
            }
        }

        return "redirect:/hr/employees/" + employeeId;
    }
}
