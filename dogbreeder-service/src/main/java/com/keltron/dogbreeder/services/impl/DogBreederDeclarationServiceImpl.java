package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederDeclarationDto;
import com.keltron.dogbreeder.entity.DogBreederDeclaration;
import com.keltron.dogbreeder.repository.DogBreederDeclarationRepository;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class DogBreederDeclarationServiceImpl extends AbstractJpaService<
        DogBreederDeclarationDto,
        Long,
        DogBreederDeclarationRepository,
        DogBreederDeclaration> {

    @Autowired
    private DogBreederDeclarationRepository dogBreederDeclarationRepository;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {

        List<DogBreederDeclaration> declarations =
                dogBreederDeclarationRepository.findAll();

        List<DogBreederDeclarationDto> dtos =
                declarations.stream()
                        .map(DogBreederDeclaration::toDTO)
                        .toList();

        return ExcelExportUtil.generateExcel(
                dtos,
                request.getXls_config()
        );
    }

    @Override
    @WriteTransactional
    public DogBreederDeclaration save(DogBreederDeclarationDto dto) {

        Long dogBreederDetailId = null;

        if (dto.getDogBreederDetail() != null
                && dto.getDogBreederDetail().getId() != null) {
            dogBreederDetailId = dto.getDogBreederDetail().getId();
        }

        if (dogBreederDetailId != null) {

            DogBreederDeclaration existingDeclaration =
                    dogBreederDeclarationRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .orElse(null);

            if (existingDeclaration != null) {

                updateEditableFields(existingDeclaration, dto);

                return dogBreederDeclarationRepository.save(existingDeclaration);
            }
        }

        return super.save(dto);
    }

    @Override
    @WriteTransactional
    public DogBreederDeclaration update(Long id, DogBreederDeclarationDto dto) {

        DogBreederDeclaration existingDeclaration =
                dogBreederDeclarationRepository.findById(id).orElse(null);

        if (existingDeclaration != null) {

            updateEditableFields(existingDeclaration, dto);

            return dogBreederDeclarationRepository.save(existingDeclaration);
        }

        return super.update(id, dto);
    }

    private void updateEditableFields(
            DogBreederDeclaration declaration,
            DogBreederDeclarationDto dto) {

        declaration.setQualificationExperience(dto.getQualificationExperience());
        declaration.setDeclarationAccepted(dto.getDeclarationAccepted());
        declaration.setDeclarationPlace(dto.getDeclarationPlace());
        declaration.setDeclarationDate(dto.getDeclarationDate());
        declaration.setApplicantName(dto.getApplicantName());
        declaration.setSignatureName(dto.getSignatureName());
    }
}