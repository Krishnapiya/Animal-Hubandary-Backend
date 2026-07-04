package com.keltron.petshop.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.entity.PetShopApplicationDocument;
import java.util.List;

import com.keltron.petshop.entity.ApplicationDeclaration;
import com.keltron.petshop.entity.PetShopFacility;
import com.keltron.petshop.entity.PetShopProposedAnimal;

import com.keltron.petshop.repository.ApplicationDeclarationRepository;
import com.keltron.petshop.repository.PetShopFacilityRepository;
import com.keltron.petshop.repository.PetShopProposedAnimalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.petshop.dto.PetShopProposedAnimalDto;
import com.keltron.petshop.dto.PetShopRegistrationApplicationDto;
import com.keltron.petshop.dto.PetShopRegistrationViewDto;
import com.keltron.petshop.entity.PetShopDetail;
import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.repository.PetShopDetailRepository;
import com.keltron.petshop.repository.PetShopRegistrationApplicationRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import org.springframework.transaction.annotation.Transactional;
@Service
public class PetShopRegistrationApplicationServiceImpl
        extends AbstractJpaService<
                PetShopRegistrationApplicationDto,
                Long,
                PetShopRegistrationApplicationRepository,
                PetShopRegistrationApplication> {
	@Autowired
	private PetShopApplicationDocumentRepository documentRepository;
	

    @Autowired
    private PetShopDetailRepository detailRepository;
    @Autowired
    private PetShopFacilityRepository facilityRepository;

    @Autowired
    private PetShopProposedAnimalRepository animalRepository;

    @Autowired
    private ApplicationDeclarationRepository declarationRepository;
    @Transactional(readOnly = true)
    public PetShopRegistrationViewDto getApplication(Long id) {

        PetShopRegistrationApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        PetShopDetail detail =
                detailRepository
                        .findByApplicationId(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Pet shop detail not found"));
        PetShopFacility facility =
                facilityRepository
                        .findByPetShopDetailId(detail.getId())
                        .orElse(null);

        ApplicationDeclaration declaration =
                declarationRepository
                        .findByApplicationId(id)
                        .orElse(null);

        List<PetShopProposedAnimalDto> animals =
                animalRepository
                        .findByApplication_IdOrderByDisplayOrderAsc(id)
                        .stream()
                        .map(PetShopProposedAnimal::toDTO)
                        .toList();
        

        PetShopRegistrationViewDto dto =
                new PetShopRegistrationViewDto();

        dto.setApplicationId(application.getId());
        dto.setApplicationNumber(application.getApplicationNumber());

        dto.setEntityType(application.getEntityType());
        dto.setApplicationKind(application.getApplicationKind());

        if (application.getStatus() != null) {
        	dto.setStatus(application.getStatus().getStatusName());
        }

        if (application.getDistrict() != null) {
            dto.setDistrict(application.getDistrict().getName());
        }

        dto.setSubmittedAt(application.getSubmittedAt());

        dto.setShopName(detail.getShopName());
        dto.setOwnerName(detail.getOwnerName());

        dto.setAddressLine1(detail.getAddressLine1());
        dto.setAddressLine2(detail.getAddressLine2());

        dto.setCity(detail.getCity());
        dto.setPincode(detail.getPincode());

        dto.setContactMobile(detail.getContactMobile());
        dto.setContactEmail(detail.getContactEmail());

        dto.setRegistrationDetails(detail.getRegistrationDetails());
     // STEP 2
        if (facility != null) {

            dto.setAccommodationInfrastructure(
                    facility.getAccommodationInfrastructure());

            dto.setWorkingHours(
                    facility.getWorkingHours());

            dto.setRestDay(
                    facility.getRestDay());

            dto.setVentilationArrangement(
                    facility.getVentilationArrangement());

            dto.setLightingArrangement(
                    facility.getLightingArrangement());

            dto.setFireSafetyArrangement(
                    facility.getFireSafetyArrangement());

            dto.setHeatingCoolingArrangement(
                    facility.getHeatingCoolingArrangement());

            dto.setPowerBackupArrangement(
                    facility.getPowerBackupArrangement());

            dto.setFoodStorageArrangement(
                    facility.getFoodStorageArrangement());

            dto.setCleanlinessWasteArrangement(
                    facility.getCleanlinessWasteArrangement());

            dto.setDeadAnimalDisposalArrangement(
                    facility.getDeadAnimalDisposalArrangement());

            dto.setVeterinarySupportArrangement(
                    facility.getVeterinarySupportArrangement());
        }

        // STEP 3
        dto.setAnimals(animals);

        // STEP 4
        if (declaration != null) {

            dto.setDeclarationPlace(
                    declaration.getDeclarationPlace());

            dto.setDeclarationDate(
                    declaration.getDeclarationDate());

            dto.setAffidavitDeponentName(
                    declaration.getAffidavitDeponentName());
        }
        List<PetShopApplicationDocumentDto> documents =
                documentRepository.findByApplication_Id(id)
                        .stream()
                        .map(PetShopApplicationDocument::toDTO)
                        .toList();

        dto.setSupportingDocuments(documents);

        return dto;
    }
    

}