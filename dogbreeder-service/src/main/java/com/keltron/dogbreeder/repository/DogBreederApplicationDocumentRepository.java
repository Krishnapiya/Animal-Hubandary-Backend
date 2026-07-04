package com.keltron.dogbreeder.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.dogbreeder.entity.DogBreederApplicationDocument;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface DogBreederApplicationDocumentRepository extends AbstractRepository<DogBreederApplicationDocument, Long>{
	 List<DogBreederApplicationDocument> findByApplication_IdOrderByIdAsc(Long applicationId);

	    Optional<DogBreederApplicationDocument> findTopByApplication_IdAndDocumentType_IdOrderByIdDesc(
	            Long applicationId,
	            Long documentTypeId
	    );
}
