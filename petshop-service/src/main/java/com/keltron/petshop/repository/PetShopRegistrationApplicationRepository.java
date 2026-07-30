package com.keltron.petshop.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface PetShopRegistrationApplicationRepository
        extends AbstractRepository<
                PetShopRegistrationApplication,
                Long> {

    Optional<PetShopRegistrationApplication>
            findByApplicationNumber(
                    String applicationNumber);

    List<PetShopRegistrationApplication>
            findByApplicantUserIdOrderByIdDesc(
                    Long applicantUserId);

    Optional<PetShopRegistrationApplication>
            findByIdAndApplicantUserId(
                    Long id,
                    Long applicantUserId);

    Optional<PetShopRegistrationApplication>
            findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                    Long applicantUserId,
                    String entityType,
                    String statusCode);
    
    List<PetShopRegistrationApplication> findByCvOfficeIdOrderByIdDesc(
            Long cvOfficeId);
    
    List<PetShopRegistrationApplication> findByCvOfficeId(Long cvOfficeId);
    List<PetShopRegistrationApplication>
    findByEntityTypeAndStatus_StatusCodeInOrderByIdDesc(
            String entityType,
            List<String> statusCodes);
    List<PetShopRegistrationApplication>
    findByEntityTypeAndStatus_StatusCodeAndDistrict_IdOrderByIdDesc(
            String entityType,
            String statusCode,
            Integer districtId);
}