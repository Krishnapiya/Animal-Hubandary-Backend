package com.keltron.admin.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.District;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface DistrictRepository extends AbstractRepository<District, Integer> {

    Optional<District> findByCodeIgnoreCase(String code);

    Optional<District> findByNameIgnoreCase(String name);
}