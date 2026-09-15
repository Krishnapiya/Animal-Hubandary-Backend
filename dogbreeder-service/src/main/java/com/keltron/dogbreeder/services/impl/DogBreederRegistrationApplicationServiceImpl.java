package com.keltron.dogbreeder.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationDto;
import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationResubmissionDto;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplicationResubmission;
import com.keltron.dogbreeder.repository.DogBreederApplicationDocumentRepository;
import com.keltron.dogbreeder.repository.DogBreederApplicationStatusMasterRepository;
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.dogbreeder.repository.DogBreederDeclarationRepository;
import com.keltron.dogbreeder.repository.DogBreederDetailRepository;
import com.keltron.dogbreeder.repository.DogBreederFacilityRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationResubmissionRepository;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DogBreederRegistrationApplicationServiceImpl
        extends AbstractJpaService<
                DogBreederRegistrationApplicationDto,
                Long,
                DogBreederRegistrationApplicationRepository,
                DogBreederRegistrationApplication> {

    private static final String DOG_BREEDER_ENTITY_TYPE = "DOG_BREEDER";

    private static final String DRAFT_STATUS_CODE = ApplicationStatus.DRAFT.name();
    private static final String SUBMITTED_STATUS_CODE = ApplicationStatus.SUBMITTED.name();
    private static final String FORWARDED_TO_CVO_STATUS_CODE = ApplicationStatus.FORWARDED_TO_CVO.name();
    private static final String INSPECTION_SCHEDULED_STATUS_CODE = ApplicationStatus.INSPECTION_SCHEDULED.name();
    private static final String VERIFIED_BY_CVO_STATUS_CODE = ApplicationStatus.VERIFIED_BY_CVO.name();
    private static final String REJECTED_BY_CVO_STATUS_CODE = ApplicationStatus.REJECTED_BY_CVO.name();
    private static final String RESUBMITTED_STATUS_CODE = ApplicationStatus.RESUBMITTED.name();
    private static final String APPROVED_STATUS_CODE = ApplicationStatus.APPLICATION_APPROVED.name();
    private static final String REJECTED_STATUS_CODE = ApplicationStatus.APPLICATION_REJECTED.name();

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

    @Autowired
    private DogBreederApplicationStatusHistoryServiceImpl historyService;

    @Autowired
    private DogBreederRegistrationApplicationResubmissionRepository resubmissionRepository;

    /**
     * Helper to retrieve currently authenticated user's name/username.
     */
    private String getCurrentUsernameOrDefault(String fallbackRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return fallbackRole;
    }

    /**
     * Safely parses database status string into ApplicationStatus enum.
     */
    private ApplicationStatus parseApplicationStatus(DogBreederRegistrationApplication app) {
        if (app.getStatus() == null || app.getStatus().getStatusCode() == null) {
            return null;
        }
        try {
            return ApplicationStatus.valueOf(app.getStatus().getStatusCode());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private DogBreederRegistrationApplicationDto mapToDto(DogBreederRegistrationApplication application) {
        if (application == null) {
            return null;
        }

        DogBreederRegistrationApplicationDto dto = application.toDTO();

        detailRepository.findByApplicationId(application.getId())
                .ifPresentOrElse(
                    detail -> dto.setBreederName(detail.getBreederName()),
                    () -> {
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

    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto> getDogBreederApplications() {
        return applicationRepository
                .findByEntityTypeOrderByIdDesc(DOG_BREEDER_ENTITY_TYPE)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getLatestDogBreederApplicationId() {
        return applicationRepository
                .findTopByEntityTypeOrderByIdDesc(DOG_BREEDER_ENTITY_TYPE)
                .map(DogBreederRegistrationApplication::getId)
                .orElseThrow(() -> new RuntimeException("No dog breeder application found"));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPreview(Long applicationId) {
        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        Map<String, Object> response = new HashMap<>();
        response.put("registrationDetails", mapToDto(application));

        var breederDetail = detailRepository.findByApplicationId(applicationId).orElse(null);
        response.put("breederDetails", breederDetail != null ? breederDetail.toDTO() : null);

        if (breederDetail != null) {
            Long dogBreederDetailId = breederDetail.getId();
            response.put("facilityDetails", facilityRepository.findByDogBreederDetail_Id(dogBreederDetailId).map(f -> f.toDTO()).orElse(null));
            response.put("declarationDetails", declarationRepository.findByDogBreederDetail_Id(dogBreederDetailId).map(d -> d.toDTO()).orElse(null));
            response.put("breedDetails", breedRepository.findByDogBreederDetail_Id(dogBreederDetailId).stream().map(b -> b.toDTO()).toList());
        } else {
            response.put("facilityDetails", null);
            response.put("declarationDetails", null);
            response.put("breedDetails", List.of());
        }

        List<?> documents = documentRepository.findByApplication_IdOrderByIdAsc(applicationId).stream().map(doc -> doc.toDTO()).toList();
        response.put("documentDetails", documents);
        response.put("supportingDocuments", documents);

        return response;
    }

    /**
     * Step 1: Breeder Submits Application
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

        ApplicationStatus fromStatus = parseApplicationStatus(application);

        ApplicationStatusMaster submittedStatus = statusRepository.findByStatusCode(SUBMITTED_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("SUBMITTED status not found"));

        application.setStatus(submittedStatus);
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.SUBMITTED,
                getCurrentUsernameOrDefault("BREEDER"),
                "Application submitted successfully",
                "SUBMIT"
        );

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
     * Step 2: Admin Forwards to CVO
     */
    @Transactional
    public String forwardToCvo(Long applicationId) {
        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        if (application.getStatus() != null &&
            FORWARDED_TO_CVO_STATUS_CODE.equalsIgnoreCase(application.getStatus().getStatusCode())) {
            return "Application is already forwarded to CVO";
        }

        ApplicationStatus fromStatus = parseApplicationStatus(application);

        ApplicationStatusMaster forwardedStatus = statusRepository.findByStatusCode(FORWARDED_TO_CVO_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("FORWARDED_TO_CVO status not found"));

        application.setStatus(forwardedStatus);
        application.setForwardedToCvoAt(LocalDateTime.now());
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.FORWARDED_TO_CVO,
                getCurrentUsernameOrDefault("ADMIN"),
                "Application forwarded to CVO for inspection",
                "FORWARD"
        );

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
     * Step 3: CVO Schedules Inspection
     */
    @Transactional
    public DogBreederRegistrationApplicationDto scheduleInspection(Long applicationId) {
        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);
        ApplicationStatus fromStatus = parseApplicationStatus(application);

        ApplicationStatusMaster status = statusRepository.findByStatusCode(INSPECTION_SCHEDULED_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("INSPECTION_SCHEDULED status not found"));

        application.setStatus(status);
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.INSPECTION_SCHEDULED,
                getCurrentUsernameOrDefault("CVO"),
                "Inspection scheduled by Chief Veterinary Officer",
                "SCHEDULE_INSPECTION"
        );

        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Inspection Scheduled",
                "Your inspection has been scheduled by the Chief Veterinary Officer.",
                "INFO"
        );

        return mapToDto(savedApp);
    }

    /**
     * Step 4: CVO Verifies Inspection
     */
    @Transactional
    public DogBreederRegistrationApplicationDto verifyByCvo(Long applicationId) {
        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);
        ApplicationStatus fromStatus = parseApplicationStatus(application);

        ApplicationStatusMaster status = statusRepository.findByStatusCode(VERIFIED_BY_CVO_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("VERIFIED_BY_CVO status not found"));

        application.setStatus(status);
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.VERIFIED_BY_CVO,
                getCurrentUsernameOrDefault("CVO"),
                "Inspection report verified by CVO",
                "VERIFY"
        );

        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Verified by CVO",
                "Your Dog Breeder Registration application inspection report has been verified by CVO.",
                "SUCCESS");

        return mapToDto(savedApp);
    }

    /**
     * Step 5: Breeder Resubmits Application
     */
    @Transactional
    public DogBreederRegistrationApplicationDto resubmitApplication(DogBreederRegistrationApplicationResubmissionDto dto) {
        DogBreederRegistrationApplication application = getDogBreederApplication(dto.getApplicationId());
        ApplicationStatus fromStatus = parseApplicationStatus(application);

        DogBreederRegistrationApplicationResubmission resubmission = dto.toEntity();
        resubmission.setApplication(application);
        resubmission.setResubmittedAt(LocalDateTime.now());
        resubmission.setResubmittedBy(application.getApplicantUserId());
        resubmissionRepository.save(resubmission);

        ApplicationStatusMaster status = statusRepository.findByStatusCode(RESUBMITTED_STATUS_CODE)
                .orElseThrow(() -> new RuntimeException("RESUBMITTED status not found"));

        application.setStatus(status);
        application.setSubmittedAt(LocalDateTime.now());
        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.RESUBMITTED,
                getCurrentUsernameOrDefault("BREEDER"),
                "Application resubmitted by breeder",
                "RESUBMIT"
        );

        notificationService.createNotification(
                savedApp.getApplicantUserId(),
                DOG_BREEDER_ENTITY_TYPE,
                savedApp.getId(),
                "Application Resubmitted",
                "Your Dog Breeder application has been resubmitted successfully.",
                "INFO");

        return mapToDto(savedApp);
    }

    /**
     * Step 6: Admin Approves Application
     */
    @Transactional
    public DogBreederRegistrationApplicationDto approveApplication(Long id) {
        DogBreederRegistrationApplication application = getDogBreederApplication(id);
        ApplicationStatus fromStatus = parseApplicationStatus(application);

        application.setStatus(
                statusRepository.findByStatusCode(APPROVED_STATUS_CODE)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status APPLICATION_APPROVED not found")));

        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.APPLICATION_APPROVED,
                getCurrentUsernameOrDefault("ADMIN"),
                "Application approved by Admin",
                "APPROVE"
        );

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
     * Step 7: Admin Rejects Application
     */
    @Transactional
    public DogBreederRegistrationApplicationDto rejectApplication(Long id) {
        DogBreederRegistrationApplication application = getDogBreederApplication(id);
        ApplicationStatus fromStatus = parseApplicationStatus(application);

        application.setStatus(
                statusRepository.findByStatusCode(REJECTED_STATUS_CODE)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status APPLICATION_REJECTED not found")));

        DogBreederRegistrationApplication savedApp = applicationRepository.save(application);

        historyService.logStatusChange(
                savedApp.getId(),
                fromStatus,
                ApplicationStatus.APPLICATION_REJECTED,
                getCurrentUsernameOrDefault("ADMIN"),
                "Application rejected by Admin",
                "REJECT"
        );

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
    public List<DogBreederRegistrationApplicationDto> getCvoForwardedApplications(Integer districtId) {
        if (districtId == null) {
            throw new IllegalArgumentException("CVO district ID is required");
        }

        List<DogBreederRegistrationApplication> applications =
                applicationRepository.findCvoApplications(DOG_BREEDER_ENTITY_TYPE, districtId);

        return applications.stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto> getMyApplications(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getRoleName());

        List<DogBreederRegistrationApplication> applications;

        if (isAdmin) {
            applications = applicationRepository.findByEntityTypeOrderByIdDesc(DOG_BREEDER_ENTITY_TYPE);
        } else {
            applications = applicationRepository.findByApplicantUserIdAndEntityTypeOrderByIdDesc(
                    user.getId(), DOG_BREEDER_ENTITY_TYPE);
        }

        return applications.stream().map(this::mapToDto).toList();
    }

    private DogBreederRegistrationApplication getDogBreederApplication(Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID is required");
        }

        DogBreederRegistrationApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Dog breeder application not found"));

        if (!DOG_BREEDER_ENTITY_TYPE.equals(application.getEntityType())) {
            throw new RuntimeException("Invalid dog breeder application");
        }

        return application;
    }
}