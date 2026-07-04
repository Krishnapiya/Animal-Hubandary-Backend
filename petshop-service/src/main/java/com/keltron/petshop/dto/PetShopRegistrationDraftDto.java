package com.keltron.petshop.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopRegistrationDraftDto {

    private Long applicationId;
    private String statusCode;
    private Integer districtId;
    private String districtName;
    private String shopName;
    private String ownerName;
    private String shopAddressLine1;
    private String shopAddressLine2;
    private String shopCity;
    private String shopPincode;
    private String ownerResidentialAddress;
    private String ownerAddressLine1;
    private String ownerAddressLine2;
    private String ownerCity;
    private String ownerPincode;
    private String ownerOfficeAddress;
    private String contactLandline;
    private String contactMobile;
    private String contactEmail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long detailId;
}
