package com.keltron.petshop.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.keltron.utility.jpa.entity.Office;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface PetShopOfficeRepository extends AbstractRepository<Office, Integer> {

	Optional<Office> findByDistrictId(Integer districtId);

}