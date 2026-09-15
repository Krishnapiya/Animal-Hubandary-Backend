package com.keltron.dogbreeder.services.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederApplicationStatusHistoryDto;
import com.keltron.dogbreeder.entity.DogBreederApplicationStatusHistory;
import com.keltron.dogbreeder.repository.DogBreederApplicationStatusHistoryRepository;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DogBreederApplicationStatusHistoryServiceImpl
        extends AbstractJpaService<
                DogBreederApplicationStatusHistoryDto,
                Long,
                DogBreederApplicationStatusHistoryRepository,
                DogBreederApplicationStatusHistory> {

    @Autowired
    private DogBreederApplicationStatusHistoryRepository historyRepository;

    /**
     * Logs a status change entry into awb.dog_breeder_application_status_history.
     * Sets required non-null audit fields (createdAt, createdBy) alongside status info.
     */
    @Transactional
    public DogBreederApplicationStatusHistory logStatusChange(
            Long applicationId,
            ApplicationStatus fromStatus,
            ApplicationStatus toStatus,
            String changedBy,
            String remarks,
            String actionType) {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        DogBreederApplicationStatusHistory history =
                new DogBreederApplicationStatusHistory();

        // 1. Set Application reference
        history.setApplication(
                new RegistrationApplication(applicationId));

        // 2. Set Status transitions
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);

        // 3. Set Action details
        history.setChangedBy(changedBy);
        history.setChangedAt(now);
        history.setRemarks(remarks);
        history.setActionType(actionType);

        // 4. Fix: Set mandatory non-null audit fields to prevent DB constraints errors
        history.setCreatedAt(now);
        history.setCreatedBy(changedBy != null ? changedBy : "SYSTEM");

        return historyRepository.save(history);
    }

    /**
     * Fetches application history in reverse chronological order (newest first).
     */
    @Transactional(readOnly = true)
    public List<DogBreederApplicationStatusHistoryDto> getHistoryByApplicationId(Long applicationId) {
        return historyRepository
                .findByApplicationIdOrderByChangedAtDesc(applicationId)
                .stream()
                .map(DogBreederApplicationStatusHistory::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Fetches application history in chronological order (oldest first for timelines).
     */
    @Transactional(readOnly = true)
    public List<DogBreederApplicationStatusHistoryDto> getHistoryByApplicationIdChronological(Long applicationId) {
        return historyRepository
                .findByApplicationIdOrderByChangedAtAsc(applicationId)
                .stream()
                .map(DogBreederApplicationStatusHistory::toDTO)
                .collect(Collectors.toList());
    }
}