package com.keltron.petshop.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.petshop.repository.ApplicationWorkflowRepository;
import com.keltron.utility.beans.dto.ApplicationWorkflowDto;
import com.keltron.utility.jpa.entity.ApplicationWorkflow;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class ApplicationWorkflowServiceImpl
        extends AbstractJpaService<
                ApplicationWorkflowDto,
                Long,
                ApplicationWorkflowRepository,
                ApplicationWorkflow> {

}