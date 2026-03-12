package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "onboarding")
public class Onboarding {
    @Id
    private String id;
    private String applicationId;
    private String candidateId;
    private String candidateName;
    private String candidateEmail;
    private String jobTitle;
    private Date startDate;
    private Date targetCompletionDate;
    private String overallStatus; // Not Started, In Progress, Completed
    
    // Step 1: Personal Info Collection
    private boolean step1Completed;
    private String step1Status; // Pending, In Progress, Completed
    private String firstName;
    private String lastName;
    private String address;
    private String dateOfBirth;
    private String emergencyContact;
    private String bankDetails;
    private Date step1CompletedDate;
    
    // Step 2: Document Upload
    private boolean step2Completed;
    private String step2Status; // Pending, In Progress, Completed
    private boolean idProofUploaded;
    private boolean addressProofUploaded;
    private boolean certificatesUploaded;
    private boolean offerLetterUploaded;
    private String step2VerificationStatus; // Pending, Verified, Rejected
    private String step2Notes;
    private Date step2CompletedDate;
    
    // Step 3: Workstation Setup
    private boolean step3Completed;
    private String step3Status; // Pending, In Progress, Completed
    private boolean laptopAssigned;
    private String laptopModel;
    private boolean emailCreated;
    private String emailId;
    private boolean accessPermissionsGranted;
    private boolean departmentToolsSetup;
    private String step3Notes;
    private Date step3CompletedDate;
    
    // Step 4: Orientation Scheduling
    private boolean step4Completed;
    private String step4Status; // Pending, In Progress, Completed
    private Date orientationDate;
    private String orientationTime;
    private String orientationMode; // In-person, Virtual
    private String meetingLink;
    private String orientationLocation;
    private String assignedTrainer;
    private Date step4CompletedDate;
    
    // Step 5: Training Tracking
    private boolean step5Completed;
    private String step5Status; // Pending, In Progress, Completed
    private boolean basicInductionCompleted;
    private Date basicInductionDate;
    private boolean roleBasedTrainingCompleted;
    private Date roleBasedTrainingDate;
    private String trainingCompletionStatus; // Not Started, In Progress, Completed
    private String step5Notes;
    private Date step5CompletedDate;
    
    // Step 6: Final Onboarding Completion
    private boolean step6Completed;
    private String step6Status; // Pending, In Progress, Completed
    private boolean allStepsCompleted;
    private String hrNotificationStatus; // Not Sent, Sent
    private String hiringManagerNotificationStatus; // Not Sent, Sent
    private boolean welcomeEmailSent;
    private Date step6CompletedDate;
    
    // Metadata
    private Date createdDate;
    private Date updatedDate;
    private String createdBy;
    private String updatedBy;
    
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
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(Date targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }
    
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    
    // Step 1
    public boolean isStep1Completed() { return step1Completed; }
    public void setStep1Completed(boolean step1Completed) { this.step1Completed = step1Completed; }
    
    public String getStep1Status() { return step1Status; }
    public void setStep1Status(String step1Status) { this.step1Status = step1Status; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getBankDetails() { return bankDetails; }
    public void setBankDetails(String bankDetails) { this.bankDetails = bankDetails; }
    
    public Date getStep1CompletedDate() { return step1CompletedDate; }
    public void setStep1CompletedDate(Date step1CompletedDate) { this.step1CompletedDate = step1CompletedDate; }
    
    // Step 2
    public boolean isStep2Completed() { return step2Completed; }
    public void setStep2Completed(boolean step2Completed) { this.step2Completed = step2Completed; }
    
    public String getStep2Status() { return step2Status; }
    public void setStep2Status(String step2Status) { this.step2Status = step2Status; }
    
    public boolean isIdProofUploaded() { return idProofUploaded; }
    public void setIdProofUploaded(boolean idProofUploaded) { this.idProofUploaded = idProofUploaded; }
    
    public boolean isAddressProofUploaded() { return addressProofUploaded; }
    public void setAddressProofUploaded(boolean addressProofUploaded) { this.addressProofUploaded = addressProofUploaded; }
    
    public boolean isCertificatesUploaded() { return certificatesUploaded; }
    public void setCertificatesUploaded(boolean certificatesUploaded) { this.certificatesUploaded = certificatesUploaded; }
    
    public boolean isOfferLetterUploaded() { return offerLetterUploaded; }
    public void setOfferLetterUploaded(boolean offerLetterUploaded) { this.offerLetterUploaded = offerLetterUploaded; }
    
    public String getStep2VerificationStatus() { return step2VerificationStatus; }
    public void setStep2VerificationStatus(String step2VerificationStatus) { this.step2VerificationStatus = step2VerificationStatus; }
    
    public String getStep2Notes() { return step2Notes; }
    public void setStep2Notes(String step2Notes) { this.step2Notes = step2Notes; }
    
    public Date getStep2CompletedDate() { return step2CompletedDate; }
    public void setStep2CompletedDate(Date step2CompletedDate) { this.step2CompletedDate = step2CompletedDate; }
    
    // Step 3
    public boolean isStep3Completed() { return step3Completed; }
    public void setStep3Completed(boolean step3Completed) { this.step3Completed = step3Completed; }
    
    public String getStep3Status() { return step3Status; }
    public void setStep3Status(String step3Status) { this.step3Status = step3Status; }
    
    public boolean isLaptopAssigned() { return laptopAssigned; }
    public void setLaptopAssigned(boolean laptopAssigned) { this.laptopAssigned = laptopAssigned; }
    
    public String getLaptopModel() { return laptopModel; }
    public void setLaptopModel(String laptopModel) { this.laptopModel = laptopModel; }
    
    public boolean isEmailCreated() { return emailCreated; }
    public void setEmailCreated(boolean emailCreated) { this.emailCreated = emailCreated; }
    
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    
    public boolean isAccessPermissionsGranted() { return accessPermissionsGranted; }
    public void setAccessPermissionsGranted(boolean accessPermissionsGranted) { this.accessPermissionsGranted = accessPermissionsGranted; }
    
    public boolean isDepartmentToolsSetup() { return departmentToolsSetup; }
    public void setDepartmentToolsSetup(boolean departmentToolsSetup) { this.departmentToolsSetup = departmentToolsSetup; }
    
    public String getStep3Notes() { return step3Notes; }
    public void setStep3Notes(String step3Notes) { this.step3Notes = step3Notes; }
    
    public Date getStep3CompletedDate() { return step3CompletedDate; }
    public void setStep3CompletedDate(Date step3CompletedDate) { this.step3CompletedDate = step3CompletedDate; }
    
    // Step 4
    public boolean isStep4Completed() { return step4Completed; }
    public void setStep4Completed(boolean step4Completed) { this.step4Completed = step4Completed; }
    
    public String getStep4Status() { return step4Status; }
    public void setStep4Status(String step4Status) { this.step4Status = step4Status; }
    
    public Date getOrientationDate() { return orientationDate; }
    public void setOrientationDate(Date orientationDate) { this.orientationDate = orientationDate; }
    
    public String getOrientationTime() { return orientationTime; }
    public void setOrientationTime(String orientationTime) { this.orientationTime = orientationTime; }
    
    public String getOrientationMode() { return orientationMode; }
    public void setOrientationMode(String orientationMode) { this.orientationMode = orientationMode; }
    
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    
    public String getOrientationLocation() { return orientationLocation; }
    public void setOrientationLocation(String orientationLocation) { this.orientationLocation = orientationLocation; }
    
    public String getAssignedTrainer() { return assignedTrainer; }
    public void setAssignedTrainer(String assignedTrainer) { this.assignedTrainer = assignedTrainer; }
    
    public Date getStep4CompletedDate() { return step4CompletedDate; }
    public void setStep4CompletedDate(Date step4CompletedDate) { this.step4CompletedDate = step4CompletedDate; }
    
    // Step 5
    public boolean isStep5Completed() { return step5Completed; }
    public void setStep5Completed(boolean step5Completed) { this.step5Completed = step5Completed; }
    
    public String getStep5Status() { return step5Status; }
    public void setStep5Status(String step5Status) { this.step5Status = step5Status; }
    
    public boolean isBasicInductionCompleted() { return basicInductionCompleted; }
    public void setBasicInductionCompleted(boolean basicInductionCompleted) { this.basicInductionCompleted = basicInductionCompleted; }
    
    public Date getBasicInductionDate() { return basicInductionDate; }
    public void setBasicInductionDate(Date basicInductionDate) { this.basicInductionDate = basicInductionDate; }
    
    public boolean isRoleBasedTrainingCompleted() { return roleBasedTrainingCompleted; }
    public void setRoleBasedTrainingCompleted(boolean roleBasedTrainingCompleted) { this.roleBasedTrainingCompleted = roleBasedTrainingCompleted; }
    
    public Date getRoleBasedTrainingDate() { return roleBasedTrainingDate; }
    public void setRoleBasedTrainingDate(Date roleBasedTrainingDate) { this.roleBasedTrainingDate = roleBasedTrainingDate; }
    
    public String getTrainingCompletionStatus() { return trainingCompletionStatus; }
    public void setTrainingCompletionStatus(String trainingCompletionStatus) { this.trainingCompletionStatus = trainingCompletionStatus; }
    
    public String getStep5Notes() { return step5Notes; }
    public void setStep5Notes(String step5Notes) { this.step5Notes = step5Notes; }
    
    public Date getStep5CompletedDate() { return step5CompletedDate; }
    public void setStep5CompletedDate(Date step5CompletedDate) { this.step5CompletedDate = step5CompletedDate; }
    
    // Step 6
    public boolean isStep6Completed() { return step6Completed; }
    public void setStep6Completed(boolean step6Completed) { this.step6Completed = step6Completed; }
    
    public String getStep6Status() { return step6Status; }
    public void setStep6Status(String step6Status) { this.step6Status = step6Status; }
    
    public boolean isAllStepsCompleted() { return allStepsCompleted; }
    public void setAllStepsCompleted(boolean allStepsCompleted) { this.allStepsCompleted = allStepsCompleted; }
    
    public String getHrNotificationStatus() { return hrNotificationStatus; }
    public void setHrNotificationStatus(String hrNotificationStatus) { this.hrNotificationStatus = hrNotificationStatus; }
    
    public String getHiringManagerNotificationStatus() { return hiringManagerNotificationStatus; }
    public void setHiringManagerNotificationStatus(String hiringManagerNotificationStatus) { this.hiringManagerNotificationStatus = hiringManagerNotificationStatus; }
    
    public boolean isWelcomeEmailSent() { return welcomeEmailSent; }
    public void setWelcomeEmailSent(boolean welcomeEmailSent) { this.welcomeEmailSent = welcomeEmailSent; }
    
    public Date getStep6CompletedDate() { return step6CompletedDate; }
    public void setStep6CompletedDate(Date step6CompletedDate) { this.step6CompletedDate = step6CompletedDate; }
    
    // Metadata
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
