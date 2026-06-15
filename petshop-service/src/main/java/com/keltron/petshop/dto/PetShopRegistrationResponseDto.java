package com.keltron.petshop.dto;

import com.keltron.petshop.dto.PetShopDetailDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopRegistrationResponseDto {

    private Long applicationId;
    private Long detailId;
    private String statusCode;
    private PetShopDetailDto detail;
}
