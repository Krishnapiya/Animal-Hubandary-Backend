package com.keltron.petshop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.keltron.petshop.entity.RegistrationApplicationResubmission;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface RegistrationApplicationResubmissionRepository
        extends AbstractRepository<
                RegistrationApplicationResubmission,
                Long> {

    List<RegistrationApplicationResubmission>
            findByApplication_IdOrderByResubmittedAtDesc(
                    Long applicationId);

}