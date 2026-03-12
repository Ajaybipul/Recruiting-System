package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByAppliedPositionId(String positionId);
    List<Candidate> findByEmail(String email);
}
