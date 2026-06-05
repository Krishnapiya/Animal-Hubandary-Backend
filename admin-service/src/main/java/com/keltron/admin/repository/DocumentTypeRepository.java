package com.keltron.admin.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface DocumentTypeRepository
        extends AbstractRepository<DocumentType, Long> {

    Optional<DocumentType> findByCodeIgnoreCase(String code);

    Optional<DocumentType> findByNameIgnoreCase(String name);
}