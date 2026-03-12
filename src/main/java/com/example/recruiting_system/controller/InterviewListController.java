package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Interview;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.CandidateService;
import com.example.recruiting_system.service.InterviewService;
import com.example.recruiting_system.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class InterviewListController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ApplicationService applicationService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private boolean hasRecruiterOrHiringRole(HttpSession session) {
        Object rolesObj = session.getAttribute("roles");
        if (!(rolesObj instanceof java.util.List)) return false;
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) rolesObj;
        return roles.contains("ROLE_RECRUITER") || roles.contains("ROLE_HIRING_MANAGER");
    }

    @GetMapping("/interviews")
    public String listAllInterviews(HttpSession session, Model model) {

        if (!hasRecruiterOrHiringRole(session)) {
            return "redirect:/login";
        }

        List<Interview> interviews = interviewService.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();

        for (Interview it : interviews) {
            Map<String, Object> r = new HashMap<>();
            r.put("id", it.getId());

            // Fetch candidate info from Application using candidateId (which stores applicationId)
            if (it.getCandidateId() != null) {
                com.example.recruiting_system.model.Application app = applicationService.findById(it.getCandidateId());
                if (app != null) {
                    r.put("candidateName", app.getFullName());
                    r.put("email", app.getEmail());
                }
            }

            if (!r.containsKey("candidateName")) {
                r.put("candidateName", "");
            }

            // job title
            if (it.getJobPositionId() != null) {
                jobPositionService.findById(it.getJobPositionId())
                        .ifPresent(jp -> r.put("jobTitle", jp.getTitle() == null ? "" : jp.getTitle()));
            } else {
                r.put("jobTitle", "");
            }

            // scheduled date/time
            if (it.getScheduledAt() != null) {
                var ldt = it.getScheduledAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                r.put("date", DATE_FMT.format(ldt.toLocalDate()));
                r.put("time", TIME_FMT.format(ldt.toLocalTime()));
            } else {
                r.put("date", "");
                r.put("time", "");
            }

            r.put("mode", it.getMode() == null ? "" : it.getMode());

            rows.add(r);
        }

        model.addAttribute("interviewRows", rows);
        model.addAttribute("count", rows.size());

        return "interview_list";
    }

    @GetMapping("/interview/{id}")
    public String viewInterviewDetails(@PathVariable String id, HttpSession session, Model model) {

        if (!hasRecruiterOrHiringRole(session)) {
            return "redirect:/login";
        }

        Interview interview = interviewService.findById(id).orElse(null);

        // Redirect to the proper hiring manager candidate detail page where feedback form is integrated
        // The interview contains candidateId which is the applicationId
        if (interview != null && interview.getCandidateId() != null) {
            String applicationId = interview.getCandidateId();
            System.out.println("Redirecting from old /interview/{id} to proper candidate detail page with applicationId: " + applicationId);
            return "redirect:/hiring-manager/candidate/" + applicationId;
        }

        // Fallback to dashboard if interview not found
        return "redirect:/hiring-manager/dashboard";
    }
}
