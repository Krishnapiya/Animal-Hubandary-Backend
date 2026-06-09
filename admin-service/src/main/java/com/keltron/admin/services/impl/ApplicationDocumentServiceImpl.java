package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.ApplicationDocumentRepository;
import com.keltron.utility.beans.dto.ApplicationDocumentDto;
import com.keltron.utility.jpa.entity.ApplicationDocument;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;


@Service
public class ApplicationDocumentServiceImpl
        extends AbstractJpaService<ApplicationDocumentDto, Long,
        ApplicationDocumentRepository, ApplicationDocument> {

	 public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
	        List<ApplicationDocumentDto> dtos = repository.findAll().stream().map(ApplicationDocument::toDTO).toList();
	        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
	    }
}