package com.example.recruiting_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "recruiters")
public class Recruiter {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role; // recruiter, admin
    private List<String> assignedPositionIds = new ArrayList<>();
    private Date createdDate;
    private Date updatedDate;

    public Recruiter() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<String> getAssignedPositionIds() { return assignedPositionIds; }
    public void setAssignedPositionIds(List<String> assignedPositionIds) { this.assignedPositionIds = assignedPositionIds; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}
