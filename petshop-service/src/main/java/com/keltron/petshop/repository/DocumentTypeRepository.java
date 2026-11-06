package com.keltron.petshop.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface DocumentTypeRepository
        extends AbstractRepository<DocumentType, Long> {

    Optional<DocumentType> findByCodeIgnoreCase(String code);

    Optional<DocumentType> findByCodeIgnoreCaseAndEntityScopeIgnoreCase(String code, String entityScope);

    List<DocumentType> findByEntityScopeIgnoreCaseAndActiveTrue(String entityScope);

    List<DocumentType> findByEntityScopeIgnoreCaseAndMandatoryTrueAndActiveTrue(String entityScope);
}
