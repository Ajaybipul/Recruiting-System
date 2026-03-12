package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.Onboarding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingRepository extends MongoRepository<Onboarding, String> {
    Optional<Onboarding> findByApplicationId(String applicationId);
    Optional<Onboarding> findByCandidateId(String candidateId);
    List<Onboarding> findByOverallStatus(String overallStatus);
    List<Onboarding> findByStep2VerificationStatus(String verificationStatus);
}
