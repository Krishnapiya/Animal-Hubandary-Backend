package com.keltron.dogbreeder.services.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.dogbreeder.dto.DogBreederRegistrationDraftDto;
import com.keltron.dogbreeder.dto.DogBreederRegistrationResponseDto;
import com.keltron.dogbreeder.dto.DogBreederRegistrationStep1Dto;
import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.dogbreeder.entity.DogBreederDeclaration;
import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.dogbreeder.entity.DogBreederFacility;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.repository.DogBreederApplicationStatusMasterRepository;
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.dogbreeder.repository.DogBreederDeclarationRepository;
import com.keltron.dogbreeder.repository.DogBreederDetailRepository;
import com.keltron.dogbreeder.repository.DogBreederFacilityRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.jpa.entity.District;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DogBreederOwnerApplicationService {

    private static final String DRAFT_STATUS = "DRAFT";
    private static final String ENTITY_TYPE = "DOG_BREEDER";
    private static final String APPLICATION_KIND = "NEW";

    private final DogBreederRegistrationApplicationRepository applicationRepository;
    private final DogBreederApplicationStatusMasterRepository statusRepository;
    private final DogBreederDetailRepository detailRepository;
    private final DogBreederFacilityRepository facilityRepository;
    private final DogBreederBreedRepository breedRepository;
    private final DogBreederDeclarationRepository declarationRepository;
    private final UsersRepository usersRepository;

    public Map<String, String> validateStep1(DogBreederRegistrationStep1Dto dto) {

        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("detail", "Request body is required");
            return errors;
        }

        if (dto.getDistrictId() == null) {
            errors.put("districtId", "District is required");
        }

        if (isBlank(dto.getBreederName())) {
            errors.put("breederName", "Breeder name is required");
        }

        if (isBlank(dto.getAddressLine1())) {
            errors.put("addressLine1", "Address is required");
        }

        if (isBlank(dto.getContactMobile())) {
            errors.put("contactMobile", "Mobile number is required");
        }

        if (isBlank(dto.getContactEmail())) {
            errors.put("contactEmail", "Email is required");
        }

        return errors;
    }

    @Transactional(readOnly = true)
    public DogBreederRegistrationDraftDto getDraftStep1() {

        Long userId = currentUserId();

        DogBreederRegistrationApplication application = applicationRepository
                .findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                        userId,
                        ENTITY_TYPE,
                        DRAFT_STATUS
                )
                .orElse(null);

        if (application == null) {
            return null;
        }

        DogBreederDetail detail = detailRepository
                .findByApplicationId(application.getId())
                .orElse(null);

        if (detail == null) {
            return null;
        }

        DogBreederRegistrationDraftDto draft = new DogBreederRegistrationDraftDto();

        draft.setApplicationId(application.getId());
        draft.setStatusCode(DRAFT_STATUS);

        if (application.getDistrict() != null) {
            draft.setDistrictId(application.getDistrict().getId());
            draft.setDistrictName(application.getDistrict().getName());
        }

        draft.setBreederName(detail.getBreederName());
        draft.setAddressLine1(detail.getAddressLine1());
        draft.setAddressLine2(detail.getAddressLine2());
        draft.setCity(detail.getCity());
        draft.setPincode(detail.getPincode());
        draft.setContactMobile(detail.getContactMobile());
        draft.setContactEmail(detail.getContactEmail());
        draft.setFacilityDetails(detail.getFacilityDetails());
        draft.setTotalDogsCount(detail.getTotalDogsCount());

        return draft;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDraft() {

        Long userId = currentUserId();

        DogBreederRegistrationApplication application = applicationRepository
                .findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                        userId,
                        ENTITY_TYPE,
                        DRAFT_STATUS
                )
                .orElse(null);

        if (application == null) {
            return null;
        }

        Map<String, Object> draft = new HashMap<>();

        draft.put("applicationId", application.getId());
        draft.put("applicationNumber", application.getApplicationNumber());
        draft.put("statusCode", DRAFT_STATUS);

        if (application.getDistrict() != null) {
            draft.put("districtId", application.getDistrict().getId());
            draft.put("districtName", application.getDistrict().getName());
        }

        int currentStep = 0;

        DogBreederDetail detail = detailRepository
                .findByApplicationId(application.getId())
                .orElse(null);

        if (detail != null) {

            currentStep = 1;

            draft.put("dogBreederDetailId", detail.getId());
            draft.put("dogBreederDetail", detail.toDTO());

            draft.put("breederName", detail.getBreederName());
            draft.put("addressLine1", detail.getAddressLine1());
            draft.put("addressLine2", detail.getAddressLine2());
            draft.put("city", detail.getCity());
            draft.put("pincode", detail.getPincode());
            draft.put("contactMobile", detail.getContactMobile());
            draft.put("contactEmail", detail.getContactEmail());
            draft.put("facilityDetails", detail.getFacilityDetails());
            draft.put("totalDogsCount", detail.getTotalDogsCount());

            DogBreederFacility facility = facilityRepository
                    .findByDogBreederDetail_Id(detail.getId())
                    .orElse(null);

            if (facility != null) {
                currentStep = 2;
                draft.put("dogBreederFacility", facility.toDTO());
            }

            List<DogBreederBreed> breeds =
                    breedRepository.findByDogBreederDetail_Id(detail.getId());

            if (breeds != null && !breeds.isEmpty()) {
                currentStep = 3;
                draft.put(
                        "dogBreederBreeds",
                        breeds.stream()
                                .map(DogBreederBreed::toDTO)
                                .toList()
                );
            }

            DogBreederDeclaration declaration = declarationRepository
                    .findByDogBreederDetail_Id(detail.getId())
                    .orElse(null);

            if (declaration != null) {
                currentStep = 4;
                draft.put("dogBreederDeclaration", declaration.toDTO());
            }
        }

        draft.put("currentStep", currentStep);

        return draft;
    }

    @Transactional
    public DogBreederRegistrationResponseDto saveStep1(
            DogBreederRegistrationStep1Dto dto) {

        Map<String, String> errors = validateStep1(dto);

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errors.toString()
            );
        }

        Long userId = currentUserId();

        ApplicationStatusMaster draftStatus =
                statusRepository.findByStatusCode(DRAFT_STATUS)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "DRAFT status is not configured"
                                )
                        );

        DogBreederRegistrationApplication application;

        if (dto.getApplicationId() != null) {
            application = applicationRepository
                    .findByIdAndApplicantUserId(dto.getApplicationId(), userId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Application not found"
                            )
                    );
        } else {
            application = new DogBreederRegistrationApplication();
            application.setEntityType(ENTITY_TYPE);
            application.setApplicationKind(APPLICATION_KIND);
            application.setApplicantUserId(userId);
        }

        application.setStatus(draftStatus);
        application.setDistrict(new District(dto.getDistrictId()));

        application = applicationRepository.save(application);

        if (application.getApplicationNumber() == null
                || application.getApplicationNumber().isBlank()) {

            application.setApplicationNumber(
                    generateApplicationNumber(application.getId())
            );

            application = applicationRepository.save(application);
        }

        DogBreederDetail detail = detailRepository
                .findByApplicationId(application.getId())
                .orElseGet(DogBreederDetail::new);

        detail.setApplicationId(application.getId());
        detail.setBreederName(dto.getBreederName().trim());
        detail.setAddressLine1(dto.getAddressLine1().trim());
        detail.setAddressLine2(dto.getAddressLine2());
        detail.setCity(dto.getCity());
        detail.setPincode(dto.getPincode());
        detail.setContactMobile(dto.getContactMobile().trim());
        detail.setContactEmail(dto.getContactEmail().trim());
        detail.setFacilityDetails(dto.getFacilityDetails());
        detail.setTotalDogsCount(dto.getTotalDogsCount());

        detail = detailRepository.save(detail);

        DogBreederRegistrationResponseDto response =
                new DogBreederRegistrationResponseDto();

        response.setApplicationId(application.getId());
        response.setDetailId(detail.getId());
        response.setStatusCode(DRAFT_STATUS);
        response.setDetail(detail.toDTO());

        return response;
    }

    private String generateApplicationNumber(Long applicationId) {
        int year = LocalDate.now().getYear();

        return String.format(
                "AWB-DB-%d-%03d",
                year,
                applicationId
        );
    }

    private Long currentUserId() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required"
            );
        }

        String username = auth.getName();

        if (auth.getPrincipal() instanceof Jwt jwt
                && jwt.getSubject() != null) {
            username = jwt.getSubject();
        }

        return usersRepository.findByUsername(username)
                .map(Users::getId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}