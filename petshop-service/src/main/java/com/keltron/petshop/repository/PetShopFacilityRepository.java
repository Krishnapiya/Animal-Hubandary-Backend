package com.keltron.petshop.repository;

import java.util.Optional;

import com.keltron.petshop.entity.PetShopFacility;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface PetShopFacilityRepository extends AbstractRepository< PetShopFacility,Long> {
	
	Optional<PetShopFacility> findByPetShopDetailId(
            Long petShopDetailId);
}

