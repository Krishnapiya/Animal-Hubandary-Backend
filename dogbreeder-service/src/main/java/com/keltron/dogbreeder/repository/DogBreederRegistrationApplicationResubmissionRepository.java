package com.keltron.dogbreeder.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplicationResubmission;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederRegistrationApplicationResubmissionRepository
        extends AbstractRepository<
                DogBreederRegistrationApplicationResubmission,
                Long> {

    List<DogBreederRegistrationApplicationResubmission>
            findByApplication_IdOrderByResubmittedAtDesc(
                    Long applicationId);

}