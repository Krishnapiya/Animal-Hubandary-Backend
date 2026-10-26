package com.keltron.dogbreeder.dto;

import java.util.regex.Pattern;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederDetailDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long applicationId;
    private String breederName;
    private String panNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;
    private String contactMobile;
    private String contactEmail;
    private String facilityDetails;
    private Integer totalDogsCount;

    private static final Pattern PAN_PATTERN =
            Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

    @Override
    public DogBreederDetail toEntity() {

        DogBreederDetail entity = new DogBreederDetail();

        entity.setId(id);
        entity.setApplicationId(applicationId);
        entity.setBreederName(breederName);
        entity.setPanNumber(panNumber);
        entity.setAddressLine1(addressLine1);
        entity.setAddressLine2(addressLine2);
        entity.setCity(city);
        entity.setPincode(pincode);
        entity.setContactMobile(contactMobile);
        entity.setContactEmail(contactEmail);
        entity.setFacilityDetails(facilityDetails);
        entity.setTotalDogsCount(totalDogsCount);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}