package com.keltron.admin.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.PaymentStatusMaster;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface PaymentStatusMasterRepository
        extends AbstractRepository<PaymentStatusMaster, Long> {

    Optional<PaymentStatusMaster> findByStatusCodeIgnoreCase(String statusCode);

    Optional<PaymentStatusMaster> findByStatusNameIgnoreCase(String statusName);

}