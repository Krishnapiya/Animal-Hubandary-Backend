package com.keltron.citizen.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.citizen.entity.ComplaintDocument;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface ComplaintDocumentRepository
        extends AbstractRepository<
                ComplaintDocument,
                Long> {

    List<ComplaintDocument>
    findByComplaint_Id(Long complaintId);

    Optional<ComplaintDocument>
    findFirstByComplaint_IdAndDocumentType_Code(
            Long complaintId,
            String code);
}