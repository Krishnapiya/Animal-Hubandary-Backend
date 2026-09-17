package com.keltron.citizen.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.keltron.citizen.entity.ComplaintRegistrationStatusHistory;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface ComplaintRegistrationStatusHistoryRepository
        extends AbstractRepository<ComplaintRegistrationStatusHistory, Long> {

    List<ComplaintRegistrationStatusHistory>
            findByComplaint_IdOrderByChangedAtDesc(Long complaintId);

    List<ComplaintRegistrationStatusHistory>
            findByComplaint_IdOrderByChangedAtAsc(Long complaintId);
}