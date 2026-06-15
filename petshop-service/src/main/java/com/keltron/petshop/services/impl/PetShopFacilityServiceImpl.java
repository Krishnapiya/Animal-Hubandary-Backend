package com.keltron.petshop.services.impl;

import org.springframework.stereotype.Service;

import com.keltron.petshop.dto.PetShopFacilityDto;
import com.keltron.petshop.entity.PetShopFacility;
import com.keltron.petshop.repository.PetShopFacilityRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class PetShopFacilityServiceImpl
        extends AbstractJpaService<
                PetShopFacilityDto,
                Long,
                PetShopFacilityRepository,
                PetShopFacility> {

}