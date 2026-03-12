package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.OnboardingChecklist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingChecklistRepository extends MongoRepository<OnboardingChecklist, String> {
    
    Optional<OnboardingChecklist> findByApplicationId(String applicationId);
    
    List<OnboardingChecklist> findByCandidateId(String candidateId);
    
    List<OnboardingChecklist> findAll();
    
    List<OnboardingChecklist> findByOverallStatus(String status);
    
    List<OnboardingChecklist> findByCreatedBy(String createdBy);
}
