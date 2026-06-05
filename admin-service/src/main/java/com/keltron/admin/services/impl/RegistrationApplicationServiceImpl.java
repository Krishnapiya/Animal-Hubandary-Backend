package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.RegistrationApplicationRepository;
import com.keltron.utility.beans.dto.RegistrationApplicationDto;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class RegistrationApplicationServiceImpl
        extends AbstractJpaService<
                RegistrationApplicationDto,
                Long,
                RegistrationApplicationRepository,
                RegistrationApplication> {

}