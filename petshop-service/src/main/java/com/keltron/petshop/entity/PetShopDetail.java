package com.keltron.petshop.entity;

import java.math.BigDecimal;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.petshop.dto.PetShopDetailDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "pet_shop_detail", schema = "awb")
@NoArgsConstructor
@ToString
public class PetShopDetail extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, unique = true)
    private Long applicationId;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column
    private String city;

    @Column
    private String pincode;

    @Column(name = "contact_mobile", nullable = false)
    private String contactMobile;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column
    private BigDecimal latitude;

    @Column
    private BigDecimal longitude;

    @Column(name = "registration_details")
    private String registrationDetails;
    
    @Column(name = "father_or_husband_name")
    private String fatherOrHusbandName;

    @Column(name = "age")
    private Integer age;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        PetShopDetailDto petShopDetailDto = (PetShopDetailDto) dto;

        if (ValidationUtils.isValid(petShopDetailDto.getId()))
            this.id = petShopDetailDto.getId();

        if (ValidationUtils.isValid(petShopDetailDto.getApplicationId()))
            this.applicationId = petShopDetailDto.getApplicationId();

        if (ValidationUtils.isValid(petShopDetailDto.getShopName()))
            this.shopName = petShopDetailDto.getShopName();

        if (ValidationUtils.isValid(petShopDetailDto.getOwnerName()))
            this.ownerName = petShopDetailDto.getOwnerName();

        if (ValidationUtils.isValid(petShopDetailDto.getAddressLine1()))
            this.addressLine1 = petShopDetailDto.getAddressLine1();

        this.addressLine2 = petShopDetailDto.getAddressLine2();
        this.city = petShopDetailDto.getCity();
        this.pincode = petShopDetailDto.getPincode();

        if (ValidationUtils.isValid(petShopDetailDto.getContactMobile()))
            this.contactMobile = petShopDetailDto.getContactMobile();

        if (ValidationUtils.isValid(petShopDetailDto.getContactEmail()))
            this.contactEmail = petShopDetailDto.getContactEmail();

        this.latitude = petShopDetailDto.getLatitude();
        this.longitude = petShopDetailDto.getLongitude();
        this.registrationDetails = petShopDetailDto.getRegistrationDetails();
        this.fatherOrHusbandName = petShopDetailDto.getFatherOrHusbandName();
        this.age = petShopDetailDto.getAge();
    }

    @Override
    public PetShopDetailDto toDTO() {

        PetShopDetailDto dto = new PetShopDetailDto();

        dto.setId(id);
        dto.setApplicationId(applicationId);
        dto.setShopName(shopName);
        dto.setOwnerName(ownerName);
        dto.setAddressLine1(addressLine1);
        dto.setAddressLine2(addressLine2);
        dto.setCity(city);
        dto.setPincode(pincode);
        dto.setContactMobile(contactMobile);
        dto.setContactEmail(contactEmail);
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setRegistrationDetails(registrationDetails);
        dto.setFatherOrHusbandName(fatherOrHusbandName);
        dto.setAge(age);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(shopName);

        return payload;
    }

    public PetShopDetail(Long id) {
        this.id = id;
    }
}