package com.keltron.dogbreeder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keltron.utility.jpa.entity.RegistrationApplication;

public interface DogBreederRegistrationApplicationRepository
        extends JpaRepository<RegistrationApplication, Long> {

    Optional<RegistrationApplication>
        findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                Long applicantUserId,
                String entityType,
                String statusCode);

    Optional<RegistrationApplication> findByIdAndApplicantUserId(
            Long id,
            Long applicantUserId);
}