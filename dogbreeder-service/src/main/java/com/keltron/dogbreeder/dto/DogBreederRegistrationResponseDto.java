package com.keltron.dogbreeder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederRegistrationResponseDto {

    private Long applicationId;
    private Long detailId;
    private String statusCode;
    private DogBreederDetailDto detail;
}