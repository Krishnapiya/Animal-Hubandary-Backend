package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederBreedDto;
import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.utility.annotations.WriteTransactional;
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

        Long dogBreederDetailId = null;

        if (dto.getDogBreederDetail() != null
                && dto.getDogBreederDetail().getId() != null) {

            dogBreederDetailId =
                    dto.getDogBreederDetail().getId();
        }

        /*
         * Check whether the same breed already exists
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

        breed.setBreedName(
                dto.getBreedName()
        );

        breed.setDogCount(
                dto.getDogCount()
        );

        breed.setAgeDescription(
                dto.getAgeDescription()
        );

        breed.setGender(
                dto.getGender()
        );
    }
}