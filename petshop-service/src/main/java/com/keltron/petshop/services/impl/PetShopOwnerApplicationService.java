package com.keltron.petshop.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.petshop.dto.PetShopRegistrationDraftDto;
import com.keltron.petshop.dto.PetShopRegistrationResponseDto;
import com.keltron.petshop.dto.PetShopRegistrationStep1Dto;
import com.keltron.petshop.entity.PetShopDetail;
import com.keltron.petshop.repository.ApplicationStatusMasterRepository;
import com.keltron.petshop.repository.PetShopDetailRepository;
import com.keltron.petshop.repository.RegistrationApplicationRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.jpa.entity.District;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetShopOwnerApplicationService {

    private static final String DRAFT_STATUS = "DRAFT";

    private final RegistrationApplicationRepository applicationRepository;
    private final ApplicationStatusMasterRepository statusRepository;
    private final PetShopDetailRepository detailRepository;
    private final UsersRepository usersRepository;

    public Map<String, String> validateStep1(PetShopRegistrationStep1Dto dto) {
        Map<String, String> errors = new HashMap<>();
        if (dto == null) {
            errors.put("detail", "Request body is required");
            return errors;
        }
        if (dto.getDistrictId() == null) {
            errors.put("districtId", "District is required");
        }
        if (isBlank(dto.getShopName())) {
            errors.put("shopName", "Pet shop name is required");
        }
        if (isBlank(dto.getOwnerName())) {
            errors.put("ownerName", "Owner name is required");
        }
        if (isBlank(dto.getShopAddressLine1())) {
            errors.put("shopAddressLine1", "Shop address is required");
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
    public PetShopRegistrationDraftDto getDraftStep1() {
        Long userId = currentUserId();
        RegistrationApplication application = applicationRepository
                .findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                        userId, "PET_SHOP", DRAFT_STATUS)
                .orElse(null);
        if (application == null) {
            return null;
        }

        PetShopDetail detail = detailRepository
                .findByApplicationId(application.getId())
                .orElse(null);
        if (detail == null) {
            return null;
        }

        PetShopRegistrationDraftDto draft = new PetShopRegistrationDraftDto();
        draft.setApplicationId(application.getId());
        draft.setDetailId(detail.getId());
        draft.setStatusCode(DRAFT_STATUS);
        if (application.getDistrict() != null) {
            draft.setDistrictId(application.getDistrict().getId());
            draft.setDistrictName(application.getDistrict().getName());
        }
        draft.setShopName(detail.getShopName());
        draft.setOwnerName(detail.getOwnerName());
        draft.setShopAddressLine1(detail.getAddressLine1());
        draft.setShopAddressLine2(detail.getAddressLine2());
        draft.setShopCity(detail.getCity());
        draft.setShopPincode(detail.getPincode());
        draft.setContactMobile(detail.getContactMobile());
        draft.setContactEmail(detail.getContactEmail());
        draft.setLatitude(detail.getLatitude());
        draft.setLongitude(detail.getLongitude());
        applyRegistrationDetails(draft, detail.getRegistrationDetails());
        return draft;
    }

    @Transactional
    public PetShopRegistrationResponseDto saveStep1(PetShopRegistrationStep1Dto dto) {
        Map<String, String> errors = validateStep1(dto);
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        }

        Long userId = currentUserId();
        ApplicationStatusMaster draftStatus = statusRepository.findByStatusCode(DRAFT_STATUS)
                .orElseThrow(() -> new IllegalStateException("DRAFT status is not configured"));

        RegistrationApplication application;
        if (dto.getApplicationId() != null) {
            application = applicationRepository
                    .findByIdAndApplicantUserId(dto.getApplicationId(), userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Application not found"));
        } else {
            application = new RegistrationApplication();
            application.setEntityType("PET_SHOP");
            application.setApplicationKind("NEW");
            application.setApplicantUserId(userId);
        }

        application.setStatus(draftStatus);
        application.setDistrict(new District(dto.getDistrictId()));
        application = applicationRepository.save(application);

        PetShopDetail detail = detailRepository
                .findByApplicationId(application.getId())
                .orElseGet(PetShopDetail::new);

        detail.setApplicationId(application.getId());
        detail.setShopName(dto.getShopName().trim());
        detail.setOwnerName(dto.getOwnerName().trim());
        detail.setAddressLine1(resolveLine(dto.getShopAddressLine1()));
        detail.setAddressLine2(dto.getShopAddressLine2());
        detail.setCity(dto.getShopCity());
        detail.setPincode(dto.getShopPincode());
        detail.setContactMobile(dto.getContactMobile().trim());
        detail.setContactEmail(dto.getContactEmail().trim());
        detail.setLatitude(dto.getLatitude());
        detail.setLongitude(dto.getLongitude());

        StringBuilder extra = new StringBuilder();
        appendLine(extra, "Owner residential (r/o)", dto.getOwnerResidentialAddress());
        appendLine(extra, "Owner address", dto.getOwnerAddressLine1());
        appendLine(extra, "Owner address line 2", dto.getOwnerAddressLine2());
        appendLine(extra, "Owner city", dto.getOwnerCity());
        appendLine(extra, "Owner pincode", dto.getOwnerPincode());
        appendLine(extra, "Owner office address", dto.getOwnerOfficeAddress());
        appendLine(extra, "Landline", dto.getContactLandline());
        if (!extra.isEmpty()) {
            detail.setRegistrationDetails(extra.toString().trim());
        }

        detail = detailRepository.save(detail);

        PetShopRegistrationResponseDto response = new PetShopRegistrationResponseDto();
        response.setApplicationId(application.getId());
        response.setDetailId(detail.getId());
        response.setStatusCode(DRAFT_STATUS);
        response.setDetail(detail.toDTO());
        return response;
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        String username = auth.getName();
        if (auth.getPrincipal() instanceof Jwt jwt && jwt.getSubject() != null) {
            username = jwt.getSubject();
        }
        return usersRepository.findByUsername(username)
                .map(Users::getId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String resolveLine(String value) {
        return value == null ? "" : value.trim();
    }

    private void appendLine(StringBuilder builder, String label, String value) {
        if (!isBlank(value)) {
            if (!builder.isEmpty()) {
                builder.append('\n');
            }
            builder.append(label).append(": ").append(value.trim());
        }
    }

    private void applyRegistrationDetails(
            PetShopRegistrationDraftDto draft,
            String registrationDetails) {

        if (isBlank(registrationDetails)) {
            return;
        }
        for (String line : registrationDetails.split("\n")) {
            int separator = line.indexOf(':');
            if (separator < 0) {
                continue;
            }
            String label = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            switch (label) {
                case "Owner residential (r/o)" ->
                        draft.setOwnerResidentialAddress(value);
                case "Owner address" ->
                        draft.setOwnerAddressLine1(value);
                case "Owner address line 2" ->
                        draft.setOwnerAddressLine2(value);
                case "Owner city" ->
                        draft.setOwnerCity(value);
                case "Owner pincode" ->
                        draft.setOwnerPincode(value);
                case "Owner office address" ->
                        draft.setOwnerOfficeAddress(value);
                case "Landline" ->
                        draft.setContactLandline(value);
                default -> { }
            }
        }
    }
}
