package com.keltron.admin.repository;

import java.util.List;

import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface AnimalSpeciesRepository
        extends AbstractRepository<AnimalSpecies, Long> {
	List<AnimalSpecies> findByParentIdAndIsActiveTrue(Long parentId);
}