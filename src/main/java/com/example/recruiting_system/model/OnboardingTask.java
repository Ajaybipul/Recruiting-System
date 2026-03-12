package com.example.recruiting_system.model;

import java.util.Date;

public class OnboardingTask {
    private String id;
    private String taskName;
    private String description;
    private String phase; // Pre-Onboarding, Day 1, Week 1, Month 1
    private String assignedTo; // Role or person (IT Manager, HR Manager, Direct Manager, etc.)
    private String assignedToEmail;
    private String status; // Pending, In Progress, Completed
    private Date dueDate;
    private Date completedDate;
    private String notes;
    private int priority; // 1-5, where 5 is highest
    
    public OnboardingTask() {
        this.status = "Pending";
        this.priority = 3;
    }
    
    public OnboardingTask(String taskName, String description, String phase, 
                         String assignedTo, Date dueDate) {
        this();
        this.taskName = taskName;
        this.description = description;
        this.phase = phase;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public String getAssignedToEmail() { return assignedToEmail; }
    public void setAssignedToEmail(String assignedToEmail) { this.assignedToEmail = assignedToEmail; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
