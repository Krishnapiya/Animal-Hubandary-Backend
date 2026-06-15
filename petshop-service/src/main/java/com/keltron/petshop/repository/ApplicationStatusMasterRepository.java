package com.keltron.petshop.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface ApplicationStatusMasterRepository
        extends AbstractRepository<ApplicationStatusMaster, Long> {

    Optional<ApplicationStatusMaster> findByStatusCode(String statusCode);
}
