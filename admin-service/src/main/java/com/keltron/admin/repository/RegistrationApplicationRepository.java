package com.keltron.admin.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface RegistrationApplicationRepository
        extends AbstractRepository<
                RegistrationApplication,
                Long> {

    Optional<RegistrationApplication>
            findByApplicationNumber(
                    String applicationNumber);
}