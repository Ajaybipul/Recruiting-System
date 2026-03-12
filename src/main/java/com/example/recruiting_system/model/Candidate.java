package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "candidates")
public class Candidate {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String resumeUrl;
    private String source;
    private String appliedPositionId;
    private Date appliedDate;
    private String recruiterId;
    private String status; // e.g., New, Screened, Interviewing, Offered, Hired, Rejected
    private List<String> interviewIds = new ArrayList<>();
    private String notes;
    private Boolean phoneScreenCompleted = false;
    private String phoneScreenNotes;
    private Date createdDate;
    private Date updatedDate;

    public Candidate() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getAppliedPositionId() { return appliedPositionId; }
    public void setAppliedPositionId(String appliedPositionId) { this.appliedPositionId = appliedPositionId; }
    public Date getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Date appliedDate) { this.appliedDate = appliedDate; }
    public String getRecruiterId() { return recruiterId; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getInterviewIds() { return interviewIds; }
    public void setInterviewIds(List<String> interviewIds) { this.interviewIds = interviewIds; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Boolean getPhoneScreenCompleted() { return phoneScreenCompleted; }
    public void setPhoneScreenCompleted(Boolean phoneScreenCompleted) { this.phoneScreenCompleted = phoneScreenCompleted; }
    public String getPhoneScreenNotes() { return phoneScreenNotes; }
    public void setPhoneScreenNotes(String phoneScreenNotes) { this.phoneScreenNotes = phoneScreenNotes; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}
