package com.example.recruiting_system.service;

import com.example.recruiting_system.model.ApplicantProfile;
import com.example.recruiting_system.repository.ApplicantProfileRepository;
import com.example.recruiting_system.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicantProfileServiceImpl implements ApplicantProfileService {

    private final ApplicantProfileRepository profileRepository;

    @Autowired
    public ApplicantProfileServiceImpl(ApplicantProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<ApplicantProfile> getByUserId(String userId) {
        return profileRepository.findByUserId(userId);
    }

    @Override
    public ApplicantProfile saveOrUpdate(ApplicantProfile profile) {
        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    @Override
    public String uploadResume(String userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Empty file");
        }

        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".pdf")) {
            throw new IOException("Only PDF files are allowed");
        }

        Path uploadsDir = Paths.get("uploads", "resumes").toAbsolutePath();
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }

        String filename = userId + "-" + UUID.randomUUID().toString() + ".pdf";
        Path target = uploadsDir.resolve(filename);
        Files.copy(file.getInputStream(), target);

        // Build a web-path relative to application (so Thymeleaf can link). We store path as /uploads/resumes/<file>
        String webPath = "/uploads/resumes/" + filename;

        // update or create profile's resumeUrl
        Optional<ApplicantProfile> opt = profileRepository.findByUserId(userId);
        ApplicantProfile profile = opt.orElseGet(ApplicantProfile::new);
        profile.setUserId(userId);
        profile.setResumeUrl(webPath);
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);

        return webPath;
    }
}
