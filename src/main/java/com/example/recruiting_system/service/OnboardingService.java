package com.example.recruiting_system.service;

import com.example.recruiting_system.model.Onboarding;
import com.example.recruiting_system.model.OnboardingDocument;
import com.example.recruiting_system.model.OnboardingChecklist;
import com.example.recruiting_system.model.OnboardingTask;
import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.repository.OnboardingRepository;
import com.example.recruiting_system.repository.OnboardingDocumentRepository;
import com.example.recruiting_system.repository.OnboardingChecklistRepository;
import com.example.recruiting_system.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OnboardingService {

    @Autowired
    private OnboardingDocumentRepository onboardingDocumentRepository;
    
    @Autowired
    private OnboardingRepository onboardingRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private OnboardingChecklistRepository onboardingChecklistRepository;

    // Onboarding Document Methods
    public OnboardingDocument submitDocument(OnboardingDocument doc) {
        doc.setCreatedDate(new Date());
        doc.setUpdatedDate(new Date());
        doc.setStatus("Submitted");
        return onboardingDocumentRepository.save(doc);
    }

    public Optional<OnboardingDocument> findDocumentById(String id) { 
        return onboardingDocumentRepository.findById(id); 
    }
    
    public List<OnboardingDocument> findDocumentByCandidate(String candidateId) { 
        return onboardingDocumentRepository.findByCandidateId(candidateId); 
    }
    
    public List<OnboardingDocument> findAllDocuments() { 
        return onboardingDocumentRepository.findAll(); 
    }
    
    public OnboardingDocument saveDocument(OnboardingDocument doc) { 
        doc.setUpdatedDate(new Date()); 
        return onboardingDocumentRepository.save(doc); 
    }
    
    // Legacy method support
    public OnboardingDocument submit(OnboardingDocument doc) {
        return submitDocument(doc);
    }
    
    public Optional<OnboardingDocument> findById(String id) {
        return findDocumentById(id);
    }
    
    public List<OnboardingDocument> findByCandidate(String candidateId) {
        return findDocumentByCandidate(candidateId);
    }
    
    public List<OnboardingDocument> findAll() {
        return findAllDocuments();
    }
    
    public OnboardingDocument save(OnboardingDocument doc) {
        return saveDocument(doc);
    }
    
    // Onboarding Process Methods
    public Onboarding initializeOnboarding(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null");
        }
        Onboarding onboarding = new Onboarding();
        onboarding.setApplicationId(application.getId());
        onboarding.setCandidateId(application.getUserId());
        onboarding.setCandidateName(application.getFullName());
        onboarding.setCandidateEmail(application.getEmail());
        onboarding.setJobTitle(application.getJobTitle());
        onboarding.setStartDate(new Date());
        onboarding.setOverallStatus("In Progress");
        onboarding.setStep1Status("Pending");
        onboarding.setStep2Status("Pending");
        onboarding.setStep3Status("Pending");
        onboarding.setStep4Status("Pending");
        onboarding.setStep5Status("Pending");
        onboarding.setStep6Status("Pending");
        onboarding.setCreatedDate(new Date());
        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    public Onboarding getOnboardingByApplicationId(String applicationId) {
        return onboardingRepository.findByApplicationId(applicationId).orElse(null);
    }
    
    public Onboarding getOnboardingByCandidateId(String candidateId) {
        return onboardingRepository.findByCandidateId(candidateId).orElse(null);
    }
    
    // Step 1 - Personal Info
    public Onboarding savePersonalInfo(Onboarding onboarding) {
        onboarding.setStep1Status("Completed");
        onboarding.setStep1Completed(true);
        onboarding.setStep1CompletedDate(new Date());
        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    // Step 2 - Documents
    public Onboarding completeDocumentStep(Onboarding onboarding) {
        onboarding.setStep2Status("Completed");
        onboarding.setStep2Completed(true);
        onboarding.setStep2CompletedDate(new Date());
        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    // Step 3 - Workstation Setup
    public Onboarding saveWorkstationSetup(Onboarding onboarding) {
        onboarding.setStep3Status("Completed");
        onboarding.setStep3Completed(true);
        onboarding.setStep3CompletedDate(new Date());
        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    // Step 4 - Orientation
    public Onboarding scheduleOrientation(Onboarding onboarding) {
        onboarding.setStep4Status("Completed");
        onboarding.setStep4Completed(true);
        onboarding.setStep4CompletedDate(new Date());
        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    // Step 5 - Training
    public Onboarding completeTraining(Onboarding onboarding) {
        onboarding.setStep5Status("Completed");
        onboarding.setStep5Completed(true);
        onboarding.setStep5CompletedDate(new Date());
        // Also mark basic induction and role-based training as completed to match UI checks
        onboarding.setBasicInductionCompleted(true);
        onboarding.setBasicInductionDate(new Date());
        onboarding.setRoleBasedTrainingCompleted(true);
        onboarding.setRoleBasedTrainingDate(new Date());
        onboarding.setTrainingCompletionStatus("Completed");
        // Determine if overall onboarding should be marked completed
        boolean stepsComplete = onboarding.isStep1Completed() && onboarding.isStep2Completed()
                && onboarding.isStep3Completed() && onboarding.isStep4Completed() && onboarding.isStep5Completed();

        // Check for a checklist with 100% completion as an alternative indicator
        com.example.recruiting_system.model.OnboardingChecklist checklist = null;
        try {
            checklist = getChecklistByApplicationId(onboarding.getApplicationId());
        } catch (Exception e) {
            // ignore - checklist may not exist
        }
        boolean checklistComplete = (checklist != null && checklist.getCompletionPercentage() == 100);

        if (stepsComplete || checklistComplete) {
            onboarding.setOverallStatus("Completed");
            onboarding.setAllStepsCompleted(true);
            // Also update application status if linked
            try {
                Optional<Application> appOpt = applicationRepository.findById(onboarding.getApplicationId());
                if (appOpt.isPresent()) {
                    Application app = appOpt.get();
                    app.setOnboardingStatus("Completed");
                    applicationRepository.save(app);
                }
            } catch (Exception ignored) {}
        } else {
            onboarding.setOverallStatus("In Progress");
        }

        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    // Step 6 - Final Completion
    public Onboarding completeOnboarding(Onboarding onboarding) {
        onboarding.setStep6Status("Completed");
        onboarding.setStep6Completed(true);
        onboarding.setAllStepsCompleted(true);
        onboarding.setOverallStatus("Completed");
        onboarding.setStep6CompletedDate(new Date());
        onboarding.setUpdatedDate(new Date());
        
        // Update application status
        Optional<Application> appOpt = applicationRepository.findById(onboarding.getApplicationId());
        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            app.setOnboardingStatus("Completed");
            applicationRepository.save(app);
        }
        
        return onboardingRepository.save(onboarding);
    }
    
    public List<Onboarding> findAllOnboarding() {
        return onboardingRepository.findAll();
    }
    
    public List<Onboarding> findOnboardingByStatus(String status) {
        return onboardingRepository.findByOverallStatus(status);
    }
    
    public List<Onboarding> findPendingDocumentVerification() {
        return onboardingRepository.findByStep2VerificationStatus("Pending");
    }
    
    public Onboarding saveOnboarding(Onboarding onboarding) {
        onboarding.setUpdatedDate(new Date());
        return onboardingRepository.save(onboarding);
    }
    
    // ============ ONBOARDING CHECKLIST METHODS ============
    
    public OnboardingChecklist createChecklist(String applicationId, String candidateId, 
                                               String candidateName, String candidateEmail, 
                                               String jobTitle, String createdBy) {
        OnboardingChecklist checklist = new OnboardingChecklist(applicationId, candidateId, 
                                                               candidateName, candidateEmail, jobTitle);
        checklist.setCreatedBy(createdBy);
        checklist.setCreatedDate(new Date());
        checklist.setUpdatedDate(new Date());
        return onboardingChecklistRepository.save(checklist);
    }
    
    public OnboardingChecklist getChecklistByApplicationId(String applicationId) {
        // Handle multiple checklists - return the most recent one
        try {
            return onboardingChecklistRepository.findByApplicationId(applicationId).orElse(null);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            // Multiple checklists exist - get all and return the most recent
            List<OnboardingChecklist> checklists = onboardingChecklistRepository.findAll().stream()
                .filter(c -> applicationId.equals(c.getApplicationId()))
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
                .limit(1)
                .toList();
            return checklists.isEmpty() ? null : checklists.get(0);
        }
    }
    
    public OnboardingChecklist getChecklistById(String checklistId) {
        return onboardingChecklistRepository.findById(checklistId).orElse(null);
    }
    
    public List<OnboardingChecklist> getAllChecklists() {
        return onboardingChecklistRepository.findAll();
    }
    
    public List<OnboardingChecklist> getChecklistsByStatus(String status) {
        return onboardingChecklistRepository.findByOverallStatus(status);
    }
    
    public OnboardingChecklist addTaskToChecklist(String checklistId, OnboardingTask task) {
        OnboardingChecklist checklist = onboardingChecklistRepository.findById(checklistId).orElse(null);
        if (checklist != null) {
            checklist.addTask(task);
            checklist.setUpdatedDate(new Date());
            checklist.updateCompletionPercentage();
            OnboardingChecklist saved = onboardingChecklistRepository.save(checklist);
            // If checklist reached 100%, mark linked onboarding/application as completed
            if (saved.getCompletionPercentage() == 100) {
                try {
                    Onboarding onboarding = onboardingRepository.findByApplicationId(saved.getApplicationId()).orElse(null);
                    if (onboarding != null) {
                        onboarding.setOverallStatus("Completed");
                        onboarding.setAllStepsCompleted(true);
                        onboarding.setUpdatedDate(new Date());
                        onboardingRepository.save(onboarding);

                        Optional<Application> appOpt = applicationRepository.findById(onboarding.getApplicationId());
                        if (appOpt.isPresent()) {
                            Application app = appOpt.get();
                            app.setOnboardingStatus("Completed");
                            applicationRepository.save(app);
                        }
                    }
                } catch (Exception ignored) {}
            }
            return saved;
        }
        return null;
    }
    
    public OnboardingChecklist updateTaskStatus(String checklistId, int taskIndex, String newStatus) {
        OnboardingChecklist checklist = onboardingChecklistRepository.findById(checklistId).orElse(null);
        if (checklist != null && taskIndex >= 0 && taskIndex < checklist.getTasks().size()) {
            OnboardingTask task = checklist.getTasks().get(taskIndex);
            task.setStatus(newStatus);
            if ("Completed".equals(newStatus)) {
                task.setCompletedDate(new Date());
            }
            checklist.setUpdatedDate(new Date());
            checklist.updateCompletionPercentage();
            OnboardingChecklist saved = onboardingChecklistRepository.save(checklist);
            if (saved.getCompletionPercentage() == 100) {
                try {
                    Onboarding onboarding = onboardingRepository.findByApplicationId(saved.getApplicationId()).orElse(null);
                    if (onboarding != null) {
                        onboarding.setOverallStatus("Completed");
                        onboarding.setAllStepsCompleted(true);
                        onboarding.setUpdatedDate(new Date());
                        onboardingRepository.save(onboarding);

                        Optional<Application> appOpt = applicationRepository.findById(onboarding.getApplicationId());
                        if (appOpt.isPresent()) {
                            Application app = appOpt.get();
                            app.setOnboardingStatus("Completed");
                            applicationRepository.save(app);
                        }
                    }
                } catch (Exception ignored) {}
            }
            return saved;
        }
        return null;
    }
    
    public OnboardingChecklist saveChecklist(OnboardingChecklist checklist) {
        checklist.setUpdatedDate(new Date());
        checklist.updateCompletionPercentage();
        OnboardingChecklist saved = onboardingChecklistRepository.save(checklist);
        if (saved.getCompletionPercentage() == 100) {
            try {
                Onboarding onboarding = onboardingRepository.findByApplicationId(saved.getApplicationId()).orElse(null);
                if (onboarding != null) {
                    onboarding.setOverallStatus("Completed");
                    onboarding.setAllStepsCompleted(true);
                    onboarding.setUpdatedDate(new Date());
                    onboardingRepository.save(onboarding);

                    Optional<Application> appOpt = applicationRepository.findById(onboarding.getApplicationId());
                    if (appOpt.isPresent()) {
                        Application app = appOpt.get();
                        app.setOnboardingStatus("Completed");
                        applicationRepository.save(app);
                    }
                }
            } catch (Exception ignored) {}
        }
        return saved;
    }
    
    // Wrapper methods for manager checklist access
    public List<OnboardingChecklist> findAllChecklists() {
        return getAllChecklists();
    }
    
    public Optional<OnboardingChecklist> findChecklistById(String checklistId) {
        return onboardingChecklistRepository.findById(checklistId);
    }
}
