package com.keltron.dogbreeder.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederDeclaration;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederDeclarationRepository
        extends AbstractRepository<DogBreederDeclaration, Long> {

    Optional<DogBreederDeclaration> findByDogBreederDetail_Id(Long dogBreederDetailId);
}