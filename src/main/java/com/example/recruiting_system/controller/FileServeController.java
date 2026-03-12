package com.example.recruiting_system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileServeController {

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
    private final Path resumeDir = Paths.get(System.getProperty("user.dir"), "resumes");

    // Handle /uploads/* paths
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<byte[]> downloadFromUploads(@PathVariable String filename) {
        return serveFile(uploadDir, filename);
    }

    // Handle /resumes/* paths (legacy support)
    @GetMapping("/resumes/{filename:.+}")
    public ResponseEntity<byte[]> downloadFromResumes(@PathVariable String filename) {
        return serveFile(resumeDir, filename);
    }

    private ResponseEntity<byte[]> serveFile(Path baseDir, String filename) {
        try {
            // Ensure base directory exists
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }

            Path filePath = baseDir.resolve(filename).normalize();
            
            // Security check - ensure file is within the intended directory
            if (!filePath.startsWith(baseDir.normalize())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            File file = filePath.toFile();
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
