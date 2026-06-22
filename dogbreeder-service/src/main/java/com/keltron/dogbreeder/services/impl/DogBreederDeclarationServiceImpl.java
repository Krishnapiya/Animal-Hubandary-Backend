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
    public DogBreederDeclaration save(
            DogBreederDeclarationDto dto) {

        return super.save(dto);
    }

    @Override
    @WriteTransactional
    public DogBreederDeclaration update(
            Long id,
            DogBreederDeclarationDto dto) {

        return super.update(id, dto);
    }
}