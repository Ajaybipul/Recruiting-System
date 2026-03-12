package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.OnboardingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingDocumentRepository extends MongoRepository<OnboardingDocument, String> {
    List<OnboardingDocument> findByCandidateId(String candidateId);
    List<OnboardingDocument> findByStatus(String status);
}
