package com.keltron.dogbreeder.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.dogbreeder.entity.DogBreederRegistrationInspection;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface DogBreederRegistrationInspectionRepository
        extends AbstractRepository<
                DogBreederRegistrationInspection,
                Long> {

    Optional<DogBreederRegistrationInspection>
            findByApplication_Id(
                    Long applicationId);

    List<DogBreederRegistrationInspection>
            findAllByOrderByIdDesc();

    List<DogBreederRegistrationInspection>
            findByApplication_ApplicantUserIdOrderByIdDesc(
                    Long applicantUserId);
}