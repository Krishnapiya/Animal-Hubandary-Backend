package com.keltron.dogbreeder.services.impl;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log =
            LoggerFactory.getLogger(
                    DogBreederRegistrationInspectionServiceImpl.class
            );

    @Autowired
    private DogBreederRegistrationInspectionRepository repository;

    @Autowired
    private DogBreederRegistrationApplicationRepository applicationRepository;

    @Autowired
    private DogBreederApplicationStatusMasterRepository
            applicationStatusMasterRepository;

    @Autowired
    private DogBreederNotificationServiceImpl notificationService;

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
                                new RuntimeException(
                                        "Application not found."
                                )
                        );

        inspection.setApplication(application);

        DogBreederRegistrationInspection savedInspection =
                repository.save(inspection);

        ApplicationStatusMaster inspectionScheduled =
                applicationStatusMasterRepository
                        .findByStatusCode("INSPECTION_SCHEDULED")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Status INSPECTION_SCHEDULED not found."
                                )
                        );

        application.setStatus(inspectionScheduled);

        applicationRepository.save(application);

        // Trigger Notification to the Breeder
        Long recipientUserId =
                getApplicantUserId(application);

        if (recipientUserId != null) {

            String inspectionDateDetails =
                    dto.getInspectionDate() != null
                            ? dto.getInspectionDate().toString()
                            : null;

            notificationService.triggerStatusNotification(
                    application,
                    "INSPECTION_SCHEDULED",
                    recipientUserId,
                    inspectionDateDetails
            );
        }

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
                                new RuntimeException(
                                        "Inspection not found."
                                )
                        );

        inspection.copyFromDTO(dto);

        if (dto.getApplicationId() != null) {

            DogBreederRegistrationApplication application =
                    applicationRepository.findById(
                            dto.getApplicationId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Application not found."
                            )
                    );

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
                        new RuntimeException(
                                "Inspection not found."
                        )
                );
    }

    /**
     * Get Inspection By Application
     *
     * IMPORTANT:
     * An application can have multiple inspection records
     * after resubmission.
     *
     * Therefore, always return the latest inspection.
     */
    @Transactional(readOnly = true)
    public DogBreederRegistrationInspection getByApplication(
            Long applicationId) {

        return repository
                .findTopByApplication_IdOrderByIdDesc(applicationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inspection not found."
                        )
                );
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
                                new RuntimeException(
                                        "Inspection not found."
                                )
                        );

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

        log.info(
                "Uploading inspection report. applicationId={}, directory={}",
                applicationId,
                inspectionUploadDir
        );

        /*
         * Validate application ID
         */
        if (applicationId == null) {

            throw new RuntimeException(
                    "Application ID is required."
            );
        }

        /*
         * Validate uploaded file
         */
        if (reportFile == null || reportFile.isEmpty()) {

            throw new RuntimeException(
                    "Inspection report file is required."
            );
        }

        /*
         * Validate recommendation
         */
        if (recommendation == null
                || recommendation.isBlank()) {

            throw new RuntimeException(
                    "Recommendation is required."
            );
        }

        /*
         * Find latest inspection
         *
         * IMPORTANT:
         *
         * A resubmitted application may have more than one
         * inspection record.
         *
         * Do NOT use:
         *
         * repository.findByApplication_Id(applicationId)
         *
         * because that expects only one result.
         *
         * Instead, get the latest inspection.
         */
        DogBreederRegistrationInspection inspection =
                repository
                        .findTopByApplication_IdOrderByIdDesc(
                                applicationId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inspection not found."
                                )
                        );

        try {

            /*
             * Validate configured upload directory
             */
            if (inspectionUploadDir == null
                    || inspectionUploadDir.isBlank()) {

                throw new RuntimeException(
                        "Inspection upload directory is not configured."
                );
            }

            /*
             * Create upload directory
             */
            Path reportDirectory =
                    Paths.get(inspectionUploadDir)
                            .toAbsolutePath()
                            .normalize();

            log.info(
                    "Inspection report directory: {}",
                    reportDirectory
            );

            Files.createDirectories(reportDirectory);

            /*
             * Check directory exists
             */
            if (!Files.exists(reportDirectory)) {

                throw new RuntimeException(
                        "Upload directory does not exist: "
                                + reportDirectory
                );
            }

            /*
             * Check directory
             */
            if (!Files.isDirectory(reportDirectory)) {

                throw new RuntimeException(
                        "Upload path is not a directory: "
                                + reportDirectory
                );
            }

            /*
             * Check write permission
             */
            if (!Files.isWritable(reportDirectory)) {

                throw new RuntimeException(
                        "Upload directory is not writable: "
                                + reportDirectory
                );
            }

            /*
             * Get original filename
             */
            String originalFileName =
                    reportFile.getOriginalFilename();

            if (originalFileName == null
                    || originalFileName.isBlank()) {

                originalFileName = "inspection-report";
            }

            /*
             * Remove possible path information
             *
             * Example:
             *
             * ../../test.pdf
             *
             * becomes:
             *
             * test.pdf
             */
            originalFileName =
                    Paths.get(originalFileName)
                            .getFileName()
                            .toString();

            /*
             * Remove unsafe characters
             */
            originalFileName =
                    originalFileName.replaceAll(
                            "[^a-zA-Z0-9._-]",
                            "_"
                    );

            /*
             * Generate unique filename
             */
            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + originalFileName;

            Path filePath =
                    reportDirectory
                            .resolve(fileName)
                            .normalize();

            /*
             * Security check
             */
            if (!filePath.startsWith(reportDirectory)) {

                throw new RuntimeException(
                        "Invalid inspection report file path."
                );
            }

            log.info(
                    "Saving inspection report to: {}",
                    filePath
            );

            /*
             * Save file
             */
            Files.copy(
                    reportFile.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            /*
             * Verify file saved
             */
            if (!Files.exists(filePath)) {

                throw new RuntimeException(
                        "Inspection report was not saved: "
                                + filePath
                );
            }

            /*
             * Save file path in DB
             */
            inspection.setInspectionReport(
                    filePath.toString()
            );

            log.info(
                    "Inspection report uploaded successfully: {}",
                    filePath
            );

        } catch (AccessDeniedException e) {

            log.error(
                    "Permission denied while uploading inspection report.",
                    e
            );

            throw new RuntimeException(
                    "Permission denied for inspection report directory: "
                            + inspectionUploadDir,
                    e
            );

        } catch (IOException e) {

            log.error(
                    "IOException while uploading inspection report. "
                            + "Directory={}, File={}",
                    inspectionUploadDir,
                    reportFile.getOriginalFilename(),
                    e
            );

            throw new RuntimeException(
                    "Failed to upload inspection report. "
                            + "Directory: "
                            + inspectionUploadDir
                            + ", File: "
                            + reportFile.getOriginalFilename()
                            + ", Error: "
                            + e.getMessage(),
                    e
            );
        }

        /*
         * IMPORTANT:
         *
         * Report remarks are separate from
         * inspection schedule remarks.
         */
        inspection.setReportRemarks(remarks);

        /*
         * Recommendation
         */
        inspection.setRecommendation(
                recommendation.trim()
        );

        DogBreederRegistrationApplication application =
                inspection.getApplication();

        if (application == null) {

            throw new RuntimeException(
                    "Application not found for inspection."
            );
        }

        /*
         * Determine application status
         */
        String statusCode =
                "APPROVED".equalsIgnoreCase(
                        recommendation.trim()
                )
                        ? "VERIFIED_BY_CVO"
                        : "REJECTED_BY_CVO";

        log.info(
                "Updating application {} status to {}",
                applicationId,
                statusCode
        );

        /*
         * Find status
         */
        ApplicationStatusMaster applicationStatus =
                applicationStatusMasterRepository
                        .findByStatusCode(statusCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Status not found: "
                                                + statusCode
                                )
                        );

        /*
         * Update application status
         */
        application.setStatus(applicationStatus);

        applicationRepository.save(application);

        /*
         * Save latest inspection
         */
        DogBreederRegistrationInspection savedInspection =
                repository.save(inspection);

        /*
         * Trigger notification
         */
        Long recipientUserId =
                getApplicantUserId(application);

        if (recipientUserId != null) {

            notificationService.triggerStatusNotification(
                    application,
                    statusCode,
                    recipientUserId
            );

            log.info(
                    "Status notification triggered. "
                            + "applicationId={}, status={}, recipientUserId={}",
                    applicationId,
                    statusCode,
                    recipientUserId
            );

        } else {

            log.warn(
                    "Applicant user ID not found. "
                            + "Notification not triggered. applicationId={}",
                    applicationId
            );
        }

        return savedInspection;
    }

    /**
     * Helper to resolve applicant's user ID
     */
    private Long getApplicantUserId(
            DogBreederRegistrationApplication application) {

        if (application == null) {
            return null;
        }

        /*
         * Check applicantUserId first
         */
        if (application.getApplicantUserId() != null) {

            return parseToLong(
                    application.getApplicantUserId()
            );
        }

        /*
         * Fall back to createdBy
         */
        if (application.getCreatedBy() != null) {

            return parseToLong(
                    application.getCreatedBy()
            );
        }

        return null;
    }

    /**
     * Safe conversion helper
     */
    private Long parseToLong(Object obj) {

        if (obj == null) {
            return null;
        }

        if (obj instanceof Long l) {
            return l;
        }

        try {

            return Long.parseLong(
                    obj.toString().trim()
            );

        } catch (NumberFormatException e) {

            log.warn(
                    "Unable to convert value to Long: {}",
                    obj
            );

            return null;
        }
    }
}