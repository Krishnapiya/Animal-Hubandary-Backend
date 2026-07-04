package com.keltron.dogbreeder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederBreedRepository
        extends AbstractRepository<DogBreederBreed, Long> {

    Optional<DogBreederBreed> findByDogBreederDetail_IdAndBreedNameIgnoreCase(
            Long dogBreederDetailId,
            String breedName
    );

    List<DogBreederBreed> findByDogBreederDetail_Id(Long dogBreederDetailId);
}