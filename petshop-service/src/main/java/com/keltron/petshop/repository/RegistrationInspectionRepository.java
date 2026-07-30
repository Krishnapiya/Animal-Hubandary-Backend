package com.keltron.petshop.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.petshop.entity.RegistrationInspection;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface RegistrationInspectionRepository
        extends AbstractRepository<
                RegistrationInspection,
                Long> {

    Optional<RegistrationInspection>
            findByApplication_Id(
                    Long applicationId);

    List<RegistrationInspection>
            findAllByOrderByIdDesc();

    List<RegistrationInspection>
            findByApplication_ApplicantUserIdOrderByIdDesc(
                    Long applicantUserId);
}