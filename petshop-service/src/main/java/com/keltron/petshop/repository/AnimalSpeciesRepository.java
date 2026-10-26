package com.keltron.petshop.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface AnimalSpeciesRepository extends AbstractRepository<AnimalSpecies, Long> {

    Optional<AnimalSpecies> findBySpeciesNameIgnoreCase(String speciesName);
}

