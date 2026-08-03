package com.keltron.petshop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.keltron.petshop.entity.RegistrationApplicationStatusHistory;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface RegistrationApplicationStatusHistoryRepository
        extends AbstractRepository<RegistrationApplicationStatusHistory, Long> {

    List<RegistrationApplicationStatusHistory>
            findByApplication_IdOrderByChangedAtDesc(Long applicationId);

    List<RegistrationApplicationStatusHistory>
            findByApplication_IdOrderByChangedAtAsc(Long applicationId);
}