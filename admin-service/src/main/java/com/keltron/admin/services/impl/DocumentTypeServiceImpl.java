package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.DocumentTypeRepository;
import com.keltron.utility.beans.dto.DocumentTypeDto;
import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DocumentTypeServiceImpl
        extends AbstractJpaService<
                DocumentTypeDto,
                Long,
                DocumentTypeRepository,
                DocumentType> {

}