package com.keltron.petshop.repository;

import java.util.Optional;

import com.keltron.petshop.entity.ApplicationDeclaration;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface ApplicationDeclarationRepository extends AbstractRepository< ApplicationDeclaration, Long> {
	Optional<ApplicationDeclaration>
    findByApplicationId(Long applicationId);
}