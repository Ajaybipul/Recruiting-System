package com.example.recruiting_system.service;

import com.example.recruiting_system.model.Interview;
import com.example.recruiting_system.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    public Interview schedule(Interview interview) {
        interview.setCreatedDate(new Date());
        interview.setUpdatedDate(new Date());
        interview.setStatus("Scheduled");
        return interviewRepository.save(interview);
    }

    public Optional<Interview> findById(String id) { return interviewRepository.findById(id); }
    public List<Interview> findByCandidate(String candidateId) { return interviewRepository.findByCandidateId(candidateId); }
    public List<Interview> findAll() { return interviewRepository.findAll(); }
    public Interview save(Interview interview) { interview.setUpdatedDate(new Date()); return interviewRepository.save(interview); }
}
