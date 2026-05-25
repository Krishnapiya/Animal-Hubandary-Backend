package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.SampleDepartmentRepository;
import com.keltron.utility.beans.dto.SampleDepartmentDto;
import com.keltron.utility.jpa.entity.SampleDepartment;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class SampleDepartmentServiceImpl
    extends AbstractJpaService<SampleDepartmentDto, Integer, SampleDepartmentRepository, SampleDepartment> {

    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
        List<SampleDepartmentDto> dtos = repository.findAll().stream().map(SampleDepartment::toDTO).toList();
        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }
}

