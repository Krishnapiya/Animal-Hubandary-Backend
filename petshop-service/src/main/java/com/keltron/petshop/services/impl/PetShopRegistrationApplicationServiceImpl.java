package com.keltron.petshop.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import com.keltron.petshop.repository.PetShopOfficeRepository;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.entity.PetShopApplicationDocument;
import java.util.List;
import java.time.LocalDateTime;

import com.keltron.petshop.dto.RegistrationApplicationResubmissionDto;
import com.keltron.petshop.entity.RegistrationApplicationResubmission;
import com.keltron.petshop.repository.RegistrationApplicationResubmissionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import com.keltron.utility.constants.ApplicationStatus;
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
    
    @Autowired
    private RegistrationApplicationStatusHistoryServiceImpl historyService;
    
    @Autowired
    private RegistrationApplicationResubmissionRepository
            resubmissionRepository;
    
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
    public List<PetShopRegistrationApplicationDto> getPetShopApplications(String status) {

        List<PetShopRegistrationApplication> applications =
                repository.findByEntityTypeAndStatus_StatusCodeInOrderByIdDesc(
                        "PET_SHOP",
                        List.of(
                                ApplicationStatus.SUBMITTED.name(),
                                ApplicationStatus.RESUBMITTED.name(),
                                ApplicationStatus.FORWARDED_TO_CVO.name(),
                                ApplicationStatus.INSPECTION_SCHEDULED.name(),
                                ApplicationStatus.VERIFIED_BY_CVO.name(),
                                ApplicationStatus.REJECTED_BY_CVO.name(),
                                ApplicationStatus.APPLICATION_APPROVED.name(),
                                ApplicationStatus.APPLICATION_REJECTED.name()
                        ));

        if (status != null && !status.isBlank()) {
            applications = applications.stream()
                    .filter(a ->
                            a.getStatus() != null &&
                            status.equalsIgnoreCase(
                                    a.getStatus().getStatusCode()))
                    .toList();
        }

        return applications.stream()
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
        
        ApplicationStatus fromStatus = application.getStatus() == null
                ? null
                : ApplicationStatus.valueOf(
                        application.getStatus().getStatusCode());

        application.setStatus(
        		statusRepository.findByStatusCode(
        		        ApplicationStatus.FORWARDED_TO_CVO.name())
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
        historyService.logStatusChange(
                application.getId(),
                fromStatus,
                ApplicationStatus.FORWARDED_TO_CVO,
                "SYSTEM",
                "Application forwarded to CVO",
                "FORWARD");

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
    public List<PetShopRegistrationApplicationDto> getMyApplications(String status) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String username = jwt.getSubject();

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        List<PetShopRegistrationApplication> applications =
                repository.findByApplicantUserIdOrderByIdDesc(user.getId());

        if (status != null && !status.isBlank()) {
            applications = applications.stream()
                    .filter(a ->
                            a.getStatus() != null &&
                            status.equalsIgnoreCase(
                                    a.getStatus().getStatusCode()))
                    .toList();
        }

        return applications.stream()
                .map(application -> {

                    PetShopRegistrationApplicationDto dto =
                            application.toDTO();

                    detailRepository.findByApplicationId(application.getId())
                            .ifPresent(detail ->
                                    dto.setShopName(detail.getShopName()));

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
        
        ApplicationStatus fromStatus = application.getStatus() == null
                ? null
                : ApplicationStatus.valueOf(
                        application.getStatus().getStatusCode());

        application.setStatus(
        		statusRepository.findByStatusCode(
        		        ApplicationStatus.SUBMITTED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status SUBMITTED not found")));

        repository.save(application);
        historyService.logStatusChange(
                application.getId(),
                fromStatus,
                ApplicationStatus.SUBMITTED,
                "SYSTEM",
                "Application submitted",
                "SUBMIT");

        notificationService.createNotification(
                application.getApplicantUserId(),
                "PET_SHOP",
                application.getId(),
                "Application Submitted",
                "Your Pet Shop registration application has been submitted successfully.",
                "INFO");

        return application.toDTO();
    }
    
    @Transactional
    public RegistrationApplicationResubmissionDto resubmitApplication(
            RegistrationApplicationResubmissionDto dto) {

        PetShopRegistrationApplication application =
                repository.findById(dto.getApplicationId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        ApplicationStatus fromStatus =
                ApplicationStatus.valueOf(
                        application.getStatus().getStatusCode());

        if (fromStatus != ApplicationStatus.REJECTED_BY_CVO) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only CVO rejected applications can be resubmitted");
        }

        RegistrationApplicationResubmission resubmission =
                dto.toEntity();

        resubmission.setApplication(application);
        resubmission.setResubmittedAt(LocalDateTime.now());
        resubmission.setResubmittedBy(application.getApplicantUserId());

        resubmissionRepository.save(resubmission);

        application.setStatus(
                statusRepository.findByStatusCode(
                        ApplicationStatus.RESUBMITTED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status RESUBMITTED not found")));

        repository.save(application);

        historyService.logStatusChange(
                application.getId(),
                fromStatus,
                ApplicationStatus.RESUBMITTED,
                "SYSTEM",
                "Application resubmitted",
                "RESUBMIT");

        notificationService.createNotification(
                application.getApplicantUserId(),
                "PET_SHOP",
                application.getId(),
                "Application Resubmitted",
                "Your application has been resubmitted successfully.",
                "INFO");

        return resubmission.toDTO();
    }
    
    @Transactional(readOnly = true)
    public List<PetShopRegistrationApplicationDto> getMyForwardedApplications(String status) {

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

        List<PetShopRegistrationApplication> applications =
                repository.findByCvOfficeIdOrderByIdDesc(officeId);

        if (status != null && !status.isBlank()) {
            applications = applications.stream()
                    .filter(a ->
                            a.getStatus() != null &&
                            status.equalsIgnoreCase(
                                    a.getStatus().getStatusCode()))
                    .toList();
        }

        System.out.println("Applications Found = " + applications.size());

        return applications.stream()
                .map(PetShopRegistrationApplication::toDTO)
                .toList();
    }
    @Transactional
    public PetShopRegistrationApplicationDto approveApplication(Long id) {

        PetShopRegistrationApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));
        
        ApplicationStatus fromStatus = application.getStatus() == null
                ? null
                : ApplicationStatus.valueOf(
                        application.getStatus().getStatusCode());

        application.setStatus(
        		statusRepository.findByStatusCode(
        		        ApplicationStatus.APPLICATION_APPROVED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_APPROVED not found")));

        repository.save(application);
        
        historyService.logStatusChange(
                application.getId(),
                fromStatus,
                ApplicationStatus.APPLICATION_APPROVED,
                "SYSTEM",
                "Application approved",
                "APPROVE");

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
        
        ApplicationStatus fromStatus = application.getStatus() == null
                ? null
                : ApplicationStatus.valueOf(
                        application.getStatus().getStatusCode());

        application.setStatus(
        		statusRepository.findByStatusCode(
        		        ApplicationStatus.APPLICATION_REJECTED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_REJECTED not found")));

        repository.save(application);
        historyService.logStatusChange(
                application.getId(),
                fromStatus,
                ApplicationStatus.APPLICATION_REJECTED,
                "SYSTEM",
                "Application rejected",
                "REJECT");

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