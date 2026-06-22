package com.keltron.petshop.repository;

import java.util.List;

import com.keltron.petshop.entity.PetShopProposedAnimal;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface PetShopProposedAnimalRepository extends AbstractRepository<PetShopProposedAnimal, Long> {
	 List<PetShopProposedAnimal>
     findByApplication_IdOrderByDisplayOrderAsc(Long applicationId);

}
