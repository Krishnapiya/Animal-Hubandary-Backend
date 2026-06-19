package com.keltron.dogbreeder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederRegistrationStep1Dto {

    private Long applicationId;
    private Integer districtId;

    private String breederName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;

    private String contactMobile;
    private String contactEmail;

    private String facilityDetails;
    private Integer totalDogsCount;
}