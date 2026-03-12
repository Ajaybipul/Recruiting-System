package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByApplicantUsername(String username);

    List<Application> findByUserId(String userId);

    List<Application> findByJobId(String jobId);

    // REQUIRED for dashboard
    long countByStatus(String status);

    List<Application> findByStatus(String status);

    long countByJobIdAndStatus(String jobId, String status);

    // Recent 5 applications
    List<Application> findTop5ByOrderByAppliedDateDesc();
}
