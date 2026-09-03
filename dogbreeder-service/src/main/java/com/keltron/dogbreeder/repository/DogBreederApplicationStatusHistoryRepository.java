package com.keltron.dogbreeder.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.keltron.dogbreeder.entity.DogBreederApplicationStatusHistory;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederApplicationStatusHistoryRepository
        extends AbstractRepository<DogBreederApplicationStatusHistory, Long> {

    List<DogBreederApplicationStatusHistory> findByApplicationIdOrderByChangedAtDesc(Long applicationId);

    List<DogBreederApplicationStatusHistory> findByApplicationIdOrderByChangedAtAsc(Long applicationId);
}