package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.admin.repository.PaymentTransactionRepository;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.beans.dto.PaymentTransactionDto;
import com.keltron.utility.jpa.entity.PaymentTransaction;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class PaymentTransactionServiceImpl
        extends AbstractJpaService<
                PaymentTransactionDto,
                Long,
                PaymentTransactionRepository,
                PaymentTransaction> {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {

        List<PaymentTransaction> paymentTransactions =
                paymentTransactionRepository.findAll();

        List<PaymentTransactionDto> dtos =
                paymentTransactions
                        .stream()
                        .map(PaymentTransaction::toDTO)
                        .toList();

        return ExcelExportUtil.generateExcel(
                dtos,
                request.getXls_config());
    }

    @Override
    @WriteTransactional
    public PaymentTransaction save(PaymentTransactionDto dto) {
        return super.save(dto);
    }

    @Override
    @WriteTransactional
    public PaymentTransaction update(Long id, PaymentTransactionDto dto) {
        return super.update(id, dto);
    }
}