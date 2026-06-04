package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.FeeScheduleRepository;
import com.keltron.utility.beans.dto.FeeScheduleDto;
import com.keltron.utility.jpa.entity.FeeSchedule;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class FeeScheduleServiceImpl extends
        AbstractJpaService<
                FeeScheduleDto,
                Integer,
                FeeScheduleRepository,
                FeeSchedule> {

}