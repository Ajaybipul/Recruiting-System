package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.Interview;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.InterviewService;
import com.example.recruiting_system.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hiring-manager")
public class HiringManagerController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private JobPositionService jobPositionService;

    private boolean hasHiringManagerRole(HttpSession session) {
        Object rolesObj = session.getAttribute("roles");
        if (!(rolesObj instanceof java.util.List)) return false;
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) rolesObj;
        return roles.contains("ROLE_HIRING_MANAGER");
    }

    // ============================================
    // DASHBOARD
    // ============================================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        // Count statistics
        long totalForwarded = applicationService.countByStatus("Sent to Hiring Manager");
        long waitingForReview = applicationService.countByStatus("Sent to Hiring Manager");
        long interviewScheduled = applicationService.countByStatus("Interview Scheduled");
        long recentlyReviewed = applicationService.countByStatus("Hiring Manager Reviewed");

        // Get recent candidates for quick view
        List<Application> recentApps = applicationService.findByStatus("Sent to Hiring Manager").stream()
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalForwarded", totalForwarded);
        model.addAttribute("waitingForReview", waitingForReview);
        model.addAttribute("upcomingInterviews", interviewScheduled);
        model.addAttribute("recentlyReviewed", recentlyReviewed);
        model.addAttribute("recentCandidates", recentApps);

        return "hiring_manager_dashboard";
    }

    // ============================================
    // VIEW FORWARDED CANDIDATES
    // ============================================
    @GetMapping("/candidates")
    public String viewForwardedCandidates(HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        // Get all candidates forwarded to HM (status = "Sent to Hiring Manager")
        List<Application> candidates = applicationService.findByStatus("Sent to Hiring Manager");

        // Build row data with interview info
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Application app : candidates) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", app.getId());
            row.put("candidateName", app.getFullName() != null ? app.getFullName() : "");
            row.put("jobTitle", app.getJobTitle() != null ? app.getJobTitle() : "");
            row.put("status", app.getStatus() != null ? app.getStatus() : "Sent to Hiring Manager");

            // Interview date/time if available
            if (app.getInterviewDate() != null && app.getInterviewTime() != null) {
                DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                row.put("interviewDate", dateFmt.format(app.getInterviewDate()));
                row.put("interviewTime", timeFmt.format(app.getInterviewTime()));
            } else {
                row.put("interviewDate", "");
                row.put("interviewTime", "");
            }

            row.put("email", app.getEmail() != null ? app.getEmail() : "");
            rows.add(row);
        }

        model.addAttribute("candidates", rows);
        model.addAttribute("count", rows.size());

        return "hiring_manager_candidates";
    }

    // ============================================
    // CANDIDATE DETAIL PAGE
    // ============================================
    @GetMapping("/candidate/{applicationId}")
    public String viewCandidateDetail(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        // Build candidate detail view
        Map<String, Object> candidateDetail = new HashMap<>();
        candidateDetail.put("id", app.getId());
        candidateDetail.put("fullName", app.getFullName());
        candidateDetail.put("email", app.getEmail());
        candidateDetail.put("jobTitle", app.getJobTitle());
        candidateDetail.put("jobId", app.getJobId());
        candidateDetail.put("status", app.getStatus());
        candidateDetail.put("resumePath", app.getResumePath());
        candidateDetail.put("screeningNotes", app.getScreeningNotes());
        candidateDetail.put("appliedDate", app.getAppliedDate());

        System.out.println("\n==== HM Candidate Detail ====");
        System.out.println("Application ID: " + app.getId());
        System.out.println("Full Name: " + app.getFullName());
        System.out.println("Screening Notes: " + app.getScreeningNotes());

        // Interview details - always add fields (even if null) so Thymeleaf can safely check
        candidateDetail.put("interviewDate", "");
        candidateDetail.put("interviewTime", "");
        candidateDetail.put("interviewMode", app.getInterviewMode());
        candidateDetail.put("interviewLocation", app.getInterviewLocation());
        candidateDetail.put("interviewMeetingLink", app.getInterviewMeetingLink());
        candidateDetail.put("interviewNotes", app.getInterviewNotes());
        
        if (app.getInterviewDate() != null && app.getInterviewTime() != null) {
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
            candidateDetail.put("interviewDate", dateFmt.format(app.getInterviewDate()));
            candidateDetail.put("interviewTime", timeFmt.format(app.getInterviewTime()));
        }

        // Feedback info
        candidateDetail.put("technicalScore", app.getTechnicalScore() != null ? app.getTechnicalScore() : "");
        candidateDetail.put("communicationScore", app.getCommunicationScore() != null ? app.getCommunicationScore() : "");
        candidateDetail.put("cultureFitScore", app.getCultureFitScore() != null ? app.getCultureFitScore() : "");
        candidateDetail.put("interviewerNotes", app.getInterviewerNotes() != null ? app.getInterviewerNotes() : "");
        candidateDetail.put("recommendation", app.getRecommendation() != null ? app.getRecommendation() : "");

        // HM feedback (if any)
        candidateDetail.put("hmFeedback", app.getHmFeedback() != null ? app.getHmFeedback() : "");
        candidateDetail.put("hmRating", app.getHmRating() != null ? app.getHmRating() : 0);
        candidateDetail.put("hmDecision", app.getHmDecision() != null ? app.getHmDecision() : "");
        candidateDetail.put("hmNotes", app.getHmNotes() != null ? app.getHmNotes() : "");

        model.addAttribute("candidate", candidateDetail);

        return "hiring_manager_candidate_detail";
    }

    // ============================================
    // SCHEDULE INTERVIEW (FOR HIRING MANAGER)
    // ============================================
    @GetMapping("/candidate/{applicationId}/schedule-interview")
    public String scheduleInterviewForm(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        model.addAttribute("candidateApplication", app);
        return "interview_schedule";
    }

    @PostMapping("/candidate/{applicationId}/schedule-interview")
    public String scheduleInterview(
            @PathVariable String applicationId,
            @RequestParam(name = "date") String dateStr,
            @RequestParam(name = "time") String timeStr,
            @RequestParam(name = "interviewType") String interviewType,
            @RequestParam(name = "location") String location,
            @RequestParam(name = "meetingLink", required = false) String meetingLink,
            @RequestParam(name = "interviewer") String interviewer,
            @RequestParam(name = "notes", required = false) String interviewNotes,
            HttpSession session,
            Model model) {
        
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        System.out.println("\n==== HM /candidate/{applicationId}/schedule-interview ====");
        System.out.println("Received applicationId: '" + applicationId + "'");
        System.out.println("Received date: " + dateStr);
        System.out.println("Received time: " + timeStr);
        System.out.println("Received interviewType: " + interviewType);
        System.out.println("Received location: " + location);
        System.out.println("Received meetingLink: " + meetingLink);
        System.out.println("Received interviewer: " + interviewer);
        System.out.println("Received interviewNotes: " + interviewNotes);

        if (applicationId == null || applicationId.trim().isEmpty()) {
            System.out.println("ERROR: applicationId is empty/null!");
            return "redirect:/hiring-manager/candidates";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            System.out.println("ERROR: Application not found for id: " + applicationId);
            return "redirect:/hiring-manager/candidates";
        }
        System.out.println("SUCCESS: Found application - " + app.getFullName());

        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
            
            // Create interview
            com.example.recruiting_system.model.Interview interview = new com.example.recruiting_system.model.Interview();
            interview.setCandidateId(app.getId());
            interview.setJobPositionId(app.getJobId());
            interview.setMode(interviewType);
            interview.setLocation(location);
            interview.setMeetingLink(meetingLink);
            interview.setInterviewNotes(interviewNotes);
            
            // Convert LocalDate + LocalTime → Date
            java.util.Date scheduledAt = java.util.Date.from(
                    date.atTime(time)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant()
            );
            interview.setScheduledAt(scheduledAt);
            interview.setStatus("Scheduled");
            
            com.example.recruiting_system.model.Interview saved = interviewService.schedule(interview);
            
            // Update application status with all interview details
            app.setInterviewId(saved.getId());
            app.setInterviewDate(date);
            app.setInterviewTime(time);
            app.setInterviewMode(interviewType);
            app.setInterviewLocation(location);
            app.setInterviewMeetingLink(meetingLink);
            app.setInterviewNotes(interviewNotes);
            app.setStatus("Interview Scheduled");
            applicationService.updateApplication(app);
            
            System.out.println("Interview saved with ID: " + saved.getId());
        } catch (Exception e) {
            System.out.println("ERROR scheduling interview: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/hiring-manager/candidate/" + applicationId;
    }

    // ============================================
    // FEEDBACK FORM
    // ============================================
    @GetMapping("/candidate/{applicationId}/feedback")
    public String feedbackForm(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        model.addAttribute("candidate", app);
        model.addAttribute("ratings", Arrays.asList(1, 2, 3, 4, 5));

        return "hiring_manager_feedback";
    }

    @PostMapping("/candidate/{applicationId}/feedback")
    public String submitFeedback(
            @PathVariable String applicationId,
            @RequestParam String comments,
            @RequestParam Integer rating,
            @RequestParam(required = false) String technicalSkills,
            @RequestParam(required = false) String communication,
            @RequestParam(required = false) String problemSolving,
            @RequestParam(required = false) String culturalFit,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) String decision,
            HttpSession session,
            Model model
    ) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        // Store HM feedback
        app.setHmFeedback(comments);
        app.setHmRating(rating);
        
        // Store detailed assessment
        if (technicalSkills != null && !technicalSkills.isEmpty()) {
            app.setTechnicalSkillsAssessment(technicalSkills);
        }
        if (communication != null && !communication.isEmpty()) {
            app.setCommunicationAssessment(communication);
        }
        if (problemSolving != null && !problemSolving.isEmpty()) {
            app.setProblemSolvingAssessment(problemSolving);
        }
        if (culturalFit != null && !culturalFit.isEmpty()) {
            app.setCulturalFitAssessment(culturalFit);
        }
        
        if (notes != null && !notes.isEmpty()) {
            app.setHmNotes(notes);
        }
        app.setStatus("Hiring Manager Reviewed");
        if (decision != null && !decision.isEmpty()) {
            app.setHmDecision(decision);
        }

        applicationService.updateApplication(app);

        System.out.println("HM Feedback submitted for " + app.getFullName() + 
                           " - Rating: " + rating + ", Technical: " + technicalSkills + 
                           ", Communication: " + communication + ", Decision: " + decision);

        return "redirect:/hiring-manager/candidate/" + applicationId;
    }

    // ============================================
    // APPROVAL / REJECTION / ON HOLD
    // ============================================
    @PostMapping("/candidate/{applicationId}/approve")
    public String approveCandidateForHR(
            @PathVariable String applicationId,
            @RequestParam(required = false) String hmNotes,
            HttpSession session
    ) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        app.setHmDecision("Approved");
        app.setStatus("READY_FOR_OFFER");
        if (hmNotes != null && !hmNotes.isEmpty()) {
            app.setHmNotes(hmNotes);
        }
        applicationService.updateApplication(app);

        System.out.println("HM Approved: " + app.getFullName());

        return "redirect:/hiring-manager/candidate/" + applicationId;
    }

    @PostMapping("/candidate/{applicationId}/reject")
    public String rejectCandidate(
            @PathVariable String applicationId,
            @RequestParam(required = false) String hmNotes,
            HttpSession session
    ) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        app.setHmDecision("Rejected");
        app.setStatus("Rejected");
        if (hmNotes != null && !hmNotes.isEmpty()) {
            app.setHmNotes(hmNotes);
        }
        applicationService.updateApplication(app);

        System.out.println("HM Rejected: " + app.getFullName());

        return "redirect:/hiring-manager/candidates";
    }

    @PostMapping("/candidate/{applicationId}/hold")
    public String putCandidateOnHold(
            @PathVariable String applicationId,
            @RequestParam(required = false) String hmNotes,
            HttpSession session
    ) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        app.setHmDecision("On Hold");
        app.setStatus("On Hold");
        if (hmNotes != null && !hmNotes.isEmpty()) {
            app.setHmNotes(hmNotes);
        }
        applicationService.updateApplication(app);

        System.out.println("HM Put On Hold: " + app.getFullName());

        return "redirect:/hiring-manager/candidate/" + applicationId;
    }

    // ============================================
    // INTERVIEWS ASSIGNED TO HM
    // ============================================
    @GetMapping("/interviews")
    public String viewAssignedInterviews(HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        // For now, fetch all interviews and display
        // In a full implementation, you'd check if HM is assigned to interview
        List<Interview> interviews = interviewService.findAll();
        
        System.out.println("\n=== HIRING MANAGER INTERVIEWS ===");
        System.out.println("Total interviews found: " + interviews.size());

        List<Map<String, Object>> rows = new ArrayList<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Interview it : interviews) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", it.getId());
            
            System.out.println("\nInterview ID: " + it.getId());
            System.out.println("  Candidate ID: " + it.getCandidateId());
            System.out.println("  Scheduled At: " + it.getScheduledAt());
            System.out.println("  Status: " + it.getStatus());

            // Candidate info from Application
            if (it.getCandidateId() != null) {
                Application app = applicationService.findById(it.getCandidateId());
                if (app != null) {
                    row.put("candidateName", app.getFullName());
                    row.put("email", app.getEmail());
                    row.put("applicationId", app.getId());
                    System.out.println("  Candidate Name: " + app.getFullName());
                    System.out.println("  Candidate Email: " + app.getEmail());
                }
            }

            // Interview date/time
            if (it.getScheduledAt() != null) {
                var ldt = it.getScheduledAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                row.put("date", dateFmt.format(ldt.toLocalDate()));
                row.put("time", timeFmt.format(ldt.toLocalTime()));
                System.out.println("  Date: " + dateFmt.format(ldt.toLocalDate()));
                System.out.println("  Time: " + timeFmt.format(ldt.toLocalTime()));
            } else {
                row.put("date", "Not scheduled");
                row.put("time", "");
                System.out.println("  Date: Not scheduled");
            }

            row.put("status", it.getStatus() != null ? it.getStatus() : "Scheduled");

            rows.add(row);
        }

        model.addAttribute("interviews", rows);
        model.addAttribute("count", rows.size());
        
        System.out.println("\nTotal rows to display: " + rows.size());

        return "hiring_manager_interviews";
    }

    @PostMapping("/interview/{interviewId}/complete")
    public String markInterviewComplete(
            @PathVariable String interviewId,
            @RequestParam(required = false) String remarks,
            HttpSession session
    ) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Optional<Interview> optInterview = interviewService.findById(interviewId);
        if (optInterview.isEmpty()) {
            return "redirect:/hiring-manager/interviews";
        }

        Interview interview = optInterview.get();
        interview.setStatus("Completed by HM");
        if (remarks != null && !remarks.isEmpty()) {
            interview.setHmRemarks(remarks);
        }
        interviewService.save(interview);

        System.out.println("HM Marked Interview Complete: " + interviewId);

        return "redirect:/hiring-manager/interviews";
    }

    // ============================================
    // REQUEST NEXT ROUND / CREATE PENDING INTERVIEW
    // ============================================
    @PostMapping("/candidate/{applicationId}/request-next-round")
    public String requestNextRound(
            @PathVariable String applicationId,
            @RequestParam(required = false) String hmNotes,
            HttpSession session
    ) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        // Create a placeholder interview record for recruiter to schedule
        Interview interview = new Interview();
        interview.setCandidateId(applicationId);
        interview.setJobPositionId(app.getJobId());
        interview.setStatus("Pending Scheduling");
        interview.setCreatedDate(new Date());
        interview.setUpdatedDate(new Date());
        Interview saved = interviewService.save(interview);

        // Link the interview and mark application status
        app.setInterviewId(saved.getId());
        app.setHmDecision("Request Next Round");
        app.setStatus("Request Next Round");
        if (hmNotes != null && !hmNotes.isEmpty()) {
            app.setHmNotes(hmNotes);
        }
        applicationService.updateApplication(app);

        System.out.println("HM requested next round for: " + app.getFullName());

        return "redirect:/hiring-manager/candidate/" + applicationId;
    }

    // ============================================
    // VIEW INTERVIEW HISTORY FOR A CANDIDATE
    // ============================================
    @GetMapping("/candidate/{applicationId}/history")
    public String viewInterviewHistory(@PathVariable String applicationId, HttpSession session, Model model) {
        if (!hasHiringManagerRole(session)) {
            return "redirect:/login";
        }

        Application app = applicationService.findById(applicationId);
        if (app == null) {
            return "redirect:/hiring-manager/candidates";
        }

        List<Interview> interviews = interviewService.findByCandidate(applicationId);
        List<Map<String, Object>> rows = new ArrayList<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Interview it : interviews) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", it.getId());

            // Candidate info
            row.put("candidateName", app.getFullName());
            row.put("jobTitle", app.getJobTitle());

            if (it.getScheduledAt() != null) {
                var ldt = it.getScheduledAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                row.put("date", dateFmt.format(ldt.toLocalDate()));
                row.put("time", timeFmt.format(ldt.toLocalTime()));
            } else {
                row.put("date", "");
                row.put("time", "");
            }

            row.put("mode", it.getMode() != null ? it.getMode() : "");
            row.put("status", it.getStatus() != null ? it.getStatus() : "");
            row.put("hmRemarks", it.getHmRemarks() != null ? it.getHmRemarks() : "");

            rows.add(row);
        }

        model.addAttribute("interviewRows", rows);
        model.addAttribute("count", rows.size());

        return "interview_list";
    }

}
