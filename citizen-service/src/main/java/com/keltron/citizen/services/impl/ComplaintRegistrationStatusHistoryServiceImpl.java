package com.keltron.citizen.services.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.citizen.dto.ComplaintRegistrationStatusHistoryDto;
import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.citizen.entity.ComplaintRegistrationStatusHistory;
import com.keltron.citizen.repository.ComplaintRegistrationStatusHistoryRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class ComplaintRegistrationStatusHistoryServiceImpl
        extends AbstractJpaService<
                ComplaintRegistrationStatusHistoryDto,
                Long,
                ComplaintRegistrationStatusHistoryRepository,
                ComplaintRegistrationStatusHistory> {

    @Autowired
    private ComplaintRegistrationStatusHistoryRepository historyRepository;

    /**
     * Log Status Change
     */
    @Transactional
    public ComplaintRegistrationStatusHistory logStatusChange(
            Long complaintId,
            String fromStatus,
            String toStatus,
            String changedBy,
            String remarks,
            String actionType) {

        ComplaintRegistrationStatusHistory history =
                new ComplaintRegistrationStatusHistory();

        history.setComplaint(
                new ComplaintRegistration(complaintId));

        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setChangedBy(changedBy);

        history.setChangedAt(
                new Timestamp(System.currentTimeMillis()));

        history.setRemarks(remarks);
        history.setActionType(actionType);

        return historyRepository.save(history);
    }

    /**
     * Latest First
     */
    @Transactional(readOnly = true)
    public List<ComplaintRegistrationStatusHistoryDto>
            getHistoryByComplaintId(Long complaintId) {

        return historyRepository
                .findByComplaint_IdOrderByChangedAtDesc(complaintId)
                .stream()
                .map(ComplaintRegistrationStatusHistory::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Oldest First
     */
    @Transactional(readOnly = true)
    public List<ComplaintRegistrationStatusHistoryDto>
            getHistoryByComplaintIdChronological(Long complaintId) {

        return historyRepository
                .findByComplaint_IdOrderByChangedAtAsc(complaintId)
                .stream()
                .map(ComplaintRegistrationStatusHistory::toDTO)
                .collect(Collectors.toList());
    }
}