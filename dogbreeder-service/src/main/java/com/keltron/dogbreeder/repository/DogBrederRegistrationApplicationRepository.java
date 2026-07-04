package com.keltron.dogbreeder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBrederRegistrationApplicationRepository
        extends AbstractRepository<DogBreederRegistrationApplication, Long> {

    Optional<DogBreederRegistrationApplication>
            findTopByEntityTypeOrderByIdDesc(String entityType);

    List<DogBreederRegistrationApplication>
            findByEntityTypeOrderByIdDesc(String entityType);
}