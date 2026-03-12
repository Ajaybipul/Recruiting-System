package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.JobPosition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPositionRepository extends MongoRepository<JobPosition, String> {
    List<JobPosition> findByPublishedTrue();
}
