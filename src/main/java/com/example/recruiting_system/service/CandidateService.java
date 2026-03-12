package com.example.recruiting_system.service;

import com.example.recruiting_system.model.Candidate;
import com.example.recruiting_system.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    public Candidate create(Candidate candidate) {
        candidate.setCreatedDate(new Date());
        candidate.setUpdatedDate(new Date());
        candidate.setStatus("New");
        return candidateRepository.save(candidate);
    }

    public Optional<Candidate> findById(String id) { return candidateRepository.findById(id); }
    public List<Candidate> findByPosition(String positionId) { return candidateRepository.findByAppliedPositionId(positionId); }
    public List<Candidate> findAll() { return candidateRepository.findAll(); }
    public Candidate save(Candidate candidate) { candidate.setUpdatedDate(new Date()); return candidateRepository.save(candidate); }
}
