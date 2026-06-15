package com.keltron.dogbreeder.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.dogbreeder.dto.ApplicationCorrectionDto;
import com.keltron.dogbreeder.entity.ApplicationCorrection;
import com.keltron.dogbreeder.repository.ApplicationCorrectionRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class ApplicationCorrectionServiceImpl
        extends AbstractJpaService<
                ApplicationCorrectionDto,
                Long,
                ApplicationCorrectionRepository,
                ApplicationCorrection> {

}