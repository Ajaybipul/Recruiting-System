package com.example.recruiting_system.controller;

import com.example.recruiting_system.model.ApplicantProfile;
import com.example.recruiting_system.model.Application;
import com.example.recruiting_system.model.Offer;
import com.example.recruiting_system.repository.ApplicationRepository;
import com.example.recruiting_system.security.User;
import com.example.recruiting_system.service.ApplicantProfileService;
import com.example.recruiting_system.service.ApplicationService;
import com.example.recruiting_system.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    @Autowired
    private ApplicantProfileService profileService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OfferService offerService;

    private boolean isApplicant(HttpSession session) {
        Object roles = session.getAttribute("roles");
        if (roles instanceof List) {
            return ((List<?>) roles).contains("ROLE_APPLICANT");
        }
        return false;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        if (!isApplicant(session)) return "redirect:/access-denied";

        String userId = (String) session.getAttribute("userId");

        ApplicantProfile profile = profileService.getByUserId(userId)
                .orElseGet(() -> {
                    ApplicantProfile p = new ApplicantProfile();
                    p.setUserId(userId);
                    return p;
                });

        String skillsString = (profile.getSkills() != null && !profile.getSkills().isEmpty())
                ? String.join(", ", profile.getSkills())
                : "";

        model.addAttribute("profile", profile);
        model.addAttribute("skillsString", skillsString);
        model.addAttribute("editMode", false);

        return "applicant_profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {

        if (!isApplicant(session)) return "redirect:/access-denied";

        String userId = (String) session.getAttribute("userId");

        ApplicantProfile profile = profileService.getByUserId(userId)
                .orElse(new ApplicantProfile());

        String skillsString = (profile.getSkills() != null)
                ? String.join(", ", profile.getSkills())
                : "";

        model.addAttribute("profile", profile);
        model.addAttribute("skillsString", skillsString);
        model.addAttribute("editMode", true);

        return "applicant_profile";
    }
    @GetMapping("/applications")
    public String viewApplications(HttpSession session, Model model) {

    if (!isApplicant(session)) 
        return "redirect:/access-denied";

    String username = (String) session.getAttribute("username");

    List<Application> apps = applicationRepository.findByApplicantUsername(username);
    
    // Debug: Print application IDs
    for (Application app : apps) {
        System.out.println("DEBUG: Application loaded - ID: " + app.getId() + ", Title: " + app.getJobTitle() + ", Status: " + app.getStatus());
    }

    // Create a map of application ID to application for easy access in template
    Map<String, Application> appMap = new HashMap<>();
    for (Application app : apps) {
        if (app.getId() != null) {
            appMap.put(app.getId(), app);
        }
    }

    model.addAttribute("applications", apps);
    model.addAttribute("appMap", appMap);

    return "applicant/applications"; // templates/applicant/applications.html
}
@GetMapping({"/home", "/dashboard"})
public String home(HttpSession session, Model model) {

    if (!isApplicant(session))
        return "redirect:/access-denied";

    String username = (String) session.getAttribute("username");

    // Get all applications
    List<Application> apps = applicationRepository.findByApplicantUsername(username);

    // Count applications by status
    int totalApps = apps.size();
    int inReview = (int) apps.stream().filter(a -> "In Review".equals(a.getStatus())).count();
    int shortlisted = (int) apps.stream().filter(a -> "Shortlisted".equals(a.getStatus())).count();
    int rejected = (int) apps.stream().filter(a -> "Rejected".equals(a.getStatus())).count();
    int accepted = (int) apps.stream().filter(a -> "Accepted".equals(a.getStatus())).count();

    model.addAttribute("totalApplications", totalApps);
    model.addAttribute("inReview", inReview);
    model.addAttribute("shortlisted", shortlisted);
    model.addAttribute("rejected", rejected);
    model.addAttribute("accepted", accepted);
    model.addAttribute("applications", apps);

    return "applicant/dashboard"; // templates/applicant/dashboard.html
}
    @GetMapping("/application/{id}")
    public String viewApplicationDetail(@PathVariable("id") String id, HttpSession session, Model model) {

        if (!isApplicant(session)) return "redirect:/access-denied";

        String username = (String) session.getAttribute("username");

        Optional<Application> appOpt = applicationRepository.findById(id);
        if (appOpt.isEmpty()) {
            return "redirect:/applicant/applications"; // not found, go back to list
        }

        Application app = appOpt.get();

        // ensure applicant can only view their own application
        if (app.getApplicantUsername() == null || !app.getApplicantUsername().equals(username)) {
            return "redirect:/access-denied";
        }

        // DEBUG: Log HM fields
        System.out.println("\n=== APPLICANT DETAIL DEBUG ===");
        System.out.println("Application ID: " + app.getId());
        System.out.println("Full Name: " + app.getFullName());
        System.out.println("Job Title: " + app.getJobTitle());
        System.out.println("HM Feedback: " + app.getHmFeedback());
        System.out.println("HM Notes: " + app.getHmNotes());
        System.out.println("HM Decision: " + app.getHmDecision());
        System.out.println("HM Rating: " + app.getHmRating());
        System.out.println("Status: " + app.getStatus());
        System.out.println("=== END DEBUG ===\n");

        model.addAttribute("application", app);

        return "applicant/application_detail";
    }
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String education,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String dateOfBirth,
                                @RequestParam(required = false) String emergencyContact,
                                @RequestParam(required = false) String bankDetails,
                                Model model) {

        if (!isApplicant(session)) return "redirect:/access-denied";

        String userId = (String) session.getAttribute("userId");

        // validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return reloadWithError(model, userId, "Invalid email format");
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() != 10) {
            return reloadWithError(model, userId, "Phone must be 10 digits");
        }

        List<String> skillList = new ArrayList<>();
        if (StringUtils.hasText(skills)) {
            skillList = Arrays.stream(skills.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        ApplicantProfile profile = profileService.getByUserId(userId)
                .orElseGet(() -> {
                    ApplicantProfile p = new ApplicantProfile();
                    p.setUserId(userId);
                    return p;
                });

        profile.setEmail(email);
        profile.setPhone(digits);
        profile.setLocation(location);
        profile.setEducation(education);
        profile.setSkills(skillList);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setAddress(address);
        profile.setDateOfBirth(dateOfBirth);
        profile.setEmergencyContact(emergencyContact);
        profile.setBankDetails(bankDetails);

        profileService.saveOrUpdate(profile);

        model.addAttribute("success", "Profile saved successfully");
        return reloadModel(model, profile, false);
    }



    @PostMapping("/resume/upload")
    public String uploadResume(HttpSession session,
                               @RequestParam("resume") MultipartFile resume,
                               Model model) {

        if (!isApplicant(session)) return "redirect:/access-denied";

        String userId = (String) session.getAttribute("userId");

        try {
            profileService.uploadResume(userId, resume);
            model.addAttribute("success", "Resume uploaded successfully");
        } catch (IOException e) {
            return reloadWithError(model, userId, "Failed to upload resume: " + e.getMessage());
        }

        ApplicantProfile profile = profileService.getByUserId(userId).orElse(null);
        return reloadModel(model, profile, false);
    }


    // helper method to reload model safely
    private String reloadModel(Model model, ApplicantProfile profile, boolean editMode) {

        String skillsString = (profile.getSkills() != null)
                ? String.join(", ", profile.getSkills())
                : "";

        model.addAttribute("profile", profile);
        model.addAttribute("skillsString", skillsString);
        model.addAttribute("editMode", editMode);

        return "applicant_profile";
    }

    private String reloadWithError(Model model, String userId, String errorMsg) {

        ApplicantProfile profile = profileService.getByUserId(userId)
                .orElse(new ApplicantProfile());

        model.addAttribute("error", errorMsg);

        return reloadModel(model, profile, true);
    }

    // ============================================
    // OFFER MANAGEMENT - Applicant Side
    // ============================================

    @GetMapping("/offer/{id}")
    public String viewOffer(@PathVariable("id") String applicationId, HttpSession session, Model model) {
        if (!isApplicant(session)) {
            return "redirect:/access-denied";
        }

        String username = (String) session.getAttribute("username");
        Application application = applicationService.findByIdWithDebug(applicationId);

        if (application == null || !application.getApplicantUsername().equals(username)) {
            return "redirect:/access-denied";
        }

        // Only show if status is OFFER_SENT
        if (!application.getStatus().equals("OFFER_SENT")) {
            return "redirect:/applicant/applications";
        }

        // Fetch the offer from offerId
        Offer offer = null;
        if (application.getOfferId() != null) {
            offer = offerService.findById(application.getOfferId()).orElse(null);
        }

        if (offer == null) {
            return "redirect:/applicant/applications?error=Offer not found";
        }

        model.addAttribute("application", application);
        model.addAttribute("offer", offer);

        System.out.println("✓ Applicant viewing offer for: " + application.getFullName());
        System.out.println("  Offer Status: " + offer.getStatus());
        System.out.println("  Position: " + offer.getPositionTitle());

        return "applicant_offer_view";
    }

    @PostMapping("/offer/{id}/accept")
    public String acceptOffer(@PathVariable("id") String applicationId, HttpSession session) {
        if (!isApplicant(session)) {
            return "redirect:/access-denied";
        }

        // Validate applicationId
        if (applicationId == null || applicationId.trim().isEmpty() || applicationId.equals("/") ||
            applicationId.equals("accept") || applicationId.equals("decline")) {
            System.err.println("❌ Invalid applicationId: " + applicationId);
            return "redirect:/applicant/applications?error=Invalid application ID";
        }

        String username = (String) session.getAttribute("username");
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application == null || !application.getApplicantUsername().equals(username)) {
            System.err.println("❌ Application not found or access denied for ID: " + applicationId);
            return "redirect:/access-denied";
        }

        try {
            // Update application status
            application.setStatus("OFFER_ACCEPTED");
            application.setOfferStatus("ACCEPTED");
            applicationRepository.save(application);

            // Update offer status
            if (application.getOfferId() != null) {
                Offer offer = offerService.findById(application.getOfferId()).orElse(null);
                if (offer != null) {
                    offer.setStatus("ACCEPTED");
                    offer.setUpdatedDate(new Date());
                    offerService.save(offer);

                    System.out.println("✓ Offer ACCEPTED by: " + application.getFullName());
                    System.out.println("  Offer ID: " + offer.getId());
                    System.out.println("  Start Date: " + offer.getStartDate());
                }
            }

            return "redirect:/applicant/applications?success=Offer accepted! Welcome aboard!";

        } catch (Exception e) {
            System.err.println("❌ Error accepting offer: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/applicant/offer/" + applicationId + "?error=Failed to accept offer";
        }
    }

    @PostMapping("/offer/{id}/decline")
    public String declineOffer(@PathVariable("id") String applicationId, HttpSession session) {
        if (!isApplicant(session)) {
            return "redirect:/access-denied";
        }

        // Validate applicationId
        if (applicationId == null || applicationId.trim().isEmpty() || applicationId.equals("/") ||
            applicationId.equals("accept") || applicationId.equals("decline")) {
            System.err.println("❌ Invalid applicationId: " + applicationId);
            return "redirect:/applicant/applications?error=Invalid application ID";
        }

        String username = (String) session.getAttribute("username");
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application == null || !application.getApplicantUsername().equals(username)) {
            System.err.println("❌ Application not found or access denied for ID: " + applicationId);
            return "redirect:/access-denied";
        }

        try {
            // Update application status
            application.setStatus("OFFER_DECLINED");
            application.setOfferStatus("DECLINED");
            applicationRepository.save(application);

            // Update offer status
            if (application.getOfferId() != null) {
                Offer offer = offerService.findById(application.getOfferId()).orElse(null);
                if (offer != null) {
                    offer.setStatus("DECLINED");
                    offer.setUpdatedDate(new Date());
                    offerService.save(offer);

                    System.out.println("✗ Offer DECLINED by: " + application.getFullName());
                    System.out.println("  Offer ID: " + offer.getId());
                }
            }

            return "redirect:/applicant/applications?success=Offer declined";

        } catch (Exception e) {
            System.err.println("❌ Error declining offer: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/applicant/offer/" + applicationId + "?error=Failed to decline offer";
        }
    }

    @PostMapping("/offer/{id}/restore")
    public String restoreOffer(@PathVariable("id") String applicationId, HttpSession session) {
        if (!isApplicant(session)) {
            return "redirect:/access-denied";
        }

        // Validate applicationId
        if (applicationId == null || applicationId.trim().isEmpty() || applicationId.equals("/")) {
            System.err.println("❌ Invalid applicationId: " + applicationId);
            return "redirect:/applicant/applications?error=Invalid application ID";
        }

        String username = (String) session.getAttribute("username");
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application == null || !application.getApplicantUsername().equals(username)) {
            System.err.println("❌ Application not found or access denied for ID: " + applicationId);
            return "redirect:/access-denied";
        }

        try {
            // Restore application status back to OFFER_SENT
            application.setStatus("OFFER_SENT");
            application.setOfferStatus("SENT");
            applicationRepository.save(application);

            // Update offer status back to SENT
            if (application.getOfferId() != null) {
                Offer offer = offerService.findById(application.getOfferId()).orElse(null);
                if (offer != null) {
                    offer.setStatus("SENT");
                    offer.setUpdatedDate(new Date());
                    offerService.save(offer);

                    System.out.println("✓ Offer RESTORED for: " + application.getFullName());
                    System.out.println("  Offer ID: " + offer.getId());
                }
            }

            return "redirect:/applicant/applications?success=Offer restored! You can now accept it.";

        } catch (Exception e) {
            System.err.println("❌ Error restoring offer: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/applicant/applications?error=Failed to restore offer";
        }
    }
}
