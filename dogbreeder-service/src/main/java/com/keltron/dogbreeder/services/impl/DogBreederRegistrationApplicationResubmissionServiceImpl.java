package com.keltron.dogbreeder.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationResubmissionUploadDto;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;

@Service
public class DogBreederRegistrationApplicationResubmissionServiceImpl {

    @Autowired
    private DogBreederRegistrationApplicationRepository applicationRepository;

    @Transactional
    public DogBreederRegistrationApplicationResubmissionUploadDto uploadDocument(
            MultipartFile file,
            Long applicationId,
            String remarks) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        // 1. Fetch Application Record
        DogBreederRegistrationApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found for ID: " + applicationId));

        // 2. Prepare Directory Path
        String appNum = application.getApplicationNumber();
        String folderName = (appNum != null && !appNum.trim().isEmpty()) 
                ? appNum 
                : String.valueOf(applicationId);

        Path uploadDirectory = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "uploads",
                "resubmission",
                folderName
        );

        if (!Files.exists(uploadDirectory)) {
            Files.createDirectories(uploadDirectory);
        }

        // 3. Save Uploaded File
        String rawName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "doc");
        String savedFileName = System.currentTimeMillis() + "_" + rawName;
        Path destination = uploadDirectory.resolve(savedFileName);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // 4. Update Status and Save Entity
        // Update status ID to Resubmitted (e.g. 10L or your project's resubmitted status ID)
        application.setStatus(new ApplicationStatusMaster(10L));
        application.setSubmittedAt(LocalDateTime.now());

        applicationRepository.save(application);

        // 5. Response DTO
        DogBreederRegistrationApplicationResubmissionUploadDto responseDto = 
                new DogBreederRegistrationApplicationResubmissionUploadDto();

        responseDto.setFileName(rawName);
        responseDto.setFilePath("resubmission/" + folderName + "/" + savedFileName);

        return responseDto;
    }
}