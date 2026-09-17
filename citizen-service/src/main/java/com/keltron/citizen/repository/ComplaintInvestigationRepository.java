package com.keltron.citizen.repository;

import java.util.Optional;

import com.keltron.citizen.entity.ComplaintInvestigation;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface ComplaintInvestigationRepository
        extends AbstractRepository<
                ComplaintInvestigation,
                Long> {

    Optional<ComplaintInvestigation> findByComplaint_Id(
            Long complaintId);
}