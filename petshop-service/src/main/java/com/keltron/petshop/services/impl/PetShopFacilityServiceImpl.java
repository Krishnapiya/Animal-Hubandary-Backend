package com.keltron.petshop.services.impl;

import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Transactional(readOnly = true)
    public PetShopFacilityDto getDraft(
            Long petShopDetailId) {

        Optional<PetShopFacility> facility =
                repository.findByPetShopDetailId(
                        petShopDetailId);

        return facility
                .map(PetShopFacility::toDTO)
                .orElse(null);
    }

}