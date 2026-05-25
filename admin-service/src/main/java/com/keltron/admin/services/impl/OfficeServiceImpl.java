package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.OfficeRepository;
import com.keltron.utility.beans.dto.OfficeDto;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class OfficeServiceImpl
    extends AbstractJpaService<OfficeDto, Integer, OfficeRepository, Office> {

    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
        List<OfficeDto> dtos = repository.findAll().stream().map(Office::toDTO).toList();
        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }
}
