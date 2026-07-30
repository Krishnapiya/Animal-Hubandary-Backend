package com.keltron.dogbreeder.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class DogBreederRegistrationApplicationServiceImpl
        extends AbstractJpaService<
                DogBreederRegistrationApplicationDto,
                Long,
                DogBreederRegistrationApplicationRepository,
                DogBreederRegistrationApplication> {

    private static final String DOG_BREEDER_ENTITY_TYPE = "DOG_BREEDER";

    private static final String DRAFT_STATUS_CODE = "DRAFT";
    private static final String FORWARDED_TO_CVO_STATUS_CODE = "FORWARDED_TO_CVO";

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

        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        Map<String, Object> response = new HashMap<>();

        response.put("registrationDetails", mapToDto(application));

        var breederDetail = detailRepository
                .findByApplicationId(applicationId)
                .orElse(null);

        response.put("breederDetails", breederDetail != null ? breederDetail.toDTO() : null);

        if (breederDetail != null) {
            Long dogBreederDetailId = breederDetail.getId();

            response.put(
                    "facilityDetails",
                    facilityRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .map(facility -> facility.toDTO())
                            .orElse(null));

            response.put(
                    "declarationDetails",
                    declarationRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .map(declaration -> declaration.toDTO())
                            .orElse(null));

            response.put(
                    "breedDetails",
                    breedRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .stream()
                            .map(breed -> breed.toDTO())
                            .toList());
        } else {
            response.put("facilityDetails", null);
            response.put("declarationDetails", null);
            response.put("breedDetails", List.of());
        }

        response.put(
                "documentDetails",
                documentRepository
                        .findByApplication_IdOrderByIdAsc(applicationId)
                        .stream()
                        .map(document -> document.toDTO())
                        .toList());

        return response;
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

        applicationRepository.save(application);

        return "Application forwarded to CVO successfully";
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
    public DogBreederRegistrationApplicationDto submitApplication(Long applicationId) {

        DogBreederRegistrationApplication application = getDogBreederApplication(applicationId);

        if (application.getStatus() == null) {
            throw new RuntimeException("Application status not found.");
        }

        if (!DRAFT_STATUS_CODE.equalsIgnoreCase(application.getStatus().getStatusCode())) {
            throw new RuntimeException("Only Draft applications can be submitted.");
        }

        ApplicationStatusMaster submittedStatus =
                statusRepository.findByStatusCode("SUBMITTED")
                        .orElseThrow(() -> new RuntimeException("SUBMITTED status not found"));

        application.setStatus(submittedStatus);

        applicationRepository.save(application);

        return mapToDto(application);
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

        // Mapping using mapToDto instead of application.toDTO()
        return applications.stream()
                .map(this::mapToDto)
                .toList();
    }
    

}