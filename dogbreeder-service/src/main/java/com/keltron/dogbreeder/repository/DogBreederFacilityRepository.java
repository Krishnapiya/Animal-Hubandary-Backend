package com.keltron.dogbreeder.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederFacility;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederFacilityRepository
        extends AbstractRepository<DogBreederFacility, Long> {

    Optional<DogBreederFacility> findByDogBreederDetail_Id(Long dogBreederDetailId);
}