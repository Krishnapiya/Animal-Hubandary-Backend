package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.StoreItemRepository;
import com.keltron.utility.beans.dto.StoreItemDto;
import com.keltron.utility.jpa.entity.StoreItem;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class StoreItemServiceImpl
    extends AbstractJpaService<StoreItemDto, Integer, StoreItemRepository, StoreItem> {

    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
        List<StoreItemDto> dtos = repository.findAll().stream().map(StoreItem::toDTO).toList();
        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }
}
