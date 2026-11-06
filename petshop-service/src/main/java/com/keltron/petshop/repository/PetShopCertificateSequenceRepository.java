package com.keltron.petshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keltron.petshop.entity.PetShopCertificateSequence;
import jakarta.persistence.LockModeType;

@Repository
public interface PetShopCertificateSequenceRepository
        extends JpaRepository<PetShopCertificateSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM PetShopCertificateSequence s WHERE s.districtCode = :districtCode AND s.issueYear = :issueYear")
    Optional<PetShopCertificateSequence> findByDistrictCodeAndIssueYearForUpdate(
            @Param("districtCode") String districtCode,
            @Param("issueYear") Integer issueYear);

    Optional<PetShopCertificateSequence> findByDistrictCodeAndIssueYear(
            String districtCode,
            Integer issueYear);
}
