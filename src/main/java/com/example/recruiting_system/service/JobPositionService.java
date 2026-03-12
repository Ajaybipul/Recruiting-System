package com.example.recruiting_system.service;

import com.example.recruiting_system.model.JobPosition;
import com.example.recruiting_system.repository.JobPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JobPositionService {

    @Autowired
    private JobPositionRepository jobPositionRepository;

    public JobPosition create(JobPosition pos) {
        pos.setCreatedDate(new Date());
        pos.setUpdatedDate(new Date());
        pos.setPublished(true); 
        return jobPositionRepository.save(pos);
    }

    public Optional<JobPosition> findById(String id){ return jobPositionRepository.findById(id); }
    public List<JobPosition> findPublished() { return jobPositionRepository.findByPublishedTrue(); }
    public List<JobPosition> findAll() { return jobPositionRepository.findAll(); }
    public JobPosition save(JobPosition pos) { pos.setUpdatedDate(new Date()); return jobPositionRepository.save(pos); }

    public long count() {
    return jobPositionRepository.count();
}


}
