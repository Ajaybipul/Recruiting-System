package com.example.recruiting_system.repository;

import com.example.recruiting_system.model.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {
    List<Offer> findByCandidateId(String candidateId);
    List<Offer> findByApplicationId(String applicationId);
}
