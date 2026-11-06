package com.keltron.petshop.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopCertificateDraftDto {

    private Long applicationId;
    private String applicationNumber;
    private String proposedCertificateNumber;
    private String ownerName;
    private String shopName;
    private String shopAddress;
    private String district;
    private String permittedAnimals;
    private LocalDate issueDate;
    private Boolean isAlreadyGenerated;
    private Long certificateId;
}
