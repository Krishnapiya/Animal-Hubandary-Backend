package com.keltron.dogbreeder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederRegistrationApplicationRepository
        extends AbstractRepository<
                DogBreederRegistrationApplication,
                Long> {

    Optional<DogBreederRegistrationApplication>
            findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                    Long applicantUserId,
                    String entityType,
                    String statusCode);

    Optional<DogBreederRegistrationApplication>
            findByIdAndApplicantUserId(
                    Long id,
                    Long applicantUserId);

    List<DogBreederRegistrationApplication>
            findByEntityTypeOrderByIdDesc(
                    String entityType);

    Optional<DogBreederRegistrationApplication>
            findTopByEntityTypeOrderByIdDesc(
                    String entityType);

    List<DogBreederRegistrationApplication>
            findByEntityTypeAndStatus_StatusCodeAndDistrict_IdOrderByIdDesc(
                    String entityType,
                    String statusCode,
                    Integer districtId);
}