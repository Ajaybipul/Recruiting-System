package com.example.recruiting_system.service;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application create(Application app) {
        app.setAppliedDate(new Date());
        if (app.getStatus() == null) app.setStatus("Submitted");
        return applicationRepository.save(app);
    }

    public List<Application> findByApplicant(String username) {
        return applicationRepository.findByApplicantUsername(username);
    }

    public List<Application> findByJobId(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> findByUserId(String userId) {
        return applicationRepository.findByUserId(userId);
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    public Application shortlistApplicant(String id) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("Sent to Hiring Manager");
            applicationRepository.save(app);
        }
        return app;
    }

    public Application rejectApplicant(String id) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("Rejected");
            applicationRepository.save(app);
        }
        return app;
    }

    public Application scheduleInterview(String id, java.time.LocalDate date, java.time.LocalTime time) {
        System.out.println("=== scheduleInterview called with id: " + id + ", date: " + date + ", time: " + time);
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            System.out.println("Found application: " + app.getId() + " - " + app.getFullName() + " - Current Status: " + app.getStatus());
            app.setInterviewDate(date);
            app.setInterviewTime(time);
            app.setStatus("Interview Scheduled");
            Application saved = applicationRepository.save(app);
            System.out.println("Application saved. New Status: " + saved.getStatus() + ", InterviewDate: " + saved.getInterviewDate());
        } else {
            System.out.println("ERROR: Application not found with id: " + id);
        }
        return app;
    }

    public Application scheduleInterviewWithDetails(String id, java.time.LocalDate date, java.time.LocalTime time, String mode, String location, String interviewId) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setInterviewDate(date);
            app.setInterviewTime(time);
            app.setInterviewMode(mode);
            app.setInterviewLocation(location);
            app.setInterviewId(interviewId);
            app.setStatus("Interview Scheduled");
            applicationRepository.save(app);
        }
        return app;
    }

    public Application submitFeedback(String id, Integer technicalScore, Integer communicationScore, Integer cultureFitScore, String notes, String recommendation) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setTechnicalScore(technicalScore);
            app.setCommunicationScore(communicationScore);
            app.setCultureFitScore(cultureFitScore);
            app.setInterviewerNotes(notes);
            app.setRecommendation(recommendation);
            // Update status
            app.setStatus("Interview Completed");
            if (recommendation != null && recommendation.equalsIgnoreCase("Hire")) {
                app.setStatus("Selected");
            } else if (recommendation != null && recommendation.equalsIgnoreCase("Reject")) {
                app.setStatus("Rejected");
            }
            applicationRepository.save(app);
        }
        return app;
    }

    public Application attachOffer(String applicationId, String offerId, String offerStatus) {
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app != null) {
            app.setOfferId(offerId);
            app.setOfferStatus(offerStatus);
            app.setOfferCreatedDate(new Date());
            app.setStatus("Offer Created");
            applicationRepository.save(app);
        }
        return app;
    }

    public Application startOnboarding(String applicationId) {
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app != null) {
            app.setOnboardingStatus("Started");
            app.setOnboardingVerificationStatus("Pending");
            applicationRepository.save(app);
        }
        return app;
    }

    public Application forwardToHiringManager(String id) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setForwardedToHiringManager(true);
            app.setStatus("Sent to Hiring Manager");
            applicationRepository.save(app);
        }
        return app;
    }

    public Application findById(String id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public Application updateApplication(Application app) {
        return applicationRepository.save(app);
    }

    // ---------------------------
    // 🌟 FIXED NEW METHODS BELOW
    // ---------------------------

    public long count() {
        return applicationRepository.count();
    }

    public long countByStatus(String status) {
        return applicationRepository.countByStatus(status);
    }

    public List<Application> findByStatus(String status) {
        return applicationRepository.findByStatus(status);
    }

    public List<Application> findRecent(int limit) {
        return applicationRepository.findTop5ByOrderByAppliedDateDesc();
    }

    public boolean hasApplied(String username, String jobId) {
        List<Application> apps = applicationRepository.findByApplicantUsername(username);
        return apps.stream().anyMatch(a -> a.getJobId().equals(jobId));
    }

    // DEBUG: Check what MongoDB returns
    public Application findByIdWithDebug(String id) {
        System.out.println("🔍 DEBUG: findByIdWithDebug called for id: " + id);
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            System.out.println("✓ Application found");
            System.out.println("  id: " + app.getId());
            System.out.println("  fullName: " + app.getFullName());
            System.out.println("  email: " + app.getEmail());
            System.out.println("  status: " + app.getStatus());
            System.out.println("  hmFeedback: " + app.getHmFeedback());
        } else {
            System.out.println("✗ Application is NULL");
        }
        return app;
    }
}
