package com.keltron.dogbreeder.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.dogbreeder.dto.DogBreederRegistrationInspectionDto;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.entity.DogBreederRegistrationInspection;
import com.keltron.dogbreeder.repository.DogBreederApplicationStatusMasterRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationInspectionRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;

@Service
@Transactional
public class DogBreederRegistrationInspectionServiceImpl {

    @Autowired
    private DogBreederRegistrationInspectionRepository repository;

    @Autowired
    private DogBreederRegistrationApplicationRepository applicationRepository;

    @Autowired
    private DogBreederApplicationStatusMasterRepository applicationStatusMasterRepository;

    @Value("${application.inspection.upload-dir}")
    private String inspectionUploadDir;

    /**
     * Save Inspection Schedule
     */
    public DogBreederRegistrationInspection save(
            DogBreederRegistrationInspectionDto dto) {

        DogBreederRegistrationInspection inspection =
                new DogBreederRegistrationInspection();

        inspection.copyFromDTO(dto);

        DogBreederRegistrationApplication application =
                applicationRepository.findById(dto.getApplicationId())
                        .orElseThrow(() ->
                                new RuntimeException("Application not found."));

        inspection.setApplication(application);

        DogBreederRegistrationInspection savedInspection =
                repository.save(inspection);

        ApplicationStatusMaster inspectionScheduled =
                applicationStatusMasterRepository
                        .findByStatusCode("INSPECTION_SCHEDULED")
                        .orElseThrow(() ->
                                new RuntimeException("Status INSPECTION_SCHEDULED not found."));

        application.setStatus(inspectionScheduled);

        applicationRepository.save(application);

        return savedInspection;
    }

    /**
     * Update Inspection
     */
    public DogBreederRegistrationInspection update(
            Long id,
            DogBreederRegistrationInspectionDto dto) {

        DogBreederRegistrationInspection inspection =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Inspection not found."));

        inspection.copyFromDTO(dto);

        if (dto.getApplicationId() != null) {

            DogBreederRegistrationApplication application =
                    applicationRepository.findById(dto.getApplicationId())
                            .orElseThrow(() ->
                                    new RuntimeException("Application not found."));

            inspection.setApplication(application);
        }

        return repository.save(inspection);
    }

    /**
     * Get Inspection By Id
     */
    @Transactional(readOnly = true)
    public DogBreederRegistrationInspection get(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Inspection not found."));
    }

    /**
     * Get Inspection By Application
     */
    @Transactional(readOnly = true)
    public DogBreederRegistrationInspection getByApplication(
            Long applicationId) {

        return repository.findByApplication_Id(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Inspection not found."));
    }
    /**
     * List All Inspections
     */
    @Transactional(readOnly = true)
    public List<DogBreederRegistrationInspection> getAll() {

        return repository.findAllByOrderByIdDesc();
    }

    /**
     * Delete Inspection
     */
    public boolean delete(Long id) {

        DogBreederRegistrationInspection inspection =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Inspection not found."));

        repository.delete(inspection);

        return true;
    }

    /**
     * Upload Inspection Report
     */
    public DogBreederRegistrationInspection uploadInspectionReport(
            Long applicationId,
            MultipartFile reportFile,
            String remarks,
            String recommendation) {

        DogBreederRegistrationInspection inspection =
                repository.findByApplication_Id(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Inspection not found."));

        try {

            Path reportDirectory = Paths.get(inspectionUploadDir);

            Files.createDirectories(reportDirectory);

            String fileName =
                    System.currentTimeMillis() + "_"
                            + reportFile.getOriginalFilename();

            Path filePath = reportDirectory.resolve(fileName);

            Files.copy(
                    reportFile.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            inspection.setInspectionReport(filePath.toString());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload inspection report.", e);
        }

        inspection.setInspectionRemarks(remarks);
        inspection.setRecommendation(recommendation);
        inspection.setStatus(recommendation);

        DogBreederRegistrationApplication application =
                inspection.getApplication();

        String statusCode =
                recommendation.equalsIgnoreCase("APPROVED")
                        ? "VERIFIED_BY_CVO"
                        : "REJECTED_BY_CVO";

        ApplicationStatusMaster applicationStatus =
                applicationStatusMasterRepository
                        .findByStatusCode(statusCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Status not found : " + statusCode));

        application.setStatus(applicationStatus);

        applicationRepository.save(application);

        return repository.save(inspection);
    }
}