package com.example.recruiting_system.service;

import com.example.recruiting_system.model.ApplicantProfile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface ApplicantProfileService {
    Optional<ApplicantProfile> getByUserId(String userId);
    ApplicantProfile saveOrUpdate(ApplicantProfile profile);
    String uploadResume(String userId, MultipartFile file) throws IOException;
}
