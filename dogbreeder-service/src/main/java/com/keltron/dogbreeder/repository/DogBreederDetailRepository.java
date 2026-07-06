package com.keltron.dogbreeder.repository;

import java.util.Optional;

import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface DogBreederDetailRepository
        extends AbstractRepository<DogBreederDetail, Long> {

    Optional<DogBreederDetail> findByApplicationId(Long applicationId);
    
    Optional<DogBreederDetail>
    findTopByApplicationIdIsNotNullOrderByApplicationIdDesc();
}