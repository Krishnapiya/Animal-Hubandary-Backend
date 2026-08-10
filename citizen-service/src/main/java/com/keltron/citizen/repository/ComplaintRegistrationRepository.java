package com.keltron.citizen.repository;

import java.util.Optional;

import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface ComplaintRegistrationRepository
        extends AbstractRepository<
                ComplaintRegistration,
                Long> {

    Optional<ComplaintRegistration> findByComplaintNumber(
            String complaintNumber);
}