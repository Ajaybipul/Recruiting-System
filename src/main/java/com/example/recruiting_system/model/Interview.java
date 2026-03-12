package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "interviews")
public class Interview {
    @Id
    private String id;
    private String candidateId;
    private String jobPositionId;
    private List<String> interviewerIds = new ArrayList<>();
    private Date scheduledAt;
    private Integer durationMinutes;
    private String location;
    private String mode; // In-person, Virtual, Phone, Video
    private String meetingLink; // Zoom/Teams/Video call link
    private String interviewNotes; // Notes/instructions for candidate
    private String status; // Scheduled, Confirmed, Completed, Canceled
    private Map<String, String> feedback; // interviewerId -> feedback text
    private String hmRemarks; // Remarks from Hiring Manager
    private Date createdDate;
    private Date updatedDate;

    public Interview() {}

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public String getJobPositionId() { return jobPositionId; }
    public void setJobPositionId(String jobPositionId) { this.jobPositionId = jobPositionId; }
    public List<String> getInterviewerIds() { return interviewerIds; }
    public void setInterviewerIds(List<String> interviewerIds) { this.interviewerIds = interviewerIds; }
    public Date getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Date scheduledAt) { this.scheduledAt = scheduledAt; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    public String getInterviewNotes() { return interviewNotes; }
    public void setInterviewNotes(String interviewNotes) { this.interviewNotes = interviewNotes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, String> getFeedback() { return feedback; }
    public void setFeedback(Map<String, String> feedback) { this.feedback = feedback; }
    public String getHmRemarks() { return hmRemarks; }
    public void setHmRemarks(String hmRemarks) { this.hmRemarks = hmRemarks; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}
