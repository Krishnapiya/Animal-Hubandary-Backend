package com.keltron.admin.services.impl;

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
}