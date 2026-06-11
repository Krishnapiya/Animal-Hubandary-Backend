package com.keltron.petshop.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.petshop.repository.PetShopDetailRepository;
import com.keltron.utility.beans.dto.PetShopDetailDto;
import com.keltron.utility.jpa.entity.PetShopDetail;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class PetShopDetailServiceImpl
        extends AbstractJpaService<
                PetShopDetailDto,
                Long,
                PetShopDetailRepository,
                PetShopDetail> {

}