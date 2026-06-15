package com.keltron.petshop.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface RegistrationApplicationRepository
        extends AbstractRepository<RegistrationApplication, Long> {

    List<RegistrationApplication> findByApplicantUserIdOrderByIdDesc(Long applicantUserId);

    Optional<RegistrationApplication> findByIdAndApplicantUserId(Long id, Long applicantUserId);

    Optional<RegistrationApplication>
            findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                    Long applicantUserId,
                    String entityType,
                    String statusCode);
}
