package com.keltron.petshop.services.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.petshop.dto.RegistrationApplicationStatusHistoryDto;
import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.entity.RegistrationApplicationStatusHistory;
import com.keltron.petshop.repository.RegistrationApplicationStatusHistoryRepository;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class RegistrationApplicationStatusHistoryServiceImpl extends
        AbstractJpaService<
                RegistrationApplicationStatusHistoryDto,
                Long,
                RegistrationApplicationStatusHistoryRepository,
                RegistrationApplicationStatusHistory> {

    @Autowired
    private RegistrationApplicationStatusHistoryRepository historyRepository;

    /**
     * Log Status Change
     */
    @Transactional
    public RegistrationApplicationStatusHistory logStatusChange(
            Long applicationId,
            ApplicationStatus fromStatus,
            ApplicationStatus toStatus,
            String changedBy,
            String remarks,
            String actionType) {

        RegistrationApplicationStatusHistory history =
                new RegistrationApplicationStatusHistory();

        history.setApplication(
                new PetShopRegistrationApplication(applicationId));

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
    public List<RegistrationApplicationStatusHistoryDto>
            getHistoryByApplicationId(Long applicationId) {

        return historyRepository
                .findByApplication_IdOrderByChangedAtDesc(applicationId)
                .stream()
                .map(RegistrationApplicationStatusHistory::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Oldest First
     */
    @Transactional(readOnly = true)
    public List<RegistrationApplicationStatusHistoryDto>
            getHistoryByApplicationIdChronological(Long applicationId) {

        return historyRepository
                .findByApplication_IdOrderByChangedAtAsc(applicationId)
                .stream()
                .map(RegistrationApplicationStatusHistory::toDTO)
                .collect(Collectors.toList());
    }
    
}