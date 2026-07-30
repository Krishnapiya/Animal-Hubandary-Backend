package com.keltron.dogbreeder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface DogBreederRegistrationApplicationRepository
        extends AbstractRepository<DogBreederRegistrationApplication, Long> {

    Optional<DogBreederRegistrationApplication>
            findFirstByApplicantUserIdAndEntityTypeAndStatus_StatusCodeOrderByIdDesc(
                    Long applicantUserId,
                    String entityType,
                    String statusCode);

    Optional<DogBreederRegistrationApplication>
            findByIdAndApplicantUserId(
                    Long id,
                    Long applicantUserId);

    List<DogBreederRegistrationApplication>
            findByEntityTypeOrderByIdDesc(
                    String entityType);

    Optional<DogBreederRegistrationApplication>
            findTopByEntityTypeOrderByIdDesc(
                    String entityType);

    @Query("""
            SELECT a
            FROM DogBreederRegistrationApplication a
            WHERE a.entityType = :entityType
              AND a.district.id = :districtId
              AND a.status.statusCode IN (
                    'FORWARDED_TO_CVO',
                    'INSPECTION_SCHEDULED',
                    'VERIFIED_BY_CVO',
                    'REJECTED_BY_CVO'
              )
            ORDER BY a.id DESC
            """)
    List<DogBreederRegistrationApplication> findCvoApplications(
            @Param("entityType") String entityType,
            @Param("districtId") Integer districtId);

    List<DogBreederRegistrationApplication>
            findByApplicantUserIdAndEntityTypeOrderByIdDesc(
                    Long applicantUserId,
                    String entityType);
}