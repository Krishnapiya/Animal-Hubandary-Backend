package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.PaymentModeRepository;
import com.keltron.utility.beans.dto.PaymentModeDto;
import com.keltron.utility.jpa.entity.PaymentMode;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class PaymentModeServiceImpl
    extends AbstractJpaService<PaymentModeDto, Integer, PaymentModeRepository, PaymentMode> {

    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
        List<PaymentModeDto> dtos = repository.findAll().stream().map(PaymentMode::toDTO).toList();
        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }
}
