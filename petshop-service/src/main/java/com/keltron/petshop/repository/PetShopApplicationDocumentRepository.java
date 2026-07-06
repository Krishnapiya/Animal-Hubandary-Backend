package com.keltron.petshop.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.petshop.entity.PetShopApplicationDocument;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface PetShopApplicationDocumentRepository
        extends AbstractRepository<
                PetShopApplicationDocument,
                Long> {

    List<PetShopApplicationDocument>
            findByApplication_Id(Long applicationId);

    Optional<PetShopApplicationDocument>
            findFirstByApplication_IdAndDocumentType_Code(
                    Long applicationId,
                    String code);
}