package com.keltron.petshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keltron.petshop.entity.PetShopCertificate;

@Repository
public interface PetShopCertificateRepository
        extends JpaRepository<PetShopCertificate, Long> {

    Optional<PetShopCertificate> findByApplication_Id(Long applicationId);

    Optional<PetShopCertificate> findByCertificateNumber(String certificateNumber);

    List<PetShopCertificate> findByApplication_IdIn(List<Long> applicationIds);
}
