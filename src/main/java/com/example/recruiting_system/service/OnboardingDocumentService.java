package com.example.recruiting_system.service;

import com.example.recruiting_system.model.OnboardingDocument;
import com.example.recruiting_system.repository.OnboardingDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class OnboardingDocumentService {
    
    @Autowired
    private OnboardingDocumentRepository documentRepository;
    
    // Save or update document
    public OnboardingDocument saveDocument(OnboardingDocument document) {
        document.setUpdatedDate(new Date());
        return documentRepository.save(document);
    }
    
    // Find all documents
    public List<OnboardingDocument> findAllDocuments() {
        return documentRepository.findAll();
    }
    
    // Find documents by applicant/candidate ID
    public List<OnboardingDocument> findDocumentsByApplicantId(String applicantId) {
        return documentRepository.findByCandidateId(applicantId);
    }
    
    // Find documents by status
    public List<OnboardingDocument> findDocumentsByStatus(String status) {
        return documentRepository.findByStatus(status);
    }
    
    // Find single document by ID
    public Optional<OnboardingDocument> findDocumentById(String id) {
        return documentRepository.findById(id);
    }
    
    // Approve document
    public OnboardingDocument approveDocument(String documentId, String approvedBy) {
        Optional<OnboardingDocument> doc = documentRepository.findById(documentId);
        if (doc.isPresent()) {
            OnboardingDocument document = doc.get();
            document.setStatus("Approved");
            document.setVerifiedById(approvedBy);
            document.setVerificationDate(new Date());
            document.setUpdatedDate(new Date());
            return documentRepository.save(document);
        }
        return null;
    }
    
    // Reject document
    public OnboardingDocument rejectDocument(String documentId, String rejectionReason, String rejectedBy) {
        Optional<OnboardingDocument> doc = documentRepository.findById(documentId);
        if (doc.isPresent()) {
            OnboardingDocument document = doc.get();
            document.setStatus("Rejected");
            document.setNotes(rejectionReason);
            document.setVerifiedById(rejectedBy);
            document.setVerificationDate(new Date());
            document.setUpdatedDate(new Date());
            return documentRepository.save(document);
        }
        return null;
    }
    
    // Get pending documents
    public List<OnboardingDocument> findPendingDocuments() {
        return documentRepository.findByStatus("Submitted");
    }
    
    // Get approved documents
    public List<OnboardingDocument> findApprovedDocuments() {
        return documentRepository.findByStatus("Approved");
    }
    
    // Delete document
    public void deleteDocument(String documentId) {
        documentRepository.deleteById(documentId);
    }
    
    // Count by status
    public long countByStatus(String status) {
        return documentRepository.findByStatus(status).size();
    }
}
