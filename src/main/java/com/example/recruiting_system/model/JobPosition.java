package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "job_positions")
public class JobPosition {
    @Id
    private String id;
    private String title;
    private String description;
    private String department;
    private String location;
    private String hiringManagerId;
    private String assignedRecruiter; // username of assigned recruiter
    private String assignedHiringManager; // username of assigned HM
    private Date postedDate;
    private boolean published = false;
    private String status; // Open, Closed
    private List<String> skills = new ArrayList<>();
    private String salaryRange;
    private Integer openings = 1;
    private Date createdDate;
    private Date updatedDate;

    public JobPosition() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getHiringManagerId() { return hiringManagerId; }
    public void setHiringManagerId(String hiringManagerId) { this.hiringManagerId = hiringManagerId; }
    public String getAssignedRecruiter() { return assignedRecruiter; }
    public void setAssignedRecruiter(String assignedRecruiter) { this.assignedRecruiter = assignedRecruiter; }
    public String getAssignedHiringManager() { return assignedHiringManager; }
    public void setAssignedHiringManager(String assignedHiringManager) { this.assignedHiringManager = assignedHiringManager; }
    public Date getPostedDate() { return postedDate; }
    public void setPostedDate(Date postedDate) { this.postedDate = postedDate; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public Integer getOpenings() { return openings; }
    public void setOpenings(Integer openings) { this.openings = openings; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}
