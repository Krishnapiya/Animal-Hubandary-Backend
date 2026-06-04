package com.keltron.admin.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface ApplicationStatusMasterRepository
        extends AbstractRepository<ApplicationStatusMaster, Long> {

    Optional<ApplicationStatusMaster> findByStatusCodeIgnoreCase(String statusCode);

    Optional<ApplicationStatusMaster> findByStatusNameIgnoreCase(String statusName);

}