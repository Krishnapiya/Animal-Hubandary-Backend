package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederBreedDto;
import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class DogBreederBreedServiceImpl
        extends AbstractJpaService<
                DogBreederBreedDto,
                Long,
                DogBreederBreedRepository,
                DogBreederBreed> {

    @Autowired
    private DogBreederBreedRepository dogBreederBreedRepository;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateExcel(
            ExcelExportRequest request) {

        List<DogBreederBreed> breeds =
                dogBreederBreedRepository.findAll();

        List<DogBreederBreedDto> dtos = breeds.stream()
                .map(DogBreederBreed::toDTO)
                .toList();

        return ExcelExportUtil.generateExcel(
                dtos,
                request.getXls_config()
        );
    }

    @Override
    @WriteTransactional
    public DogBreederBreed save(DogBreederBreedDto dto) {

        /*
         * 1. If explicit ID is provided, update that record directly
         */
        if (dto.getId() != null && dto.getId() > 0) {
            DogBreederBreed existingById = dogBreederBreedRepository
                    .findById(dto.getId())
                    .orElse(null);

            if (existingById != null) {
                updateEditableFields(existingById, dto);
                return dogBreederBreedRepository.save(existingById);
            }
        }

        Long dogBreederDetailId = null;

        if (dto.getDogBreederDetail() != null
                && dto.getDogBreederDetail().getId() != null) {

            dogBreederDetailId =
                    dto.getDogBreederDetail().getId();
        }

        /*
         * 2. Check whether the same breed already exists
         * for this breeder detail.
         */
        if (dogBreederDetailId != null
                && dto.getBreedName() != null
                && !dto.getBreedName().isBlank()) {

            DogBreederBreed existingBreed =
                    dogBreederBreedRepository
                            .findByDogBreederDetail_IdAndBreedNameIgnoreCase(
                                    dogBreederDetailId,
                                    dto.getBreedName().trim()
                            )
                            .orElse(null);

            if (existingBreed != null) {

                updateEditableFields(
                        existingBreed,
                        dto
                );

                return dogBreederBreedRepository.save(
                        existingBreed
                );
            }
        }

        return super.save(dto);
    }

    @Override
    @WriteTransactional
    public DogBreederBreed update(
            Long id,
            DogBreederBreedDto dto) {

        DogBreederBreed existingBreed =
                dogBreederBreedRepository
                        .findById(id)
                        .orElse(null);

        if (existingBreed != null) {

            updateEditableFields(
                    existingBreed,
                    dto
            );

            return dogBreederBreedRepository.save(
                    existingBreed
            );
        }

        return super.update(id, dto);
    }

    private void updateEditableFields(
            DogBreederBreed breed,
            DogBreederBreedDto dto) {

        if (ValidationUtils.isValid(dto.getBreedName())) {
            breed.setBreedName(
                    dto.getBreedName().trim()
            );
        }

        if (dto.getDogCount() != null) {
            breed.setDogCount(
                    dto.getDogCount()
            );
        }

        if (dto.getAgeDescription() != null) {
            breed.setAgeDescription(
                    dto.getAgeDescription().trim()
            );
        }

        if (dto.getGender() != null) {
            breed.setGender(
                    dto.getGender().trim().toUpperCase()
            );
        }

        /*
         * Species mapping
         */
        if (dto.getSpecies() != null && dto.getSpecies().getId() != null) {
            AnimalSpecies animalSpecies = new AnimalSpecies();
            animalSpecies.setId(dto.getSpecies().getId());
            breed.setSpecies(animalSpecies);
        } else if (dto.getSpeciesId() != null) {
            AnimalSpecies animalSpecies = new AnimalSpecies();
            animalSpecies.setId(dto.getSpeciesId());
            breed.setSpecies(animalSpecies);
        }

        /*
         * Custom species name
         */
        if (ValidationUtils.isValid(dto.getCustomSpeciesName())) {
            breed.setCustomSpeciesName(
                    dto.getCustomSpeciesName().trim()
            );
        } else {
            breed.setCustomSpeciesName(null);
        }

        /*
         * Custom breed name
         */
        if (ValidationUtils.isValid(dto.getCustomBreedName())) {
            breed.setCustomBreedName(
                    dto.getCustomBreedName().trim()
            );
        } else {
            breed.setCustomBreedName(null);
        }

        /*
         * Description
         */
        if (ValidationUtils.isValid(dto.getDescription())) {
            breed.setDescription(
                    dto.getDescription().trim()
            );
        } else {
            breed.setDescription(null);
        }

        /*
         * Price offered
         */
        breed.setPriceOffered(
                dto.getPriceOffered()
        );

        /*
         * Display order
         */
        if (dto.getDisplayOrder() != null) {
            breed.setDisplayOrder(
                    dto.getDisplayOrder()
            );
        }
    }
}
