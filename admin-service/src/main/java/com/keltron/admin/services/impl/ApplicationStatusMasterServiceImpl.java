package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.ApplicationStatusMasterRepository;
import com.keltron.utility.beans.dto.ApplicationStatusMasterDto;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class ApplicationStatusMasterServiceImpl
        extends AbstractJpaService<
                ApplicationStatusMasterDto,
                Long,
                ApplicationStatusMasterRepository,
                ApplicationStatusMaster> {

}