package com.keltron.citizen.predicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.citizen.entity.ComplaintDocument;
import com.keltron.citizen.searchbean.ComplaintDocumentSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class ComplaintDocumentPredicates {

    private ComplaintDocumentPredicates() {
    }

    public static Specification<ComplaintDocument> createPredicate(
            ComplaintDocumentSearchBean searchBean) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<Object, Object> complaintJoin =
                    root.join("complaint", JoinType.LEFT);

            Join<Object, Object> documentTypeJoin =
                    root.join("documentType", JoinType.LEFT);

            Join<Object, Object> uploadedByJoin =
                    root.join("uploadedBy", JoinType.LEFT);

            // =====================================================
            // Filter by ID
            // =====================================================

            if (ValidationUtils.isValid(searchBean.getId())) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            // =====================================================
            // Filter by Complaint ID(s)
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getComplaintIds())
                    && !searchBean.getComplaintIds().isEmpty()) {

                predicates.add(
                        complaintJoin
                                .get("id")
                                .in(searchBean.getComplaintIds()));

            } else if (ValidationUtils.isValid(
                    searchBean.getComplaintId())) {

                predicates.add(
                        criteriaBuilder.equal(
                                complaintJoin.get("id"),
                                searchBean.getComplaintId()));
            }

            // =====================================================
            // Filter by Document Type ID(s)
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getDocumentTypeIds())
                    && !searchBean.getDocumentTypeIds().isEmpty()) {

                predicates.add(
                        documentTypeJoin
                                .get("id")
                                .in(searchBean.getDocumentTypeIds()));

            } else if (ValidationUtils.isValid(
                    searchBean.getDocumentTypeId())) {

                predicates.add(
                        criteriaBuilder.equal(
                                documentTypeJoin.get("id"),
                                searchBean.getDocumentTypeId()));
            }

            // =====================================================
            // Filter by Uploaded By ID(s)
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getUploadedByIds())
                    && !searchBean.getUploadedByIds().isEmpty()) {

                predicates.add(
                        uploadedByJoin
                                .get("id")
                                .in(searchBean.getUploadedByIds()));

            } else if (ValidationUtils.isValid(
                    searchBean.getUploadedBy())) {

                predicates.add(
                        criteriaBuilder.equal(
                                uploadedByJoin.get("id"),
                                searchBean.getUploadedBy()));
            }

            // =====================================================
            // Filter by File Name
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getFileName())) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("fileName")),
                                "%"
                                        + searchBean.getFileName()
                                                .toLowerCase()
                                        + "%"));
            }

            // =====================================================
            // Filter by File Path
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getFilePath())) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("filePath")),
                                "%"
                                        + searchBean.getFilePath()
                                                .toLowerCase()
                                        + "%"));
            }

            // =====================================================
            // Filter by Mime Type
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getMimeType())) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("mimeType")),
                                "%"
                                        + searchBean.getMimeType()
                                                .toLowerCase()
                                        + "%"));
            }

            // =====================================================
            // Filter by Uploaded Date From
            // =====================================================

            if (searchBean.getUploadedAtFrom() != null) {

                LocalDateTime fromDate =
                        searchBean.getUploadedAtFrom()
                                .atStartOfDay();

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("uploadedAt"),
                                fromDate));
            }

            // =====================================================
            // Filter by Uploaded Date To
            // =====================================================

            if (searchBean.getUploadedAtTo() != null) {

                LocalDateTime toDate =
                        searchBean.getUploadedAtTo()
                                .plusDays(1)
                                .atStartOfDay();

                predicates.add(
                        criteriaBuilder.lessThan(
                                root.get("uploadedAt"),
                                toDate));
            }

            // =====================================================
            // Global Search
            // =====================================================

            if (ValidationUtils.isValid(
                    searchBean.getSearch())) {

                String keyword =
                        "%"
                                + searchBean.getSearch()
                                        .toLowerCase()
                                + "%";

                Predicate byFileName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("fileName")),
                                keyword);

                Predicate byFilePath =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("filePath")),
                                keyword);

                Predicate byMimeType =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("mimeType")),
                                keyword);

                Predicate byComplaintNumber =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        complaintJoin.get(
                                                "complaintNumber")),
                                keyword);

                Predicate byDocumentType =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        documentTypeJoin.get("name")),
                                keyword);

                Predicate byUploadedUsername =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        uploadedByJoin.get(
                                                "username")),
                                keyword);

                Predicate byUploadedFname =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        uploadedByJoin.get("fname")),
                                keyword);

                Predicate byUploadedLname =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        uploadedByJoin.get("lname")),
                                keyword);

                predicates.add(
                        criteriaBuilder.or(
                                byFileName,
                                byFilePath,
                                byMimeType,
                                byComplaintNumber,
                                byDocumentType,
                                byUploadedUsername,
                                byUploadedFname,
                                byUploadedLname));
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(
                            predicates.toArray(
                                    new Predicate[0]))
                    : null;
        };
    }
}