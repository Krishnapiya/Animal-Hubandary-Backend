package com.keltron.petshop.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import com.keltron.petshop.repository.PetShopOfficeRepository;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.entity.PetShopApplicationDocument;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.keltron.petshop.entity.ApplicationDeclaration;
import com.keltron.petshop.entity.PetShopFacility;
import com.keltron.petshop.entity.PetShopProposedAnimal;

import com.keltron.petshop.repository.ApplicationDeclarationRepository;
import com.keltron.petshop.repository.ApplicationStatusMasterRepository;
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
    @Autowired
    private ApplicationStatusMasterRepository statusRepository;
    @Autowired
    private PetShopOfficeRepository officeRepository;
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PetShopNotificationServiceImpl notificationService;
    
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
        
        dto.setFatherOrHusbandName(detail.getFatherOrHusbandName());
        dto.setAge(detail.getAge());

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
    @Transactional(readOnly = true)
    public List<PetShopRegistrationApplicationDto> getPetShopApplications() {

    	return repository
    	        .findByEntityTypeAndStatus_StatusCodeInOrderByIdDesc(
    	                "PET_SHOP",
    	                List.of(
    	                        "SUBMITTED",
    	                        "FORWARDED_TO_CVO",
    	                        "INSPECTION_SCHEDULED",
    	                        "VERIFIED_BY_CVO",
    	                        "REJECTED_BY_CVO",
    	                        "APPLICATION_APPROVED",
    	                        "APPLICATION_REJECTED"
    	                ))
    	        .stream()
    	        .map(PetShopRegistrationApplication::toDTO)
    	        .toList();
    }
    @Transactional
    public PetShopRegistrationApplicationDto forwardApplication(Long id) {

        PetShopRegistrationApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        application.setStatus(
                statusRepository.findByStatusCode("FORWARDED_TO_CVO")
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status FORWARDED_TO_CVO not found")));
        Office office = officeRepository
                .findByDistrictId(application.getDistrict().getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "District office not found"));
        System.out.println("Application District = " + application.getDistrict().getId());
        System.out.println("Office ID = " + office.getId());

        application.setCvOfficeId(Long.valueOf(office.getId()));

        System.out.println("cvOfficeId after set = " + application.getCvOfficeId());

        repository.save(application);

        System.out.println("Saved successfully");

        application.setCvOfficeId(Long.valueOf(office.getId()));

        repository.save(application);

        notificationService.createNotification(
                application.getApplicantUserId(),
                "PET_SHOP",
                application.getId(),
                "Application Forwarded",
                "Your application has been forwarded to the Chief Veterinary Officer for verification.",
                "INFO");

        return application.toDTO();
    }
    
    @Transactional(readOnly = true)
    public List<PetShopRegistrationApplicationDto> getMyApplications() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String username = jwt.getSubject();

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        return repository
                .findByApplicantUserIdOrderByIdDesc(user.getId())
                .stream()
                .map(application -> {

                    PetShopRegistrationApplicationDto dto = application.toDTO();

                    detailRepository.findByApplicationId(application.getId())
                            .ifPresent(detail -> {
                                dto.setShopName(detail.getShopName());
                            });

                    return dto;
                })
                .toList();
    }
    @Transactional
    public PetShopRegistrationApplicationDto submitApplication(Long id) {

        PetShopRegistrationApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        application.setStatus(
                statusRepository.findByStatusCode("SUBMITTED")
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status SUBMITTED not found")));

        repository.save(application);

        notificationService.createNotification(
                application.getApplicantUserId(),
                "PET_SHOP",
                application.getId(),
                "Application Submitted",
                "Your Pet Shop registration application has been submitted successfully.",
                "INFO");

        return application.toDTO();
    }
    
    
    @Transactional(readOnly = true)
    public List<PetShopRegistrationApplicationDto> getMyForwardedApplications() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String username = jwt.getSubject();

        System.out.println("Logged in username = " + username);

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        System.out.println("Office = " + user.getOffice().getName());

        Long officeId = Long.valueOf(user.getOffice().getId());

        System.out.println("Office ID = " + officeId);

        List<PetShopRegistrationApplicationDto> applications =
                repository.findByCvOfficeIdOrderByIdDesc(officeId)
                        .stream()
                        .map(PetShopRegistrationApplication::toDTO)
                        .toList();

        System.out.println("Applications Found = " + applications.size());

        return applications;
    }
    @Transactional
    public PetShopRegistrationApplicationDto approveApplication(Long id) {

        PetShopRegistrationApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        application.setStatus(
                statusRepository.findByStatusCode("APPLICATION_APPROVED")
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_APPROVED not found")));

        repository.save(application);

        notificationService.createNotification(
                application.getApplicantUserId(),
                "PET_SHOP",
                application.getId(),
                "Application Approved",
                " Your Pet Shop registration application has been approved.",
                "SUCCESS");

        return application.toDTO();
    }
    
    @Transactional
    public PetShopRegistrationApplicationDto rejectApplication(Long id) {

        PetShopRegistrationApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        application.setStatus(
                statusRepository.findByStatusCode("APPLICATION_REJECTED")
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_REJECTED not found")));

        repository.save(application);

        notificationService.createNotification(
                application.getApplicantUserId(),
                "PET_SHOP",
                application.getId(),
                "Application Rejected",
                "Your Pet Shop registration application has been rejected.",
                "ERROR");

        return application.toDTO();
    }
    

}