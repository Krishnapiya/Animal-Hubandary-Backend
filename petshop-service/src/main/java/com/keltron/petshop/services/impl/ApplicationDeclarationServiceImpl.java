package com.keltron.petshop.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.petshop.dto.ApplicationDeclarationDto;
import com.keltron.petshop.entity.ApplicationDeclaration;
import com.keltron.petshop.repository.ApplicationDeclarationRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class ApplicationDeclarationServiceImpl
        extends AbstractJpaService<
                ApplicationDeclarationDto,
                Long,
                ApplicationDeclarationRepository,
                ApplicationDeclaration> {

}