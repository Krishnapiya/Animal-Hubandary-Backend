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
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.dogbreeder.repository.DogBreederDeclarationRepository;
import com.keltron.dogbreeder.repository.DogBreederDetailRepository;
import com.keltron.dogbreeder.repository.DogBreederFacilityRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DogBreederRegistrationApplicationServiceImpl
        extends AbstractJpaService<
                DogBreederRegistrationApplicationDto,
                Long,
                DogBreederRegistrationApplicationRepository,
                DogBreederRegistrationApplication> {

    private static final String DOG_BREEDER_ENTITY_TYPE =
            "DOG_BREEDER";

    private static final String FORWARDED_TO_CVO_STATUS_CODE =
            "FORWARDED_TO_CVO";

    /*
     * Status ID 5 must correspond to FORWARDED_TO_CVO
     * in application_status_master.
     */
    private static final Long FORWARDED_TO_CVO_STATUS_ID = 5L;

    @Autowired
    private DogBreederRegistrationApplicationRepository
            applicationRepository;

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

    /**
     * Admin list: returns all Dog Breeder applications.
     */
    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto>
            getDogBreederApplications() {

        return applicationRepository
                .findByEntityTypeOrderByIdDesc(
                        DOG_BREEDER_ENTITY_TYPE)
                .stream()
                .map(DogBreederRegistrationApplication::toDTO)
                .toList();
    } 

    /**
     * Returns the latest Dog Breeder application ID.
     */
    @Transactional(readOnly = true)
    public Long getLatestDogBreederApplicationId() {

        return applicationRepository
                .findTopByEntityTypeOrderByIdDesc(
                        DOG_BREEDER_ENTITY_TYPE)
                .map(DogBreederRegistrationApplication::getId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No dog breeder application found"));
    }

    /**
     * Returns complete application preview details.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPreview(Long applicationId) {

        DogBreederRegistrationApplication application =
                getDogBreederApplication(applicationId);

        Map<String, Object> response = new HashMap<>();

        response.put(
                "registrationDetails",
                application.toDTO());

        var breederDetail = detailRepository
                .findByApplicationId(applicationId)
                .orElse(null);

        response.put(
                "breederDetails",
                breederDetail != null
                        ? breederDetail.toDTO()
                        : null);

        if (breederDetail != null) {

            Long dogBreederDetailId =
                    breederDetail.getId();

            response.put(
                    "facilityDetails",
                    facilityRepository
                            .findByDogBreederDetail_Id(
                                    dogBreederDetailId)
                            .map(facility ->
                                    facility.toDTO())
                            .orElse(null));

            response.put(
                    "declarationDetails",
                    declarationRepository
                            .findByDogBreederDetail_Id(
                                    dogBreederDetailId)
                            .map(declaration ->
                                    declaration.toDTO())
                            .orElse(null));

            response.put(
                    "breedDetails",
                    breedRepository
                            .findByDogBreederDetail_Id(
                                    dogBreederDetailId)
                            .stream()
                            .map(breed -> breed.toDTO())
                            .toList());

        } else {

            response.put(
                    "facilityDetails",
                    null);

            response.put(
                    "declarationDetails",
                    null);

            response.put(
                    "breedDetails",
                    List.of());
        }

        response.put(
                "documentDetails",
                documentRepository
                        .findByApplication_IdOrderByIdAsc(
                                applicationId)
                        .stream()
                        .map(document ->
                                document.toDTO())
                        .toList());

        return response;
    }

    /**
     * Admin forwards an application to CVO.
     */
    @Transactional
    public String forwardToCvo(Long applicationId) {

        DogBreederRegistrationApplication application =
                getDogBreederApplication(applicationId);

        Long currentStatusId =
                application.getStatus() != null
                        ? application.getStatus().getId()
                        : null;

        String currentStatusCode =
                application.getStatus() != null
                        ? application.getStatus().getStatusCode()
                        : null;

        /*
         * Avoid forwarding the same application again.
         */
        if (FORWARDED_TO_CVO_STATUS_ID.equals(currentStatusId)
                || FORWARDED_TO_CVO_STATUS_CODE.equalsIgnoreCase(
                        currentStatusCode)) {

            return "Application is already forwarded to CVO";
        }

        application.setStatus(
                new ApplicationStatusMaster(
                        FORWARDED_TO_CVO_STATUS_ID));

        application.setForwardedToCvoAt(
                LocalDateTime.now());

        applicationRepository.save(application);

        return "Application forwarded to CVO successfully";
    }

    /**
     * CVO list.
     *
     * Returns only applications where:
     *
     * entityType = DOG_BREEDER
     * statusCode = FORWARDED_TO_CVO
     * districtId = logged-in CVO district
     */
    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto>
            getCvoForwardedApplications(Integer districtId) {

        if (districtId == null) {
            throw new IllegalArgumentException(
                    "CVO district ID is required");
        }

        System.out.println(
                "CVO forwarded list districtId = "
                        + districtId);

        List<DogBreederRegistrationApplication> applications =
                applicationRepository
                        .findByEntityTypeAndStatus_StatusCodeAndDistrict_IdOrderByIdDesc(
                                DOG_BREEDER_ENTITY_TYPE,
                                FORWARDED_TO_CVO_STATUS_CODE,
                                districtId);

        System.out.println(
                "CVO forwarded application count = "
                        + applications.size());

        return applications
                .stream()
                .map(DogBreederRegistrationApplication::toDTO)
                .toList();
    }

    /**
     * Finds and validates a Dog Breeder application.
     */
    private DogBreederRegistrationApplication
            getDogBreederApplication(Long applicationId) {

        if (applicationId == null) {
            throw new IllegalArgumentException(
                    "Application ID is required");
        }

        DogBreederRegistrationApplication application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dog breeder application not found"));

        if (!DOG_BREEDER_ENTITY_TYPE.equals(
                application.getEntityType())) {

            throw new RuntimeException(
                    "Invalid dog breeder application");
        }

        return application;
    }
}