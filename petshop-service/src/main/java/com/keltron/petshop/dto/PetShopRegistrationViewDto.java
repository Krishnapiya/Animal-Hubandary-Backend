package com.keltron.petshop.dto;

import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopRegistrationViewDto {

    private Long applicationId;
    private String applicationNumber;

    private String entityType;
    private String applicationKind;

    private String status;
    private String district;

    private LocalDateTime submittedAt;

    // Pet Shop Details
    private String shopName;
    private String ownerName;

    private String addressLine1;
    private String addressLine2;

    private String city;
    private String pincode;

    private String contactMobile;
    private String contactEmail;

    private String registrationDetails;
 // STEP 2
    private String accommodationInfrastructure;
    private String workingHours;
    private String restDay;
    private String ventilationArrangement;
    private String lightingArrangement;
    private String fireSafetyArrangement;
    private String heatingCoolingArrangement;
    private String powerBackupArrangement;
    private String foodStorageArrangement;
    private String cleanlinessWasteArrangement;
    private String deadAnimalDisposalArrangement;
    private String veterinarySupportArrangement;

    // STEP 3
    private List<PetShopProposedAnimalDto> animals;

    // STEP 4
    private String declarationPlace;
    private LocalDate declarationDate;
    private String affidavitDeponentName;
    
    private List<PetShopApplicationDocumentDto> supportingDocuments;
    
}