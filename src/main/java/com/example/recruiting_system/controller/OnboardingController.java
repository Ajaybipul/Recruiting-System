package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.Onboarding;
import com.example.recruiting_system.model.OnboardingDocument;
import com.example.recruiting_system.model.OnboardingChecklist;
import com.example.recruiting_system.model.OnboardingTask;
import com.example.recruiting_system.model.ApplicantProfile;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.OnboardingService;
import com.example.recruiting_system.repository.ApplicationRepository;
import com.example.recruiting_system.repository.ApplicantProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Controller
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicantProfileRepository applicantProfileRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // ONBOARDING SPECIALIST DASHBOARD - Only HR/Onboarding can access
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN')")
    @GetMapping({"/onboard/dashboard", "/onboarding/dashboard"})
    public String onboardingSpecialistDashboard(Model model) {
        List<Onboarding> allOnboarding = onboardingService.findAllOnboarding();
        long totalOnboarding = allOnboarding.size();
        long inProgress = allOnboarding.stream().filter(o -> "In Progress".equals(o.getOverallStatus())).count();
        long completed = allOnboarding.stream().filter(o -> "Completed".equals(o.getOverallStatus())).count();
        long pendingDocVerification = onboardingService.findPendingDocumentVerification().size();
        
        model.addAttribute("totalOnboarding", totalOnboarding);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("completed", completed);
        model.addAttribute("pendingDocVerification", pendingDocVerification);
        model.addAttribute("onboardingList", allOnboarding);
        
        return "onboarding_dashboard";
    }

    // Debug endpoint: fetch onboarding and documents for a candidateId
    @GetMapping("/debug/onboarding/{candidateId}")
    public ResponseEntity<?> debugOnboardingForCandidate(@PathVariable String candidateId) {
        try {
            Onboarding onboarding = onboardingService.getOnboardingByCandidateId(candidateId);
            java.util.List<OnboardingDocument> docs = onboardingService.findDocumentByCandidate(candidateId);
            java.util.Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("onboarding", onboarding);
            resp.put("documents", docs);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            java.util.Map<String, String> err = new java.util.HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CANDIDATES LIST FOR ONBOARDING SPECIALIST - Only HR/Onboarding can access
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN')")
    @GetMapping("/onboard/candidates")
    public String onboardingCandidates(Model model) {
        List<Onboarding> candidates = onboardingService.findAllOnboarding();
        model.addAttribute("candidates", candidates);
        return "onboarding_candidates";
    }

    // Alternative URL mapping for consistency
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN')")
    @GetMapping("/onboarding/candidates")
    public String onboardingCandidatesAlt(Model model) {
        return onboardingCandidates(model);
    }

    // INDIVIDUAL CANDIDATE ONBOARDING DETAIL
    @GetMapping("/onboard/candidates/{id}")
    public String candidateOnboardingDetail(@PathVariable String id, Model model) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            onboarding = new Onboarding();
        }
        Application application = applicationService.findById(id);
        
        // If direct lookup fails, force-use Onboarding data
        if (application == null && onboarding != null && onboarding.getCandidateName() != null) {
            application = new Application();
            application.setId(id);
            application.setFullName(onboarding.getCandidateName());
            application.setEmail(onboarding.getCandidateEmail());
            application.setJobTitle(onboarding.getJobTitle());
            application.setPhone(""); // placeholder
        } else if (application == null) {
            application = new Application();
            application.setId(id);
            application.setFullName("Candidate Details");
            application.setJobTitle("Position");
        }
        
        // Also try to load an onboarding checklist (if any) to reflect completion percentage
        com.example.recruiting_system.model.OnboardingChecklist checklist = onboardingService.getChecklistByApplicationId(id);

        // If there's a checklist already at 100% but onboarding isn't marked Completed, update it now
        try {
            if (checklist != null && checklist.getCompletionPercentage() == 100
                    && (onboarding.getOverallStatus() == null || !"Completed".equals(onboarding.getOverallStatus()))) {
                onboarding = onboardingService.completeOnboarding(onboarding);
            }
        } catch (Exception ignored) {}

        // Create a HashMap with candidate data for Thymeleaf rendering
        // This avoids Spring Data MongoDB serialization issues with @Field annotations
        Map<String, Object> candidateData = new HashMap<>();
        if (application != null) {
            candidateData.put("fullName", application.getFullName() != null ? application.getFullName() : "");
            candidateData.put("email", application.getEmail() != null ? application.getEmail() : "");
            candidateData.put("phone", application.getPhone() != null ? application.getPhone() : "");
            candidateData.put("jobTitle", application.getJobTitle() != null ? application.getJobTitle() : "");
            candidateData.put("experience", application.getExperience() != null ? application.getExperience() : "");
            candidateData.put("skills", application.getSkills() != null ? application.getSkills() : "");
            candidateData.put("resumePath", application.getResumePath() != null ? application.getResumePath() : "");
        }

        model.addAttribute("onboarding", onboarding);
        model.addAttribute("checklist", checklist);
        model.addAttribute("candidateData", candidateData);
        return "onboarding_candidate_detail";
    }

    // STEP 1: PERSONAL INFO COLLECTION
    @GetMapping("/onboard/candidates/{id}/personal-info")
    public String showPersonalInfoForm(@PathVariable String id, Model model) {
        Application app = applicationService.findById(id);
        if (app == null) {
            return "redirect:/login";
        }

        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            onboarding = onboardingService.initializeOnboarding(app);
        }

        // Always try to prefill from ApplicantProfile if available; only overwrite empty onboarding fields
        try {
            Optional<ApplicantProfile> profileOpt = applicantProfileRepository.findByUserId(app.getUserId());
            if (profileOpt.isPresent()) {
                ApplicantProfile profile = profileOpt.get();
                if ((onboarding.getFirstName() == null || onboarding.getFirstName().isEmpty()) && profile.getFirstName() != null)
                    onboarding.setFirstName(profile.getFirstName());
                if ((onboarding.getLastName() == null || onboarding.getLastName().isEmpty()) && profile.getLastName() != null)
                    onboarding.setLastName(profile.getLastName());
                if ((onboarding.getAddress() == null || onboarding.getAddress().isEmpty()) && profile.getAddress() != null)
                    onboarding.setAddress(profile.getAddress());
                if ((onboarding.getDateOfBirth() == null || onboarding.getDateOfBirth().isEmpty()) && profile.getDateOfBirth() != null)
                    onboarding.setDateOfBirth(profile.getDateOfBirth());
                if ((onboarding.getEmergencyContact() == null || onboarding.getEmergencyContact().isEmpty()) && profile.getEmergencyContact() != null)
                    onboarding.setEmergencyContact(profile.getEmergencyContact());
                if ((onboarding.getBankDetails() == null || onboarding.getBankDetails().isEmpty()) && profile.getBankDetails() != null)
                    onboarding.setBankDetails(profile.getBankDetails());
            }
        } catch (Exception e) {
            System.err.println("Error fetching applicant profile: " + e.getMessage());
        }

        model.addAttribute("onboarding", onboarding);
        model.addAttribute("application", app);
        return "onboarding_personal_info";
    }

    @PostMapping("/onboard/candidates/{id}/update-doc-status")
    public String updateDocumentStatus(@PathVariable String id,
                                       @RequestParam(required = false) String status) {
        Application app = applicationService.findById(id);
        if (app == null) return "redirect:/onboard/candidates";

        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            onboarding = onboardingService.initializeOnboarding(app);
        }

        // Normalize and apply status
        if (status == null || status.isBlank()) {
            onboarding.setStep2VerificationStatus("Pending");
            onboarding.setStep2Status("Pending");
            onboarding.setStep2Completed(false);
            onboarding.setIdProofUploaded(false);
            onboarding.setAddressProofUploaded(false);
            onboarding.setCertificatesUploaded(false);
        } else if ("Submitted".equalsIgnoreCase(status)) {
            onboarding.setStep2VerificationStatus("Submitted");
            onboarding.setStep2Status("In Progress");
            onboarding.setStep2Completed(false);
            // mark uploaded flags true so detail page shows documents uploaded
            onboarding.setIdProofUploaded(true);
            onboarding.setAddressProofUploaded(true);
            onboarding.setCertificatesUploaded(true);
        } else if ("Verified".equalsIgnoreCase(status)) {
            onboarding.setStep2VerificationStatus("Verified");
            onboarding.setStep2Status("Completed");
            onboarding.setStep2Completed(true);
            onboarding.setStep2CompletedDate(new java.util.Date());
            // mark uploaded + verified state
            onboarding.setIdProofUploaded(true);
            onboarding.setAddressProofUploaded(true);
            onboarding.setCertificatesUploaded(true);
        } else if ("Rejected".equalsIgnoreCase(status)) {
            onboarding.setStep2VerificationStatus("Rejected");
            onboarding.setStep2Status("Pending");
            onboarding.setStep2Completed(false);
            // keep uploaded flags true if they exist; do not force remove uploads on rejection
        } else if ("More Info Requested".equalsIgnoreCase(status)) {
            onboarding.setStep2VerificationStatus("More Info Requested");
            onboarding.setStep2Status("In Progress");
            onboarding.setStep2Completed(false);
            // ensure uploaded flags true so HR/candidate can see uploaded docs
            onboarding.setIdProofUploaded(true);
            onboarding.setAddressProofUploaded(true);
            onboarding.setCertificatesUploaded(true);
        }

        onboardingService.saveOnboarding(onboarding);
        return "redirect:/onboard/candidates";
    }

    @PostMapping("/onboard/candidates/{id}/personal-info")
    public String savePersonalInfo(@PathVariable String id,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam String address,
                                    @RequestParam String dateOfBirth,
                                    @RequestParam String emergencyContact,
                                    @RequestParam String bankDetails) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        
        onboarding.setFirstName(firstName);
        onboarding.setLastName(lastName);
        onboarding.setAddress(address);
        onboarding.setDateOfBirth(dateOfBirth);
        onboarding.setEmergencyContact(emergencyContact);
        onboarding.setBankDetails(bankDetails);
        onboardingService.savePersonalInfo(onboarding);
        
        return "redirect:/onboard/candidates/" + id;
    }

    // Quick action to force-mark training completed for an application (useful for admin/debug)
    @GetMapping("/onboard/candidates/{id}/force-complete-training")
    public String forceCompleteTraining(@PathVariable String id) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            if (app == null) return "redirect:/onboard/candidates";
            onboarding = onboardingService.initializeOnboarding(app);
        }
        onboardingService.completeTraining(onboarding);
        return "redirect:/onboard/candidates/" + id;
    }

    // STEP 2: DOCUMENT UPLOAD
    @GetMapping("/onboard/candidates/{id}/documents")
    public String showDocuments(@PathVariable String id, Model model, HttpSession session) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        Application app = applicationService.findById(id);
        List<OnboardingDocument> docs = onboardingService.findDocumentByCandidate(app.getUserId());
        
        model.addAttribute("onboarding", onboarding);
        model.addAttribute("application", app);
        model.addAttribute("documents", docs);
        return "onboarding_documents";
    }

    @PostMapping("/onboard/candidates/{id}/upload-document")
    public String uploadDocument(@PathVariable String id,
                                 @RequestParam String documentType,
                                 @RequestParam MultipartFile file,
                                 HttpSession session) throws IOException {
        Application app = applicationService.findById(id);
        if (app == null) return "redirect:/onboard/candidates";
        
        String candidateId = app.getUserId();
        String uploadsDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "onboarding" + File.separator + candidateId;
        Path dir = Paths.get(uploadsDir);
        Files.createDirectories(dir);
        
        String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path dest = dir.resolve(filename);
        file.transferTo(dest.toFile());

        OnboardingDocument doc = new OnboardingDocument();
        doc.setCandidateId(candidateId);
        doc.setDocumentType(documentType);
        doc.setStatus("Submitted");
        doc.setSubmittedDate(new java.util.Date());
        doc.setFilePath("/uploads/onboarding/" + candidateId + "/" + filename);
        onboardingService.submitDocument(doc);

        // Update onboarding document flags
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        // If onboarding record doesn't exist yet, initialize it so flags can be set
        if (onboarding == null) {
            onboarding = onboardingService.initializeOnboarding(app);
        }
        if (onboarding != null) {
            if ("ID Proof".equals(documentType)) onboarding.setIdProofUploaded(true);
            else if ("Address Proof".equals(documentType)) onboarding.setAddressProofUploaded(true);
            else if ("Certificates".equals(documentType)) onboarding.setCertificatesUploaded(true);
            else if ("Offer Letter".equals(documentType)) onboarding.setOfferLetterUploaded(true);
            // Update step2 status/verification so UI shows Submitted/Completed appropriately
            onboarding.setStep2Status("In Progress");
            onboarding.setStep2VerificationStatus("Submitted");

            // If all required documents are uploaded, mark step2 completed
            boolean allUploaded = onboarding.isIdProofUploaded() && onboarding.isAddressProofUploaded() && onboarding.isCertificatesUploaded();
            if (allUploaded) {
                onboarding.setStep2Completed(true);
                onboarding.setStep2Status("Completed");
                // keep verification status as Submitted until HR verifies
            }

            onboardingService.saveOnboarding(onboarding);
        }

        return "redirect:/onboard/candidates/" + id + "/documents";
    }

    // STEP 3: WORKSTATION SETUP
    @GetMapping("/onboard/candidates/{id}/workstation")
    public String showWorkstationForm(@PathVariable String id, Model model) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        model.addAttribute("onboarding", onboarding);
        return "onboarding_workstation";
    }

    @PostMapping("/onboard/candidates/{id}/workstation")
    public String saveWorkstationSetup(@PathVariable String id,
                                        @RequestParam(defaultValue = "false") boolean laptopAssigned,
                                        @RequestParam(required = false) String laptopModel,
                                        @RequestParam(defaultValue = "false") boolean emailCreated,
                                        @RequestParam(required = false) String emailId,
                                        @RequestParam(defaultValue = "false") boolean accessPermissionsGranted,
                                        @RequestParam(defaultValue = "false") boolean departmentToolsSetup) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        
        onboarding.setLaptopAssigned(laptopAssigned);
        onboarding.setLaptopModel(laptopModel);
        onboarding.setEmailCreated(emailCreated);
        onboarding.setEmailId(emailId);
        onboarding.setAccessPermissionsGranted(accessPermissionsGranted);
        onboarding.setDepartmentToolsSetup(departmentToolsSetup);
        onboardingService.saveWorkstationSetup(onboarding);
        
        return "redirect:/onboard/candidates/" + id;
    }

    // STEP 4: ORIENTATION SCHEDULING
    @GetMapping("/onboard/candidates/{id}/orientation")
    public String showOrientationForm(@PathVariable String id, Model model) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        model.addAttribute("onboarding", onboarding);
        return "onboarding_orientation";
    }

    @PostMapping("/onboard/candidates/{id}/orientation")
    public String scheduleOrientation(@PathVariable String id,
                                      @RequestParam String orientationDate,
                                      @RequestParam String orientationTime,
                                      @RequestParam String orientationMode,
                                      @RequestParam(required = false) String meetingLink,
                                      @RequestParam(required = false) String orientationLocation,
                                      @RequestParam(required = false) String assignedTrainer) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        
        try {
            onboarding.setOrientationDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(orientationDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        onboarding.setOrientationTime(orientationTime);
        onboarding.setOrientationMode(orientationMode);
        onboarding.setMeetingLink(meetingLink);
        onboarding.setOrientationLocation(orientationLocation);
        onboarding.setAssignedTrainer(assignedTrainer);
        onboardingService.scheduleOrientation(onboarding);
        
        return "redirect:/onboard/candidates/" + id;
    }

    // STEP 5: TRAINING TRACKING
    @GetMapping("/onboard/candidates/{id}/training")
    public String showTrainingForm(@PathVariable String id, Model model) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        model.addAttribute("onboarding", onboarding);
        return "onboarding_training";
    }

    @PostMapping("/onboard/candidates/{id}/training")
    public String saveTrainingInfo(@PathVariable String id,
                                   @RequestParam(defaultValue = "false") boolean basicInductionCompleted,
                                   @RequestParam(defaultValue = "false") boolean roleBasedTrainingCompleted,
                                   @RequestParam(required = false) String trainingCompletionStatus) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        
        onboarding.setBasicInductionCompleted(basicInductionCompleted);
        if (basicInductionCompleted) {
            onboarding.setBasicInductionDate(new java.util.Date());
        }
        onboarding.setRoleBasedTrainingCompleted(roleBasedTrainingCompleted);
        if (roleBasedTrainingCompleted) {
            onboarding.setRoleBasedTrainingDate(new java.util.Date());
        }
        if (trainingCompletionStatus != null) {
            onboarding.setTrainingCompletionStatus(trainingCompletionStatus);
        }
        onboardingService.completeTraining(onboarding);
        
        return "redirect:/onboard/candidates/" + id;
    }

    // STEP 6: FINAL ONBOARDING COMPLETION
    @GetMapping("/onboard/candidates/{id}/complete")
    public String showCompletionForm(@PathVariable String id, Model model) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        model.addAttribute("onboarding", onboarding);
        return "onboarding_complete";
    }

    @PostMapping("/onboard/candidates/{id}/complete")
    public String completeOnboarding(@PathVariable String id,
                                     @RequestParam(defaultValue = "false") boolean sendHrNotification,
                                     @RequestParam(defaultValue = "false") boolean sendHmNotification,
                                     @RequestParam(defaultValue = "false") boolean sendWelcomeEmail) {
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(id);
        if (onboarding == null) {
            Application app = applicationService.findById(id);
            onboarding = onboardingService.initializeOnboarding(app);
        }
        
        onboarding.setHrNotificationStatus(sendHrNotification ? "Sent" : "Not Sent");
        onboarding.setHiringManagerNotificationStatus(sendHmNotification ? "Sent" : "Not Sent");
        onboarding.setWelcomeEmailSent(sendWelcomeEmail);
        
        onboardingService.completeOnboarding(onboarding);
        
        return "redirect:/onboard/dashboard";
    }

    // LEGACY APPLICANT ENDPOINTS
    @GetMapping("/applicant/onboarding")
    public String onboardingDashboard(HttpSession session, Model model) {
        Object uid = session.getAttribute("userId");
        String candidateId = uid != null ? uid.toString() : null;
        List<Application> apps = candidateId != null ? applicationService.findByUserId(candidateId) : java.util.Collections.emptyList();
        model.addAttribute("applications", apps);
        return "onboarding_dashboard";
    }

    @GetMapping("/applicant/onboarding/documents/{applicationId}")
    public String showApplicantDocuments(@PathVariable String applicationId, Model model, HttpSession session) {
        Application app = applicationService.findById(applicationId);
        model.addAttribute("application", app);
        Object uid = session.getAttribute("userId");
        String candidateId = uid != null ? uid.toString() : null;
        List<OnboardingDocument> docs = candidateId != null ? onboardingService.findDocumentByCandidate(candidateId) : java.util.Collections.emptyList();
        model.addAttribute("documents", docs);
        return "onboarding_documents";
    }

    // HR / ONBOARDING SPECIALIST VERIFICATION - Only HR/Onboarding can access (Pending Approvals)
    @GetMapping("/hr/onboarding/verify")
    public String verifyList(Model model) {
        List<OnboardingDocument> docs = onboardingService.findAllDocuments();
        
        // Filter only pending documents (not verified or rejected)
        List<OnboardingDocument> pendingDocs = docs.stream()
            .filter(doc -> !"Verified".equalsIgnoreCase(doc.getStatus()) && 
                          !"Rejected".equalsIgnoreCase(doc.getStatus()))
            .collect(Collectors.toList());
        
        // Group documents by candidateId
        Map<String, List<Map<String, Object>>> groupedByCandidate = new HashMap<>();
        Map<String, String> candidateNames = new HashMap<>();
        
        for (OnboardingDocument doc : pendingDocs) {
            String candidateId = doc.getCandidateId();
            
            // Get candidate name (only once per candidate)
            if (!candidateNames.containsKey(candidateId)) {
                List<Application> apps = applicationRepository.findByUserId(candidateId);
                if (!apps.isEmpty()) {
                    candidateNames.put(candidateId, apps.get(0).getFullName());
                } else {
                    candidateNames.put(candidateId, "Unknown");
                }
            }
            
            // Create enriched document
            Map<String, Object> enrichedDoc = new HashMap<>();
            enrichedDoc.put("id", doc.getId());
            enrichedDoc.put("candidateId", doc.getCandidateId());
            enrichedDoc.put("documentType", doc.getDocumentType());
            enrichedDoc.put("status", doc.getStatus());
            enrichedDoc.put("filePath", doc.getFilePath());
            enrichedDoc.put("submittedDate", doc.getSubmittedDate());
            enrichedDoc.put("verifiedById", doc.getVerifiedById());
            enrichedDoc.put("verificationDate", doc.getVerificationDate());
            enrichedDoc.put("notes", doc.getNotes());
            enrichedDoc.put("createdDate", doc.getCreatedDate());
            enrichedDoc.put("updatedDate", doc.getUpdatedDate());
            
            // Add to grouped map
            groupedByCandidate.computeIfAbsent(candidateId, k -> new java.util.ArrayList<>()).add(enrichedDoc);
        }
        
        // Create list of candidate groups with all their documents
        List<Map<String, Object>> candidateGroups = new java.util.ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedByCandidate.entrySet()) {
            String candidateId = entry.getKey();
            List<Map<String, Object>> candidateDocs = entry.getValue();
            
            Map<String, Object> group = new HashMap<>();
            group.put("candidateId", candidateId);
            group.put("candidateName", candidateNames.get(candidateId));
            group.put("documents", candidateDocs);
            group.put("id", candidateId);
            
            // Add first document info for easy access in template
            if (!candidateDocs.isEmpty()) {
                Map<String, Object> firstDoc = candidateDocs.get(0);
                group.put("firstDocumentId", firstDoc.get("id"));
                group.put("firstDocumentType", firstDoc.get("documentType"));
                group.put("firstSubmittedDate", firstDoc.get("submittedDate"));
            }
            
            candidateGroups.add(group);
        }
        
        model.addAttribute("documents", candidateGroups);
        return "onboarding_verify";
    }

    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ADMIN')")
    @PostMapping("/hr/onboarding/verify")
    public String verifyDocument(@RequestParam String documentId,
                                 @RequestParam String action,
                                 @RequestParam(required = false) String notes,
                                 HttpSession session) {
        Optional<OnboardingDocument> opt = onboardingService.findDocumentById(documentId);
        if (opt.isPresent()) {
            OnboardingDocument doc = opt.get();
            // Handle approve action
            if ("approve".equalsIgnoreCase(action) || "verify".equalsIgnoreCase(action)) {
                doc.setStatus("Verified");
                doc.setVerifiedById(session.getAttribute("userId") != null ? session.getAttribute("userId").toString() : "system");
                doc.setVerificationDate(new java.util.Date());
            } else if ("request-info".equalsIgnoreCase(action)) {
                // Handle request more info action
                doc.setStatus("More Info Requested");
                if (notes != null && !notes.trim().isEmpty()) {
                    doc.setNotes(notes);
                }
            } else {
                // Default to rejected
                doc.setStatus("Rejected");
            }
            onboardingService.saveDocument(doc);

            // Ensure onboarding step2 reflects verification
            try {
                String candidateId = doc.getCandidateId();
                Onboarding onboarding = onboardingService.getOnboardingByCandidateId(candidateId);
                if (onboarding != null) {
                    // If doc was verified, mark step2 verification as Verified and set completed when appropriate
                    if ("Verified".equalsIgnoreCase(doc.getStatus())) {
                        onboarding.setStep2VerificationStatus("Verified");
                        onboarding.setStep2Status("Completed");
                        onboarding.setStep2Completed(true);
                        onboarding.setStep2CompletedDate(new java.util.Date());
                    } else if ("Rejected".equalsIgnoreCase(doc.getStatus())) {
                        onboarding.setStep2VerificationStatus("Rejected");
                        onboarding.setStep2Status("Pending");
                        onboarding.setStep2Completed(false);
                    } else if ("More Info Requested".equalsIgnoreCase(doc.getStatus())) {
                        onboarding.setStep2VerificationStatus("More Info Requested");
                        onboarding.setStep2Status("In Progress");
                    }
                    onboardingService.saveOnboarding(onboarding);
                }
            } catch (Exception e) {
                System.err.println("Error updating onboarding verification status: " + e.getMessage());
            }
        }
        return "redirect:/hr/onboarding/verify";
    }

    // MANAGER ONBOARDING CHECKLIST - Manager marks tasks as complete
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_TEAM_LEAD', 'ROLE_HR', 'ROLE_ADMIN')")
    @GetMapping("/manager/onboarding/checklist")
    public String managerChecklistList(Model model) {
        List<OnboardingChecklist> checklists = onboardingService.findAllChecklists();
        
        // Filter only in-progress checklists (not started or in progress)
        List<OnboardingChecklist> activeChecklists = checklists.stream()
            .filter(cl -> !"Completed".equalsIgnoreCase(cl.getOverallStatus()))
            .collect(Collectors.toList());
        
        model.addAttribute("checklists", activeChecklists);
        return "manager_checklist";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_TEAM_LEAD', 'ROLE_HR', 'ROLE_ADMIN')")
    @GetMapping("/manager/onboarding/checklist/{checklistId}")
    public String managerChecklistDetail(@PathVariable String checklistId, Model model) {
        Optional<OnboardingChecklist> opt = onboardingService.findChecklistById(checklistId);
        if (opt.isPresent()) {
            OnboardingChecklist checklist = opt.get();
            model.addAttribute("checklist", checklist);
            
            // Group tasks by phase
            Map<String, List<OnboardingTask>> tasksByPhase = new LinkedHashMap<>();
            if (checklist.getTasks() != null) {
                for (OnboardingTask task : checklist.getTasks()) {
                    String phase = task.getPhase() != null ? task.getPhase() : "Other";
                    tasksByPhase.computeIfAbsent(phase, k -> new ArrayList<>()).add(task);
                }
            }
            model.addAttribute("tasksByPhase", tasksByPhase);
            return "manager_checklist_detail";
        }
        return "redirect:/manager/onboarding/checklist";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_TEAM_LEAD', 'ROLE_HR', 'ROLE_ADMIN')")
    @PostMapping("/manager/onboarding/checklist/task/complete")
    public String completeTask(@RequestParam String checklistId,
                               @RequestParam String taskId,
                               @RequestParam(required = false) String notes,
                               HttpSession session) {
        Optional<OnboardingChecklist> opt = onboardingService.findChecklistById(checklistId);
        if (opt.isPresent()) {
            OnboardingChecklist checklist = opt.get();
            
            // Find and update the task
            if (checklist.getTasks() != null) {
                for (OnboardingTask task : checklist.getTasks()) {
                    if (task.getId().equals(taskId)) {
                        task.setStatus("Completed");
                        task.setCompletedDate(new java.util.Date());
                        if (notes != null && !notes.trim().isEmpty()) {
                            task.setNotes(notes);
                        }
                        break;
                    }
                }
            }
            
            // Calculate new completion percentage
            int totalTasks = checklist.getTasks() != null ? checklist.getTasks().size() : 0;
            int completedTasks = checklist.getTasks() != null 
                ? (int) checklist.getTasks().stream()
                    .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()))
                    .count() 
                : 0;
            
            int completionPercentage = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
            checklist.setCompletionPercentage(completionPercentage);
            
            // Update overall status
            if (completionPercentage == 100) {
                checklist.setOverallStatus("Completed");
            } else if (completionPercentage > 0) {
                checklist.setOverallStatus("In Progress");
            }
            
            checklist.setUpdatedDate(new java.util.Date());
            checklist.setUpdatedBy(session.getAttribute("username") != null 
                ? session.getAttribute("username").toString() : "system");
            
            onboardingService.saveChecklist(checklist);

            // If checklist completed, propagate to onboarding training step (step5)
            try {
                if ("Completed".equalsIgnoreCase(checklist.getOverallStatus())) {
                    String applicationId = checklist.getApplicationId();
                    Onboarding onboarding = onboardingService.getOnboardingByApplicationId(applicationId);
                    if (onboarding != null) {
                        onboardingService.completeTraining(onboarding);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error propagating checklist completion to onboarding: " + e.getMessage());
            }
        }
        return "redirect:/manager/onboarding/checklist/{checklistId}".replace("{checklistId}", checklistId);
    }
    
    // Documents list for onboarding team
    @GetMapping("/onboarding/documents")
    public String onboardingDocuments(Model model) {
        List<OnboardingDocument> documents = onboardingService.findAllDocuments();
        long totalDocuments = documents.size();
        long pendingDocuments = documents.stream().filter(d -> "Submitted".equals(d.getStatus())).count();
        long approvedDocuments = documents.stream().filter(d -> "Verified".equals(d.getStatus())).count();
        long rejectedDocuments = documents.stream().filter(d -> "Rejected".equals(d.getStatus())).count();
        
        model.addAttribute("documents", documents);
        model.addAttribute("totalDocuments", totalDocuments);
        model.addAttribute("pendingDocuments", pendingDocuments);
        model.addAttribute("approvedDocuments", approvedDocuments);
        model.addAttribute("rejectedDocuments", rejectedDocuments);
        
        return "onboarding_documents";
    }
    
    // ============ ONBOARDING CHECKLIST ENDPOINTS ============
    
    // View new hires (candidates with OFFER_ACCEPTED status) - Only HR/Onboarding can access
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN', 'ROLE_RECRUITER', 'ROLE_HIRING_MANAGER')")
    @GetMapping("/onboarding/new-hires")
    public String viewNewHires(Model model, HttpSession session) {
        // Get all applications with OFFER_ACCEPTED status
        List<Application> allApps = applicationService.findAll();
        System.out.println("=== viewNewHires Called ===");
        System.out.println("Total applications: " + allApps.size());
        
        List<Application> newHires = allApps.stream()
            .filter(app -> "OFFER_ACCEPTED".equals(app.getStatus()))
            .toList();
        
        System.out.println("Applications with OFFER_ACCEPTED status: " + newHires.size());
        for (Application app : newHires) {
            System.out.println("  - ID: " + app.getId() + ", Name: " + app.getFullName() + ", Status: " + app.getStatus());
        }
        
        model.addAttribute("newHires", newHires);
        model.addAttribute("totalNewHires", newHires.size());
        
        return "onboarding_new_hires";
    }
    
    // View candidate details for checklist creation - Only HR/Onboarding can access
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN', 'ROLE_RECRUITER', 'ROLE_HIRING_MANAGER')")
    @GetMapping("/onboarding/new-hires/{applicationId}")
    public String viewNewHireDetails(@PathVariable String applicationId, Model model) {
        System.out.println("=== viewNewHireDetails Called ===");
        System.out.println("ApplicationId: " + applicationId);
        
        Application application = applicationService.findById(applicationId);
        
        System.out.println("Application found: " + (application != null));
        if (application != null) {
            System.out.println("  - Full Name: '" + application.getFullName() + "'");
            System.out.println("  - Email: '" + application.getEmail() + "'");
            System.out.println("  - Phone: '" + application.getPhone() + "'");
            System.out.println("  - Job Title: '" + application.getJobTitle() + "'");
            System.out.println("  - Status: '" + application.getStatus() + "'");
            System.out.println("  - Applied Date: " + application.getAppliedDate());
            System.out.println("  - Application Object Class: " + application.getClass().getName());
            System.out.println("  - Application ID: " + application.getId());
            
            // Create a map with the data to ensure it's accessible
            Map<String, Object> appData = new HashMap<>();
            appData.put("id", application.getId());
            appData.put("fullName", application.getFullName());
            appData.put("email", application.getEmail());
            appData.put("phone", application.getPhone());
            appData.put("jobTitle", application.getJobTitle());
            appData.put("appliedDate", application.getAppliedDate());
            appData.put("status", application.getStatus());
            
            System.out.println("Data Map created successfully");
            model.addAttribute("appData", appData);
        } else {
            System.out.println("DEBUG: Application NOT found!");
        }
        
        model.addAttribute("application", application);
        model.addAttribute("applicationId", applicationId);
        
        OnboardingChecklist checklist = onboardingService.getChecklistByApplicationId(applicationId);
        System.out.println("Checklist found: " + (checklist != null));
        model.addAttribute("checklist", checklist);
        
        System.out.println("=== Returning onboarding_new_hire_details ===");
        return "onboarding_new_hire_details";
    }
    
    // Show create checklist form
    @GetMapping("/onboarding/create-checklist/{applicationId}")
    public String showCreateChecklistForm(@PathVariable String applicationId, Model model) {
        Application application = applicationService.findById(applicationId);
        
        if (application == null || !"OFFER_ACCEPTED".equals(application.getStatus())) {
            return "redirect:/onboarding/new-hires?error=Invalid application";
        }
        
        // Create a map with the candidate data for the template
        Map<String, Object> candidateData = new HashMap<>();
        candidateData.put("name", application.getFullName());
        candidateData.put("email", application.getEmail());
        candidateData.put("jobTitle", application.getJobTitle());
        
        model.addAttribute("application", application);
        model.addAttribute("candidateData", candidateData);
        model.addAttribute("applicationId", applicationId);
        model.addAttribute("templates", java.util.List.of(
            "Pre-Onboarding",
            "Day 1",
            "Week 1",
            "Month 1",
            "Custom"
        ));
        
        return "onboarding_create_checklist";
    }
    
    // Create onboarding checklist
    @PostMapping("/onboarding/create-checklist")
    public String createChecklist(@RequestParam String applicationId,
                                 @RequestParam String templateType,
                                 @RequestParam(required = false) String customTemplateName,
                                 Model model,
                                 HttpSession session) {
        Application application = applicationService.findById(applicationId);
        if (application == null) {
            return "redirect:/onboarding/new-hires?error=Application not found";
        }
        
        String userId = session.getAttribute("userId") != null ? session.getAttribute("userId").toString() : "system";
        
        // Create or get Onboarding record for this application
        Onboarding onboarding = onboardingService.getOnboardingByApplicationId(applicationId);
        if (onboarding == null) {
            onboarding = onboardingService.initializeOnboarding(application);
        }
        
        // Create new checklist
        OnboardingChecklist checklist = onboardingService.createChecklist(
            application.getId(),
            application.getUserId(),
            application.getFullName(),
            application.getEmail(),
            application.getJobTitle(),
            userId
        );
        
        String templateName = "Custom".equals(templateType) ? customTemplateName : templateType;
        checklist.setTemplateName(templateName);
        checklist.setTemplateType(templateType);
        checklist.setOverallStatus("In Progress");
        
        onboardingService.saveChecklist(checklist);
        
        model.addAttribute("checklist", checklist);
        model.addAttribute("application", application);
        
        return "redirect:/onboarding/edit-checklist/" + checklist.getId();
    }
    
    // Edit checklist - add tasks - Only HR/Onboarding can access
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN')")
    @GetMapping("/onboarding/edit-checklist/{checklistId}")
    public String editChecklist(@PathVariable String checklistId, Model model) {
        OnboardingChecklist checklist = onboardingService.getChecklistById(checklistId);
        
        if (checklist == null) {
            return "redirect:/onboarding/new-hires?error=Checklist not found";
        }
        
        model.addAttribute("checklist", checklist);
        java.util.List<String> phases = java.util.List.of(
            "Pre-Onboarding",
            "Day 1",
            "Week 1",
            "Month 1"
        );
        model.addAttribute("phases", phases);
        
        // Map phases to their tasks for template use
        java.util.Map<String, java.util.List<?>> phaseTasksMap = new java.util.HashMap<>();
        if (checklist.getTasks() != null) {
            for (String phase : phases) {
                phaseTasksMap.put(phase, checklist.getTasks().stream()
                    .filter(t -> t.getPhase() != null && t.getPhase().equals(phase))
                    .toList());
            }
        }
        model.addAttribute("phaseTasksMap", phaseTasksMap);
        
        model.addAttribute("roles", java.util.List.of(
            "IT Manager",
            "HR Manager",
            "Direct Manager",
            "Hiring Manager",
            "Team Lead",
            "Other"
        ));
        
        return "onboarding_edit_checklist";
    }
    
    // Add task to checklist
    @PostMapping("/onboarding/add-task/{checklistId}")
    public String addTaskToChecklist(@PathVariable String checklistId,
                                     @RequestParam String taskName,
                                     @RequestParam(required = false) String description,
                                     @RequestParam String phase,
                                     @RequestParam String assignedTo,
                                     @RequestParam(required = false) String dueDate) {
        
        OnboardingTask task = new OnboardingTask();
        task.setTaskName(taskName);
        task.setDescription(description);
        task.setPhase(phase);
        task.setAssignedTo(assignedTo);
        task.setStatus("Pending");
        
        onboardingService.addTaskToChecklist(checklistId, task);
        
        return "redirect:/onboarding/edit-checklist/" + checklistId;
    }
    
    // Update task status
    @PreAuthorize("hasAnyRole('ROLE_HR', 'ROLE_ONBOARDING', 'ROLE_ADMIN')")
    @PostMapping("/onboarding/update-task-status/{checklistId}")
    public String updateTaskStatus(@PathVariable String checklistId,
                                   @RequestParam int taskIndex,
                                   @RequestParam String newStatus) {
        onboardingService.updateTaskStatus(checklistId, taskIndex, newStatus);

        // After updating task status, if the checklist is now completed, propagate to onboarding training step
        try {
            OnboardingChecklist checklist = onboardingService.getChecklistById(checklistId);
            if (checklist != null && "Completed".equalsIgnoreCase(checklist.getOverallStatus())) {
                String applicationId = checklist.getApplicationId();
                Onboarding onboarding = onboardingService.getOnboardingByApplicationId(applicationId);
                if (onboarding != null) {
                    onboardingService.completeTraining(onboarding);
                }
            }
        } catch (Exception e) {
            System.err.println("Error propagating checklist completion to onboarding: " + e.getMessage());
        }

        return "redirect:/onboarding/edit-checklist/" + checklistId;
    }
    
    // View checklist details
    @GetMapping("/onboarding/checklist/{checklistId}")
    public String viewChecklistDetails(@PathVariable String checklistId, Model model) {
        OnboardingChecklist checklist = onboardingService.getChecklistById(checklistId);
        
        if (checklist == null) {
            return "redirect:/onboarding/new-hires?error=Checklist not found";
        }
        
        model.addAttribute("checklist", checklist);
        
        // Calculate task counts by status
        long completedCount = checklist.getTasks().stream().filter(t -> "Completed".equals(t.getStatus())).count();
        long inProgressCount = checklist.getTasks().stream().filter(t -> "In Progress".equals(t.getStatus())).count();
        long pendingCount = checklist.getTasks().stream().filter(t -> "Pending".equals(t.getStatus())).count();
        
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("pendingCount", pendingCount);
        
        // Add phases list
        model.addAttribute("phases", java.util.Arrays.asList("Pre-Onboarding", "Day 1", "Week 1", "Month 1"));
        
        return "onboarding_checklist_details";
    }
}

