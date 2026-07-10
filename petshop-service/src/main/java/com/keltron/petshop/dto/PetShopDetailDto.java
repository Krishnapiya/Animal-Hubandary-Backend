package com.keltron.petshop.dto;

import java.math.BigDecimal;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.petshop.entity.PetShopDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopDetailDto extends AbstractDto {

    private static final long serialVersionUID = 1L;
    private String fatherOrHusbandName;
    private Integer age;
    private Long id;
    private Long applicationId;
    private String shopName;
    private String ownerName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;
    private String contactMobile;
    private String contactEmail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String registrationDetails;

    @Override
    public PetShopDetail toEntity() {

        PetShopDetail entity = new PetShopDetail();

        entity.setId(id);
        entity.setApplicationId(applicationId);
        entity.setShopName(shopName);
        entity.setOwnerName(ownerName);
        entity.setAddressLine1(addressLine1);
        entity.setAddressLine2(addressLine2);
        entity.setCity(city);
        entity.setPincode(pincode);
        entity.setContactMobile(contactMobile);
        entity.setContactEmail(contactEmail);
        entity.setLatitude(latitude);
        entity.setLongitude(longitude);
        entity.setRegistrationDetails(registrationDetails);
        entity.setFatherOrHusbandName(fatherOrHusbandName);
        entity.setAge(age);

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