package com.keltron.admin.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.keltron.utility.beans.dto.*;
import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.admin.repository.AnimalSpeciesRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class AnimalSpeciesServiceImpl extends AbstractJpaService<
        AnimalSpeciesDto,
        Long,
        AnimalSpeciesRepository,
        AnimalSpecies> {
	public List<AnimalSpeciesDto> findBreedsBySpeciesId(Long speciesId) {

        List<AnimalSpecies> breeds =
                repository.findByParentIdAndIsActiveTrue(speciesId);

        return breeds.stream()
                .map(AnimalSpecies::toDTO)
                .collect(Collectors.toList());
    }
}