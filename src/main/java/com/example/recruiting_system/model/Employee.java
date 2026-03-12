package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    
    // Link to Application
    private String applicationId;
    
    // Basic Employee Info
    private String employeeId; // Unique employee ID (generated)
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private String department;
    private Double salary;
    
    // Employment Details
    private String employmentType; // Full-time, Part-time, Contract
    private Date joiningDate;
    private String reportsTo; // Manager name/ID
    private String officeLocation;
    
    // Status
    private String status; // Active, Inactive, On Leave, Terminated
    private Date createdDate;
    private Date activationDate;
    
    // Onboarding Status
    private String onboardingStatus; // Not Started, In Progress, Completed
    private Date onboardingStartDate;
    private Date onboardingCompletedDate;

    public Employee() {}

    public Employee(String applicationId, String employeeId, String firstName, String lastName, 
                    String email, String position, String department, Double salary) {
        this.applicationId = applicationId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.status = "Active";
        this.createdDate = new Date();
        this.onboardingStatus = "Not Started";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public Date getJoiningDate() { return joiningDate; }
    public void setJoiningDate(Date joiningDate) { this.joiningDate = joiningDate; }

    public String getReportsTo() { return reportsTo; }
    public void setReportsTo(String reportsTo) { this.reportsTo = reportsTo; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getActivationDate() { return activationDate; }
    public void setActivationDate(Date activationDate) { this.activationDate = activationDate; }

    public String getOnboardingStatus() { return onboardingStatus; }
    public void setOnboardingStatus(String onboardingStatus) { this.onboardingStatus = onboardingStatus; }

    public Date getOnboardingStartDate() { return onboardingStartDate; }
    public void setOnboardingStartDate(Date onboardingStartDate) { this.onboardingStartDate = onboardingStartDate; }

    public Date getOnboardingCompletedDate() { return onboardingCompletedDate; }
    public void setOnboardingCompletedDate(Date onboardingCompletedDate) { this.onboardingCompletedDate = onboardingCompletedDate; }
}
