package com.example.recruiting_system.service;

import com.example.recruiting_system.model.Offer;
import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.repository.OfferRepository;
import com.example.recruiting_system.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    public Offer create(Offer offer) {
        offer.setCreatedDate(new Date());
        offer.setUpdatedDate(new Date());
        offer.setStatus("Draft");
        return offerRepository.save(offer);
    }

    public Optional<Offer> findById(String id) { return offerRepository.findById(id); }
    public List<Offer> findByCandidate(String candidateId) { return offerRepository.findByCandidateId(candidateId); }
    
    /**
     * Find all offers for an applicant by username
     * This method first finds all applications by username, then finds offers linked to those applications
     * It also enriches offers with job title from the application if positionTitle is missing
     */
    public List<Offer> findOffersByUsername(String username) {
        List<Application> applications = applicationRepository.findByApplicantUsername(username);
        List<Offer> offers = new ArrayList<>();
        
        for (Application app : applications) {
            // Try to find offer by applicationId
            List<Offer> offersByApp = offerRepository.findByApplicationId(app.getId());
            for (Offer offer : offersByApp) {
                // Enrich offer with job title if missing
                if ((offer.getPositionTitle() == null || offer.getPositionTitle().isEmpty()) && app.getJobTitle() != null) {
                    offer.setPositionTitle(app.getJobTitle());
                }
                offers.add(offer);
            }
            
            // Also try to find by candidateId if the offer has it set
            if (app.getUserId() != null) {
                List<Offer> offersByCandidate = offerRepository.findByCandidateId(app.getUserId());
                // Avoid duplicates
                for (Offer offer : offersByCandidate) {
                    // Enrich offer with job title if missing
                    if ((offer.getPositionTitle() == null || offer.getPositionTitle().isEmpty()) && app.getJobTitle() != null) {
                        offer.setPositionTitle(app.getJobTitle());
                    }
                    if (!offers.stream().anyMatch(o -> o.getId().equals(offer.getId()))) {
                        offers.add(offer);
                    }
                }
            }
        }
        
        return offers;
    }
    
    public List<Offer> findAll() { return offerRepository.findAll(); }
    public Offer save(Offer offer) { offer.setUpdatedDate(new Date()); return offerRepository.save(offer); }
    public void delete(String id) { offerRepository.deleteById(id); }
}
