package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "onboarding_checklists")
public class OnboardingChecklist {
    @Id
    private String id;
    private String applicationId;
    private String candidateId;
    private String candidateName;
    private String candidateEmail;
    private String jobTitle;
    private Date createdDate;
    private Date startDate;
    private String overallStatus; // Not Started, In Progress, Completed
    private int completionPercentage;
    
    // Checklist metadata
    private String templateType; // Custom or Pre-made
    private String templateName; // Pre-Onboarding, Day1, Week1, Month1, Custom
    
    // Tasks assigned to different roles
    private List<OnboardingTask> tasks; // All tasks for this checklist
    
    // Creator info
    private String createdBy; // Onboarding Coordinator username
    private Date updatedDate;
    private String updatedBy;
    
    public OnboardingChecklist() {
        this.tasks = new ArrayList<>();
        this.createdDate = new Date();
        this.overallStatus = "Not Started";
        this.completionPercentage = 0;
    }
    
    public OnboardingChecklist(String applicationId, String candidateId, String candidateName, 
                              String candidateEmail, String jobTitle) {
        this();
        this.applicationId = applicationId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.jobTitle = jobTitle;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    
    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    
    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    
    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
    
    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public List<OnboardingTask> getTasks() { return tasks; }
    public void setTasks(List<OnboardingTask> tasks) { this.tasks = tasks; }
    
    public void addTask(OnboardingTask task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    // Helper method to calculate completion percentage
    public void updateCompletionPercentage() {
        if (tasks == null || tasks.isEmpty()) {
            this.completionPercentage = 0;
            return;
        }
        
        long completedTasks = tasks.stream()
            .filter(task -> "Completed".equals(task.getStatus()))
            .count();
        
        this.completionPercentage = (int) ((completedTasks * 100) / tasks.size());
        
        // Update overall status
        if (this.completionPercentage == 0) {
            this.overallStatus = "Not Started";
        } else if (this.completionPercentage == 100) {
            this.overallStatus = "Completed";
        } else {
            this.overallStatus = "In Progress";
        }
    }
}
