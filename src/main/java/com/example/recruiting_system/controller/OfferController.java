package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.Offer;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

@Controller
public class OfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private ApplicationService applicationService;

    // Applicant endpoints
    @GetMapping("/applicant/offers")
    public String listApplicantOffers(HttpSession session, Model model) {
        Object username = session.getAttribute("username");
        
        List<Offer> offers = new ArrayList<>();
        
        if (username != null) {
            // Try to find offers by username through applications
            offers = offerService.findOffersByUsername(username.toString());
        }
        
        // If no offers found by username, try by userId
        if (offers.isEmpty()) {
            Object uid = session.getAttribute("userId");
            String candidateId = uid != null ? uid.toString() : null;
            if (candidateId != null) {
                offers = offerService.findByCandidate(candidateId);
            }
        }
        
        // If still no offers, get all as fallback
        if (offers.isEmpty()) {
            offers = offerService.findAll();
        }
        
        model.addAttribute("offers", offers);
        return "offer_list";
    }
}

