package com.keltron.dogbreeder.repository;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederBreedRepository extends AbstractRepository<DogBreederBreed, Long> {

}