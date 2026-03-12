package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.Recruiter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
}
