package com.keltron.petshop.services;

import java.util.List;

import com.keltron.petshop.dto.PetShopCertificateDraftDto;
import com.keltron.petshop.dto.PetShopCertificateDto;
import com.keltron.petshop.dto.PetShopRegistrationApplicationDto;
import com.keltron.petshop.entity.PetShopCertificate;

public interface PetShopCertificateService {

    PetShopCertificateDto generateCertificate(Long applicationId, Long adminUserId);

    PetShopCertificateDto getCertificateDetails(Long applicationId);

    byte[] getCertificatePdfBytes(Long applicationId);

    PetShopCertificate getCertificateEntity(Long applicationId);

    void enrichApplicationsWithCertificate(List<PetShopRegistrationApplicationDto> dtos);

    Long getCurrentUserId();

    void validateCertificateAccess(Long applicationId);

    PetShopCertificateDraftDto getDraftCertificateDetails(Long applicationId);

    byte[] getDraftCertificatePdfBytes(Long applicationId);
}
