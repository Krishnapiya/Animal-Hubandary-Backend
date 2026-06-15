package com.keltron.dogbreeder.entity;

import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.dogbreeder.dto.DogBreederDetailDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "dog_breeder_detail", schema = "awb")
@NoArgsConstructor
@ToString
public class DogBreederDetail extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, unique = true)
    private Long applicationId;

    @Column(name = "breeder_name", nullable = false)
    private String breederName;

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

    @Column(name = "facility_details")
    private String facilityDetails;

    @Column(name = "total_dogs_count")
    private Integer totalDogsCount;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DogBreederDetailDto dogBreederDetailDto =
                (DogBreederDetailDto) dto;

        if (ValidationUtils.isValid(
                dogBreederDetailDto.getId()))
            this.id = dogBreederDetailDto.getId();

        if (ValidationUtils.isValid(
                dogBreederDetailDto.getApplicationId()))
            this.applicationId =
                    dogBreederDetailDto.getApplicationId();

        if (ValidationUtils.isValid(
                dogBreederDetailDto.getBreederName()))
            this.breederName =
                    dogBreederDetailDto.getBreederName();

        if (ValidationUtils.isValid(
                dogBreederDetailDto.getAddressLine1()))
            this.addressLine1 =
                    dogBreederDetailDto.getAddressLine1();

        this.addressLine2 =
                dogBreederDetailDto.getAddressLine2();

        this.city =
                dogBreederDetailDto.getCity();

        this.pincode =
                dogBreederDetailDto.getPincode();

        if (ValidationUtils.isValid(
                dogBreederDetailDto.getContactMobile()))
            this.contactMobile =
                    dogBreederDetailDto.getContactMobile();

        if (ValidationUtils.isValid(
                dogBreederDetailDto.getContactEmail()))
            this.contactEmail =
                    dogBreederDetailDto.getContactEmail();

        this.facilityDetails =
                dogBreederDetailDto.getFacilityDetails();

        this.totalDogsCount =
                dogBreederDetailDto.getTotalDogsCount();
    }

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederDetailDto toDTO() {

        DogBreederDetailDto dto =
                new DogBreederDetailDto();

        dto.setId(id);
        dto.setApplicationId(applicationId);
        dto.setBreederName(breederName);
        dto.setAddressLine1(addressLine1);
        dto.setAddressLine2(addressLine2);
        dto.setCity(city);
        dto.setPincode(pincode);
        dto.setContactMobile(contactMobile);
        dto.setContactEmail(contactEmail);
        dto.setFacilityDetails(facilityDetails);
        dto.setTotalDogsCount(totalDogsCount);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(breederName);

        return payload;
    }

    public DogBreederDetail(Long id) {
        this.id = id;
    }
}