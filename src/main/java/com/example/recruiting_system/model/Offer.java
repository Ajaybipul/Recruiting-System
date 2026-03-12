package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    private String applicationId;  // Link to Application
    private String candidateId;
    private String candidateName;
    private String candidateEmail;
    private String jobPositionId;
    private String recruiterId;
    private String position;
    private String positionTitle;  // NEW: Full position title
    private String department;
    private Double salary;
    private String notes;
    private Date offerDate;
    private String benefits;
    private String status; // Draft, Sent, Accepted, Declined, Approved, Rejected
    private String offerType; // DRAFT (from recruiter) or FINAL (from HR)
    private Date expirationDate;
    private Date createdDate;
    private Date updatedDate;
    private Date sentDate;
    private String offerPdfPath;
    private String createdBy; // recruiter or hr
    private String approvedBy; // hr name/id if approved
    private Date approvedDate;
    private String approvalNotes;
    
    // NEW FIELDS from workflow guide
    private String reportTo;  // Manager name
    private String employmentTerms;  // Full-time, At-will, etc.
    private Integer paidTimeOff;  // Days
    private Double signOnBonus;  // Optional amount
    private Boolean healthInsurance;
    private Boolean fourOhOneK;
    private Date startDate;
    private String additionalNotes;

    public Offer() {}

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
    public String getJobPositionId() { return jobPositionId; }
    public void setJobPositionId(String jobPositionId) { this.jobPositionId = jobPositionId; }
    public String getRecruiterId() { return recruiterId; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Date getOfferDate() { return offerDate; }
    public void setOfferDate(Date offerDate) { this.offerDate = offerDate; }
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }
    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    public Date getSentDate() { return sentDate; }
    public void setSentDate(Date sentDate) { this.sentDate = sentDate; }
    public String getOfferPdfPath() { return offerPdfPath; }
    public void setOfferPdfPath(String offerPdfPath) { this.offerPdfPath = offerPdfPath; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public Date getApprovedDate() { return approvedDate; }
    public void setApprovedDate(Date approvedDate) { this.approvedDate = approvedDate; }
    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }
    
    // Getters and setters for new fields
    public String getReportTo() { return reportTo; }
    public void setReportTo(String reportTo) { this.reportTo = reportTo; }
    public String getEmploymentTerms() { return employmentTerms; }
    public void setEmploymentTerms(String employmentTerms) { this.employmentTerms = employmentTerms; }
    public Integer getPaidTimeOff() { return paidTimeOff; }
    public void setPaidTimeOff(Integer paidTimeOff) { this.paidTimeOff = paidTimeOff; }
    public Double getSignOnBonus() { return signOnBonus; }
    public void setSignOnBonus(Double signOnBonus) { this.signOnBonus = signOnBonus; }
    public Boolean getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(Boolean healthInsurance) { this.healthInsurance = healthInsurance; }
    public Boolean getFourOhOneK() { return fourOhOneK; }
    public void setFourOhOneK(Boolean fourOhOneK) { this.fourOhOneK = fourOhOneK; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
}
