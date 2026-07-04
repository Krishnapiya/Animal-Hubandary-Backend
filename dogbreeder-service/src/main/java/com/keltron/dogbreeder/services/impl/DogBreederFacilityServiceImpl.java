package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederFacilityDto;
import com.keltron.dogbreeder.entity.DogBreederFacility;
import com.keltron.dogbreeder.repository.DogBreederFacilityRepository;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class DogBreederFacilityServiceImpl extends AbstractJpaService<
        DogBreederFacilityDto,
        Long,
        DogBreederFacilityRepository,
        DogBreederFacility> {

    @Autowired
    private DogBreederFacilityRepository dogBreederFacilityRepository;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {

        List<DogBreederFacility> facilities =
                dogBreederFacilityRepository.findAll();

        List<DogBreederFacilityDto> dtos =
                facilities.stream()
                        .map(DogBreederFacility::toDTO)
                        .toList();

        return ExcelExportUtil.generateExcel(
                dtos,
                request.getXls_config());
    }

    @Override
    @WriteTransactional
    public DogBreederFacility save(DogBreederFacilityDto dto) {

        if (dto.getDogBreederDetailId() != null) {

            DogBreederFacility existingFacility =
                    dogBreederFacilityRepository
                            .findByDogBreederDetail_Id(dto.getDogBreederDetailId())
                            .orElse(null);

            if (existingFacility != null) {

                updateEditableFields(existingFacility, dto);

                return dogBreederFacilityRepository.save(existingFacility);
            }
        }

        return super.save(dto);
    }

    @Override
    @WriteTransactional
    public DogBreederFacility update(Long id, DogBreederFacilityDto dto) {

        DogBreederFacility existingFacility =
                dogBreederFacilityRepository.findById(id).orElse(null);

        if (existingFacility != null) {

            updateEditableFields(existingFacility, dto);

            return dogBreederFacilityRepository.save(existingFacility);
        }

        return super.update(id, dto);
    }

    private void updateEditableFields(
            DogBreederFacility facility,
            DogBreederFacilityDto dto) {

        facility.setAccommodationInfrastructure(dto.getAccommodationInfrastructure());
        facility.setWorkingHours(dto.getWorkingHours());
        facility.setRestDay(dto.getRestDay());
        facility.setVentilationArrangement(dto.getVentilationArrangement());
        facility.setLightingArrangement(dto.getLightingArrangement());
        facility.setHeatingCoolingArrangement(dto.getHeatingCoolingArrangement());
        facility.setFoodStorageArrangement(dto.getFoodStorageArrangement());
        facility.setCleanlinessWasteArrangement(dto.getCleanlinessWasteArrangement());
        facility.setDeadAnimalDisposalArrangement(dto.getDeadAnimalDisposalArrangement());
        facility.setVeterinarySupportArrangement(dto.getVeterinarySupportArrangement());
        facility.setCageEnclosureDetails(dto.getCageEnclosureDetails());
    }
}