package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.DesignationRepository;
import com.keltron.utility.beans.dto.DesignationDto;
import com.keltron.utility.jpa.entity.Designation;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class DesignationServiceImpl
    extends AbstractJpaService<DesignationDto, Integer, DesignationRepository, Designation> {

    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
        List<DesignationDto> dtos = repository.findAll().stream().map(Designation::toDTO).toList();
        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }
}
