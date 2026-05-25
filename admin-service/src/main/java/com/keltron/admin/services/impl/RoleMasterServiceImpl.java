package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.RoleMasterRepository;
import com.keltron.utility.beans.dto.RoleMasterDto;
import com.keltron.utility.jpa.entity.RoleMaster;

import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class RoleMasterServiceImpl 
	extends AbstractJpaService<RoleMasterDto, Integer, RoleMasterRepository, RoleMaster> {

}
