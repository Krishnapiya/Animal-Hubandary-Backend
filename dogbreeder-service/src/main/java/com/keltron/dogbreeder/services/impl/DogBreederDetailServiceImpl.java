package com.keltron.dogbreeder.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.dogbreeder.dto.DogBreederDetailDto;
import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.dogbreeder.repository.DogBreederDetailRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DogBreederDetailServiceImpl
        extends AbstractJpaService<
                DogBreederDetailDto,
                Long,
                DogBreederDetailRepository,
                DogBreederDetail> {

}