package com.keltron.admin.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.admin.repository.PaymentStatusMasterRepository;
import com.keltron.utility.beans.dto.PaymentStatusMasterDto;
import com.keltron.utility.jpa.entity.PaymentStatusMaster;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class PaymentStatusMasterServiceImpl
        extends AbstractJpaService<
                PaymentStatusMasterDto,
                Long,
                PaymentStatusMasterRepository,
                PaymentStatusMaster> {

}