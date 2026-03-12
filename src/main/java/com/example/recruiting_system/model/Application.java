package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Document(collection = "applications")
public class Application {
    @Id
    private String id;
    @Field("userId")
    private String userId; 
    @Field("jobId")
    private String jobId;
    @Field("jobTitle")
    private String jobTitle;
    @Field("applicantUsername")
    private String applicantUsername;
    @Field("fullName")
    private String fullName;
    @Field("email")
    private String email;
    @Field("phone")
    private String phone;
    @Field("resumePath")
    private String resumePath; // stored file path
    @Field("experience")
    private String experience;
    @Field("skills")
    private String skills;
    @Field("status")
    private String status; // Submitted, Under Review, Shortlisted, Interview Scheduled, Selected, Rejected
    @Field("screeningNotes")
    private String screeningNotes;
    @Field("interviewDate")
    private LocalDate interviewDate;
    @Field("interviewTime")
    private LocalTime interviewTime;
    @Field("interviewMode")
    private String interviewMode; // In-person, Virtual, Phone, Video
    @Field("interviewLocation")
    private String interviewLocation;
    @Field("interviewMeetingLink")
    private String interviewMeetingLink; // Zoom/Teams link for virtual interviews
    @Field("interviewNotes")
    private String interviewNotes; // Notes for candidate about interview
    @Field("forwardedToHiringManager")
    private boolean forwardedToHiringManager;
    @Field("appliedDate")
    private Date appliedDate;

    // Interview / Feedback fields
    @Field("interviewId")
    private String interviewId; // link to Interview document
    @Field("technicalScore")
    private Integer technicalScore;
    @Field("communicationScore")
    private Integer communicationScore;
    @Field("cultureFitScore")
    private Integer cultureFitScore;
    @Field("interviewerNotes")
    private String interviewerNotes;
    @Field("recommendation")
    private String recommendation; // e.g., Hire / No Hire / Hold

    // Offer fields
    @Field("offerId")
    private String offerId;
    @Field("offerStatus")
    private String offerStatus; // Offer Created, Sent, Accepted, Rejected
    @Field("offerPdfPath")
    private String offerPdfPath;
    @Field("offerCreatedDate")
    private Date offerCreatedDate;
    private transient Offer offer;  // Transient for template access

    // Onboarding
    @Field("onboardingStatus")
    private String onboardingStatus; // Not Started, In Progress, Completed
    @Field("onboardingDocumentPaths")
    private java.util.List<String> onboardingDocumentPaths;
    @Field("onboardingVerificationStatus")
    private String onboardingVerificationStatus; // Pending, Verified, Rejected

    // Hiring Manager fields
    @Field("hmFeedback")
    private String hmFeedback; // HM comments/feedback
    @Field("hmRating")
    private Integer hmRating; // 1-5 star rating from HM
    @Field("hmNotes")
    private String hmNotes; // HM internal notes
    @Field("hmDecision")
    private String hmDecision; // Approved, Rejected, On Hold
    @Field("technicalSkillsAssessment")
    private String technicalSkillsAssessment; // Good / Average / Poor
    @Field("communicationAssessment")
    private String communicationAssessment; // Good / Average / Poor
    @Field("problemSolvingAssessment")
    private String problemSolvingAssessment; // Good / Average / Poor
    @Field("culturalFitAssessment")
    private String culturalFitAssessment; // Yes / No

    // Background Check & Verification fields
    @Field("backgroundCheckStatus")
    private String backgroundCheckStatus; // Not Started, In Progress, Completed, Approved, Failed
    @Field("backgroundCheckInitiatedDate")
    private Date backgroundCheckInitiatedDate;
    @Field("backgroundCheckCompletedDate")
    private Date backgroundCheckCompletedDate;
    @Field("backgroundCheckNotes")
    private String backgroundCheckNotes;
    @Field("backgroundCheckPassed")
    private boolean backgroundCheckPassed;
    @Field("referenceCheckStatus")
    private String referenceCheckStatus; // Not Started, In Progress, Completed, Approved
    @Field("referenceCheckDate")
    private Date referenceCheckDate;
    @Field("educationVerificationStatus")
    private String educationVerificationStatus; // Not Started, Verified, Failed
    @Field("employmentVerificationStatus")
    private String employmentVerificationStatus; // Not Started, Verified, Failed
    @Field("criminalCheckStatus")
    private String criminalCheckStatus; // Not Started, Clear, Issues
    @Field("criminalCheckNotes")
    private String criminalCheckNotes;

    // HR Decision fields (post-interview, post-offer)
    @Field("hrFinalDecision")
    private String hrFinalDecision; // Hired, Offer Declined, Withdrawn
    @Field("hrDecisionNotes")
    private String hrDecisionNotes; // HR notes on final decision
    @Field("hrDecisionDate")
    private Date hrDecisionDate; // When HR made the final decision
    @Field("employeeId")
    private String employeeId; // Link to Employee record once hired
    @Field("offerAcceptedDate")
    private Date offerAcceptedDate; // When candidate accepted the offer
    @Field("joiningDate")
    private Date joiningDate; // Expected joining date for new hire

    public Application() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getApplicantUsername() { return applicantUsername; }
    public void setApplicantUsername(String applicantUsername) { this.applicantUsername = applicantUsername; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getScreeningNotes() { return screeningNotes; }
    public void setScreeningNotes(String screeningNotes) { this.screeningNotes = screeningNotes; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }
    public LocalTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalTime interviewTime) { this.interviewTime = interviewTime; }
    public String getInterviewMode() { return interviewMode; }
    public void setInterviewMode(String interviewMode) { this.interviewMode = interviewMode; }
    public String getInterviewLocation() { return interviewLocation; }
    public void setInterviewLocation(String interviewLocation) { this.interviewLocation = interviewLocation; }
    public String getInterviewMeetingLink() { return interviewMeetingLink; }
    public void setInterviewMeetingLink(String interviewMeetingLink) { this.interviewMeetingLink = interviewMeetingLink; }
    public String getInterviewNotes() { return interviewNotes; }
    public void setInterviewNotes(String interviewNotes) { this.interviewNotes = interviewNotes; }
    public boolean isForwardedToHiringManager() { return forwardedToHiringManager; }
    public void setForwardedToHiringManager(boolean forwardedToHiringManager) { this.forwardedToHiringManager = forwardedToHiringManager; }
    public Date getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Date appliedDate) { this.appliedDate = appliedDate; }

    public String getInterviewId() { return interviewId; }
    public void setInterviewId(String interviewId) { this.interviewId = interviewId; }
    public Integer getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }
    public Integer getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }
    public Integer getCultureFitScore() { return cultureFitScore; }
    public void setCultureFitScore(Integer cultureFitScore) { this.cultureFitScore = cultureFitScore; }
    public String getInterviewerNotes() { return interviewerNotes; }
    public void setInterviewerNotes(String interviewerNotes) { this.interviewerNotes = interviewerNotes; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }
    public String getOfferStatus() { return offerStatus; }
    public void setOfferStatus(String offerStatus) { this.offerStatus = offerStatus; }
    public String getOfferPdfPath() { return offerPdfPath; }
    public void setOfferPdfPath(String offerPdfPath) { this.offerPdfPath = offerPdfPath; }
    public Date getOfferCreatedDate() { return offerCreatedDate; }
    public void setOfferCreatedDate(Date offerCreatedDate) { this.offerCreatedDate = offerCreatedDate; }
    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }

    public String getOnboardingStatus() { return onboardingStatus; }
    public void setOnboardingStatus(String onboardingStatus) { this.onboardingStatus = onboardingStatus; }
    public java.util.List<String> getOnboardingDocumentPaths() { return onboardingDocumentPaths; }
    public void setOnboardingDocumentPaths(java.util.List<String> onboardingDocumentPaths) { this.onboardingDocumentPaths = onboardingDocumentPaths; }
    public String getOnboardingVerificationStatus() { return onboardingVerificationStatus; }
    public void setOnboardingVerificationStatus(String onboardingVerificationStatus) { this.onboardingVerificationStatus = onboardingVerificationStatus; }

    public String getHmFeedback() { return hmFeedback; }
    public void setHmFeedback(String hmFeedback) { this.hmFeedback = hmFeedback; }
    public Integer getHmRating() { return hmRating; }
    public void setHmRating(Integer hmRating) { this.hmRating = hmRating; }
    public String getHmNotes() { return hmNotes; }
    public void setHmNotes(String hmNotes) { this.hmNotes = hmNotes; }
    public String getHmDecision() { return hmDecision; }
    public void setHmDecision(String hmDecision) { this.hmDecision = hmDecision; }
    public String getTechnicalSkillsAssessment() { return technicalSkillsAssessment; }
    public void setTechnicalSkillsAssessment(String technicalSkillsAssessment) { this.technicalSkillsAssessment = technicalSkillsAssessment; }
    public String getCommunicationAssessment() { return communicationAssessment; }
    public void setCommunicationAssessment(String communicationAssessment) { this.communicationAssessment = communicationAssessment; }
    public String getProblemSolvingAssessment() { return problemSolvingAssessment; }
    public void setProblemSolvingAssessment(String problemSolvingAssessment) { this.problemSolvingAssessment = problemSolvingAssessment; }
    public String getCulturalFitAssessment() { return culturalFitAssessment; }
    public void setCulturalFitAssessment(String culturalFitAssessment) { this.culturalFitAssessment = culturalFitAssessment; }

    // Background Check & Verification getters/setters
    public String getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(String backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }
    public Date getBackgroundCheckInitiatedDate() { return backgroundCheckInitiatedDate; }
    public void setBackgroundCheckInitiatedDate(Date backgroundCheckInitiatedDate) { this.backgroundCheckInitiatedDate = backgroundCheckInitiatedDate; }
    public Date getBackgroundCheckCompletedDate() { return backgroundCheckCompletedDate; }
    public void setBackgroundCheckCompletedDate(Date backgroundCheckCompletedDate) { this.backgroundCheckCompletedDate = backgroundCheckCompletedDate; }
    public String getBackgroundCheckNotes() { return backgroundCheckNotes; }
    public void setBackgroundCheckNotes(String backgroundCheckNotes) { this.backgroundCheckNotes = backgroundCheckNotes; }
    public boolean isBackgroundCheckPassed() { return backgroundCheckPassed; }
    public void setBackgroundCheckPassed(boolean backgroundCheckPassed) { this.backgroundCheckPassed = backgroundCheckPassed; }
    
    public String getReferenceCheckStatus() { return referenceCheckStatus; }
    public void setReferenceCheckStatus(String referenceCheckStatus) { this.referenceCheckStatus = referenceCheckStatus; }
    public Date getReferenceCheckDate() { return referenceCheckDate; }
    public void setReferenceCheckDate(Date referenceCheckDate) { this.referenceCheckDate = referenceCheckDate; }
    
    public String getEducationVerificationStatus() { return educationVerificationStatus; }
    public void setEducationVerificationStatus(String educationVerificationStatus) { this.educationVerificationStatus = educationVerificationStatus; }
    public String getEmploymentVerificationStatus() { return employmentVerificationStatus; }
    public void setEmploymentVerificationStatus(String employmentVerificationStatus) { this.employmentVerificationStatus = employmentVerificationStatus; }
    
    public String getCriminalCheckStatus() { return criminalCheckStatus; }
    public void setCriminalCheckStatus(String criminalCheckStatus) { this.criminalCheckStatus = criminalCheckStatus; }
    public String getCriminalCheckNotes() { return criminalCheckNotes; }
    public void setCriminalCheckNotes(String criminalCheckNotes) { this.criminalCheckNotes = criminalCheckNotes; }

    // HR Decision getters/setters
    public String getHrFinalDecision() { return hrFinalDecision; }
    public void setHrFinalDecision(String hrFinalDecision) { this.hrFinalDecision = hrFinalDecision; }
    public String getHrDecisionNotes() { return hrDecisionNotes; }
    public void setHrDecisionNotes(String hrDecisionNotes) { this.hrDecisionNotes = hrDecisionNotes; }
    public Date getHrDecisionDate() { return hrDecisionDate; }
    public void setHrDecisionDate(Date hrDecisionDate) { this.hrDecisionDate = hrDecisionDate; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public Date getOfferAcceptedDate() { return offerAcceptedDate; }
    public void setOfferAcceptedDate(Date offerAcceptedDate) { this.offerAcceptedDate = offerAcceptedDate; }
    public Date getJoiningDate() { return joiningDate; }
    public void setJoiningDate(Date joiningDate) { this.joiningDate = joiningDate; }

    // DEBUG: toString method
    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", interviewDate=" + interviewDate +
                ", hmFeedback='" + hmFeedback + '\'' +
                '}';
    }
}
