package com.keltron.dogbreeder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DogBreederRegistrationApplicationResubmissionUploadDto {

    private String fileName;

    private String filePath;
}