package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.ApplicantProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicantProfileRepository extends MongoRepository<ApplicantProfile, String> {
    Optional<ApplicantProfile> findByUserId(String userId);
}
