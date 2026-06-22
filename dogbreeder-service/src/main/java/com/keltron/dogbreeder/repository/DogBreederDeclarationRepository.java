package com.keltron.dogbreeder.repository;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederDeclaration;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederDeclarationRepository
extends AbstractRepository<DogBreederDeclaration, Long> {
}