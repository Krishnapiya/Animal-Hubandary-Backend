package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.DistrictRepository;
import com.keltron.utility.beans.dto.DistrictDto;
import com.keltron.utility.jpa.entity.District;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DistrictServiceImpl extends
        AbstractJpaService<
                DistrictDto,
                Integer,
                DistrictRepository,
                District> {

}