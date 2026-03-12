package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.Candidate;
import com.example.recruiting_system.model.JobPosition;
import com.example.recruiting_system.service.CandidateService;
import com.example.recruiting_system.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JobPositionService jobPositionService;

    @GetMapping("/apply/{positionId}")
    public String applyForm(@PathVariable String positionId, Model model) {
        JobPosition pos = jobPositionService.findById(positionId).orElse(new JobPosition());
        model.addAttribute("position", pos);
        model.addAttribute("candidate", new Candidate());
        return "apply";
    }

    @PostMapping("/apply")
    public String submitApplication(@ModelAttribute Candidate candidate) {
        candidate.setAppliedDate(new Date());
        candidate.setStatus("Applied");
        candidateService.create(candidate);
        return "redirect:/candidate/status";
    }

    @GetMapping("/status")
    public String statusPage() { return "application_status"; }
}
