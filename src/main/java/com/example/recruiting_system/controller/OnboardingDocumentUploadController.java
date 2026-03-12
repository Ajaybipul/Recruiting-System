package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.OnboardingDocument;
import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.service.OnboardingDocumentService;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.repository.ApplicationRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/applicant/onboarding")
public class OnboardingDocumentUploadController {
    
    @Autowired
    private OnboardingDocumentService documentService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    // Helper method to check if applicant has selected applications
    private boolean hasSelectedApplication(String username) {
        List<Application> apps = applicationRepository.findByApplicantUsername(username);
        return apps.stream().anyMatch(app -> 
            "Selected".equals(app.getStatus()) || 
            "Accepted".equals(app.getStatus()) || 
            "OFFER_ACCEPTED".equals(app.getStatus()) ||
            "Onboarding Started".equals(app.getStatus()) ||
            "Onboarding".equals(app.getStatus())
        );
    }
    
    // Helper method to check if applicant has accepted offer and started onboarding
    private boolean hasAcceptedOfferAndStartedOnboarding(String username) {
        List<Application> apps = applicationRepository.findByApplicantUsername(username);
        return apps.stream().anyMatch(app -> 
            // Check for OFFER_ACCEPTED status OR onboarding already started
            "OFFER_ACCEPTED".equals(app.getStatus()) ||
            (app.getOnboardingStatus() != null && 
            ("Started".equals(app.getOnboardingStatus()) || 
             "In Progress".equals(app.getOnboardingStatus()) ||
             "Completed".equals(app.getOnboardingStatus())))
        );
    }
    
    // Display upload form for applicant
    @GetMapping("/upload")
    public String showUploadForm(HttpSession session, Model model) {
        Object userId = session.getAttribute("userId");
        Object username = session.getAttribute("username");
        
        if (userId == null || username == null) {
            return "redirect:/login";
        }
        
        // Check if applicant has accepted offer and started onboarding
        if (!hasAcceptedOfferAndStartedOnboarding(username.toString())) {
            return "redirect:/applicant/applications?error=You must accept an offer to upload onboarding documents";
        }
        
        String applicantId = userId.toString();
        List<OnboardingDocument> uploadedDocs = documentService.findDocumentsByApplicantId(applicantId);
        model.addAttribute("documents", uploadedDocs);
        
        // Calculate document statistics for display
        long totalDocuments = uploadedDocs.size();
        long approvedDocuments = uploadedDocs.stream()
                .filter(doc -> "Approved".equals(doc.getStatus()))
                .count();
        long submittedDocuments = uploadedDocs.stream()
                .filter(doc -> "Submitted".equals(doc.getStatus()))
                .count();
        
        model.addAttribute("totalDocuments", totalDocuments);
        model.addAttribute("approvedDocuments", approvedDocuments);
        model.addAttribute("submittedDocuments", submittedDocuments);
        
        return "applicant_onboarding_upload";
    }
    
    // Handle file upload
    @PostMapping("/upload")
    public String uploadDocument(
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "documentType", required = false, defaultValue = "General") String documentType,
            @RequestParam(value = "applicationId", required = false) String applicationId,
            HttpSession session,
            Model model) throws IOException {
        
        Object userId = session.getAttribute("userId");
        Object username = session.getAttribute("username");
        
        if (userId == null || username == null) {
            return "redirect:/login";
        }
        
        // Check if applicant has at least one selected application
        if (!hasSelectedApplication(username.toString())) {
            return "redirect:/applicant/applications?error=You are not selected for any job yet";
        }
        
        // Validate files were uploaded
        if (files == null || files.length == 0) {
            String applicantId = userId.toString();
            List<OnboardingDocument> uploadedDocs = documentService.findDocumentsByApplicantId(applicantId);
            model.addAttribute("documents", uploadedDocs);
            model.addAttribute("error", "Please select at least one file to upload");
            return "applicant_onboarding_upload";
        }
        
        String applicantId = userId.toString();
        String applicantName = (String) session.getAttribute("userName");
        if (applicantName == null) {
            applicantName = "Unknown";
        }
        
        // Get application details if provided
        String jobTitle = "General Application";
        if (applicationId != null && !applicationId.isEmpty()) {
            Application app = applicationService.findById(applicationId);
            if (app != null && app.getJobTitle() != null) {
                jobTitle = app.getJobTitle();
            }
        }
        
        // Create uploads directory if it doesn't exist
        String uploadsDir = System.getProperty("user.dir") + File.separator + "uploads" 
            + File.separator + "onboarding" + File.separator + applicantId;
        Path dirPath = Paths.get(uploadsDir);
        Files.createDirectories(dirPath);
        
        // Process each file
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = dirPath.resolve(fileName);
                    file.transferTo(filePath.toFile());
                    
                    // Save document metadata to database
                    OnboardingDocument doc = new OnboardingDocument();
                    doc.setCandidateId(applicantId);
                    doc.setDocumentType(documentType);
                    doc.setFilePath("/uploads/onboarding/" + applicantId + "/" + fileName);
                    doc.setStatus("Submitted");
                    doc.setSubmittedDate(new Date());
                    
                    documentService.saveDocument(doc);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    model.addAttribute("error", "Failed to upload file: " + file.getOriginalFilename());
                }
            }
        }
        
        // Redirect to upload page with success message
        model.addAttribute("success", "Documents uploaded successfully!");
        return "redirect:/applicant/onboarding/upload";
    }
    
    // Download document (for applicant to view their own)
    @GetMapping("/download/{documentId}")
    public String downloadDocument(
            @PathVariable String documentId,
            HttpSession session) {
        
        Optional<OnboardingDocument> doc = documentService.findDocumentById(documentId);
        Object userId = session.getAttribute("userId");
        
        if (doc.isPresent() && userId != null 
            && doc.get().getCandidateId().equals(userId.toString())) {
            // In a real application, you would stream the file here
            // For now, just redirect to view
            return "redirect:" + doc.get().getFilePath();
        }
        
        return "redirect:/applicant/onboarding/upload?error=Document not found";
    }
    
    // Delete document (only if not yet approved)
    @PostMapping("/delete/{documentId}")
    public String deleteDocument(
            @PathVariable String documentId,
            HttpSession session,
            Model model) {
        
        Optional<OnboardingDocument> doc = documentService.findDocumentById(documentId);
        Object userId = session.getAttribute("userId");
        
        if (doc.isPresent() && userId != null 
            && doc.get().getCandidateId().equals(userId.toString())
            && "Submitted".equals(doc.get().getStatus())) {
            
            documentService.deleteDocument(documentId);
            model.addAttribute("success", "Document deleted successfully");
        } else {
            model.addAttribute("error", "Cannot delete this document");
        }
        
        return "redirect:/applicant/onboarding/upload";
    }
}
