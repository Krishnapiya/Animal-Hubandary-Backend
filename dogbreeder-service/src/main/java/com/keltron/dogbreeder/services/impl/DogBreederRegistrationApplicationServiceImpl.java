package com.keltron.dogbreeder.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationDto;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.repository.DogBreederApplicationDocumentRepository;
import com.keltron.dogbreeder.repository.DogBreederApplicationStatusMasterRepository;
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.dogbreeder.repository.DogBreederDeclarationRepository;
import com.keltron.dogbreeder.repository.DogBreederDetailRepository;
import com.keltron.dogbreeder.repository.DogBreederFacilityRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationResubmissionDto;

@Service
public class DogBreederRegistrationApplicationServiceImpl
        extends AbstractJpaService<
                DogBreederRegistrationApplicationDto,
                Long,
                DogBreederRegistrationApplicationRepository,
                DogBreederRegistrationApplication> {

    private static final String DOG_BREEDER_ENTITY_TYPE = "DOG_BREEDER";

    private static final String DRAFT_STATUS_CODE = "DRAFT";
    private static final String SUBMITTED_STATUS_CODE = "SUBMITTED";
    private static final String FORWARDED_TO_CVO_STATUS_CODE = "FORWARDED_TO_CVO";
    private static final String INSPECTION_SCHEDULED_STATUS_CODE = "INSPECTION_SCHEDULED";
    private static final String VERIFIED_BY_CVO_STATUS_CODE = "VERIFIED_BY_CVO";
    private static final String APPROVED_STATUS_CODE = "APPLICATION_APPROVED";
    private static final String REJECTED_STATUS_CODE = "APPLICATION_REJECTED";

    @Autowired
    private DogBreederRegistrationApplicationRepository applicationRepository;

    @Autowired
    private DogBreederDetailRepository detailRepository;

    @Autowired
    private DogBreederFacilityRepository facilityRepository;

    @Autowired
    private DogBreederDeclarationRepository declarationRepository;

    @Autowired
    private DogBreederBreedRepository breedRepository;

    @Autowired
    private DogBreederApplicationDocumentRepository documentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DogBreederApplicationStatusMasterRepository statusRepository;

    @Autowired
    private DogBreederNotificationServiceImpl notificationService;

    /**
     * Helper mapper method to populate breederName onto the DTO.
     */
    private DogBreederRegistrationApplicationDto mapToDto(DogBreederRegistrationApplication application) {
        if (application == null) {
            return null;
        }

        DogBreederRegistrationApplicationDto dto = application.toDTO();

        // 1. Try to fetch breeder name from DogBreederDetail
        detailRepository.findByApplicationId(application.getId())
                .ifPresentOrElse(
                    detail -> dto.setBreederName(detail.getBreederName()),
                    () -> {
                        // 2. Fallback to Applicant User Full Name if details record doesn't exist yet
                        if (application.getApplicantUserId() != null) {
                            usersRepository.findById(application.getApplicantUserId())
                                    .ifPresent(user -> dto.setBreederName(
                                            user.getFname() != null ? user.getFname() : user.getUsername()
                                    ));
                        }
                    }
                );

        return dto;
    }

    /**
     * Admin list: returns all Dog Breeder applications with breeder names.
     */
    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto> getDogBreederApplications() {
        return applicationRepository
                .findByEntityTypeOrderByIdDesc(DOG_BREEDER_ENTITY_TYPE)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Returns the latest Dog Breeder application ID.
     */
    @Transactional(readOnly = true)
    public Long getLatestDogBreederApplicationId() {
        return applicationRepository
                .findTopByEntityTypeOrderByIdDesc(DOG_BREEDER_ENTITY_TYPE)
                .map(DogBreederRegistrationApplication::getId)
                .orElseThrow(() -> new RuntimeException("No dog breeder application found"));
    }

    /**
     * Returns complete application preview details.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPreview(Long applicationId) {

        DogBreederRegistrationApplication application =
                getDogBreederApplication(applicationId);

        Map<String, Object> response = new HashMap<>();

        // Main application / registration details
        response.put(
                "registrationDetails",
                mapToDto(application)
        );

        // Breeder details
        var breederDetail =
                detailRepository
                        .findByApplicationId(applicationId)
                        .orElse(null);

        response.put(
                "breederDetails",
                breederDetail != null
                        ? breederDetail.toDTO()
                        : null
        );

        if (breederDetail != null) {

            Long dogBreederDetailId =
                    breederDetail.getId();

            // Facility details
            response.put(
                    "facilityDetails",
                    facilityRepository
                            .findByDogBreederDetail_Id(
                                    dogBreederDetailId
                            )
                            .map(facility -> facility.toDTO())
                            .orElse(null)
            );

            // Declaration details
            response.put(
                    "declarationDetails",
                    declarationRepository
                            .findByDogBreederDetail_Id(
                                    dogBreederDetailId
                            )
                            .map(declaration -> declaration.toDTO())
                            .orElse(null)
            );

            // Breed details
            response.put(
                    "breedDetails",
                    breedRepository
                            .findByDogBreederDetail_Id(
                                    dogBreederDetailId
                            )
                            .stream()
                            .map(breed -> breed.toDTO())
                            .toList()
            );

        } else {

            response.put("facilityDetails", null);
            response.put("declarationDetails", null);
            response.put("breedDetails", List.of());
        }

        // Uploaded documents
        List<?> documents =
                documentRepository
                        .findByApplication_IdOrderByIdAsc(applicationId)
                        .stream()
                        .map(document -> document.toDTO())
                        .toList();

        response.put("documentDetails", documents);

        // Alias used by frontend preview
        response.put("supportingDocuments", documents);

        return response;
    }
    /**
     * Breeder submits draft application.
     */
    @Transactional
    public DogBreederRegistrationApplicationDto submitApplication(Long applicationId) {

        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        if (application.getStatus() == null) {
            throw new RuntimeException("Application status not found.");
        }

        if (!DRAFT_STATUS_CODE.equalsIgnoreCase(application.getStatus().getStatusCode())) {
            throw new RuntimeException("Only Draft applications can be submitted.");
        }

        ApplicationStatusMaster submittedStatus =
                statusRepository.findByStatusCode(SUBMITTED_STATUS_CODE)
                        .orElseThrow(() -> new RuntimeException("SUBMITTED status not found"));

        application.setStatus(submittedStatus);

        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        // NOTIFICATION: Triggered on submission
        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Submitted",
                "Your Dog Breeder Registration application (" + savedApp.getApplicationNumber() + ") has been submitted successfully.",
                "INFO");

        return mapToDto(savedApp);
    }

    /**
     * Admin forwards an application to CVO.
     */
    @Transactional
    public String forwardToCvo(Long applicationId) {

        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        if (application.getStatus() != null &&
            FORWARDED_TO_CVO_STATUS_CODE.equalsIgnoreCase(application.getStatus().getStatusCode())) {
            return "Application is already forwarded to CVO";
        }

        ApplicationStatusMaster forwardedStatus =
                statusRepository.findByStatusCode(FORWARDED_TO_CVO_STATUS_CODE)
                        .orElseThrow(() -> new RuntimeException("FORWARDED_TO_CVO status not found"));

        application.setStatus(forwardedStatus);
        application.setForwardedToCvoAt(LocalDateTime.now());

        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        // NOTIFICATION: Triggered when admin forwards to CVO
        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Forwarded to CVO",
                "Your application (" + savedApp.getApplicationNumber() + ") has been forwarded to the District CVO for inspection.",
                "INFO");

        return "Application forwarded to CVO successfully";
    }

    /**
     * CVO Schedules Inspection.
     */
    /**
     * CVO Schedules Inspection.
     */
    /**
     * CVO Schedules Inspection.
     */
    /**
     * CVO Schedules Inspection.
     */
    @Transactional
    public DogBreederRegistrationApplicationDto scheduleInspection(Long applicationId) {

        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        // 1. Set application status to INSPECTION_SCHEDULED
        ApplicationStatusMaster status = statusRepository.findByStatusCode(INSPECTION_SCHEDULED_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("INSPECTION_SCHEDULED status not found"));

        application.setStatus(status);
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        // 2. Trigger notification with Title = "Inspection Scheduled"
        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Inspection Scheduled", // Heading shown at top of card
                "Your inspection has been scheduled by the Chief Veterinary Officer.",
                "INFO"
        );

        return mapToDto(savedApp);
    }
    /**
     * CVO Verifies Application / Uploads Report.
     */
    @Transactional
    public DogBreederRegistrationApplicationDto verifyByCvo(Long applicationId) {

        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        ApplicationStatusMaster status = statusRepository.findByStatusCode(VERIFIED_BY_CVO_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("VERIFIED_BY_CVO status not found"));

        application.setStatus(status);
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        // NOTIFICATION: Triggered when CVO completes inspection/verification
        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Verified by CVO",
                "Your Dog Breeder Registration application (" + savedApp.getApplicationNumber() + ") inspection report has been verified by CVO.",
                "SUCCESS");

        return mapToDto(savedApp);
    }

    /**
     * CVO list with breeder names populated.
     */
    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto> getCvoForwardedApplications(Integer districtId) {

        if (districtId == null) {
            throw new IllegalArgumentException("CVO district ID is required");
        }

        List<DogBreederRegistrationApplication> applications =
                applicationRepository.findCvoApplications(
                        DOG_BREEDER_ENTITY_TYPE,
                        districtId);

        return applications
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Admin approves final registration.
     */
    @Transactional
    public DogBreederRegistrationApplicationDto approveApplication(Long id) {

        DogBreederRegistrationApplication application =
                getDogBreederApplication(id);

        application.setStatus(
                statusRepository.findByStatusCode(APPROVED_STATUS_CODE)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_APPROVED not found")));

        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        // NOTIFICATION: Triggered when Admin approves application
        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Approved",
                "Congratulations! Your Dog Breeder registration application (" + savedApp.getApplicationNumber() + ") has been approved.",
                "SUCCESS");

        return mapToDto(savedApp);
    }

    /**
     * Admin rejects application.
     */
    @Transactional
    public DogBreederRegistrationApplicationDto rejectApplication(Long id) {

        DogBreederRegistrationApplication application =
                getDogBreederApplication(id);

        application.setStatus(
                statusRepository.findByStatusCode(REJECTED_STATUS_CODE)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_REJECTED not found")));

        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        // NOTIFICATION: Triggered when Admin rejects application
        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Rejected",
                "Your Dog Breeder registration application (" + savedApp.getApplicationNumber() + ") has been rejected.",
                "ERROR");

        return mapToDto(savedApp);
    }

    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto> getMyApplications(String username) {

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole() != null
                && "ADMIN".equalsIgnoreCase(user.getRole().getRoleName());

        List<DogBreederRegistrationApplication> applications;

        if (isAdmin) {
            applications = applicationRepository
                    .findByEntityTypeOrderByIdDesc(DOG_BREEDER_ENTITY_TYPE);
        } else {
            applications = applicationRepository
                    .findByApplicantUserIdAndEntityTypeOrderByIdDesc(
                            user.getId(),
                            DOG_BREEDER_ENTITY_TYPE);
        }

        return applications.stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Finds and validates a Dog Breeder application.
     */
    private DogBreederRegistrationApplication getDogBreederApplication(Long applicationId) {

        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID is required");
        }

        DogBreederRegistrationApplication application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Dog breeder application not found"));

        if (!DOG_BREEDER_ENTITY_TYPE.equals(application.getEntityType())) {
            throw new RuntimeException("Invalid dog breeder application");
        }

        return application;
    }
    @Transactional
    public DogBreederRegistrationApplicationDto resubmitApplication(
            DogBreederRegistrationApplicationResubmissionDto dto) {

        DogBreederRegistrationApplication application =
                applicationRepository.findById(dto.getApplicationId())
                        .orElseThrow(() -> new RuntimeException("Application not found"));

        ApplicationStatusMaster status = statusRepository
                .findByStatusCode("RESUBMITTED")
                .orElseThrow(() -> new RuntimeException("RESUBMITTED status not found"));

        application.setStatus(status);
        application.setSubmittedAt(LocalDateTime.now());

        DogBreederRegistrationApplication saved =
                applicationRepository.save(application);

        return mapToDto(saved);
    }
}