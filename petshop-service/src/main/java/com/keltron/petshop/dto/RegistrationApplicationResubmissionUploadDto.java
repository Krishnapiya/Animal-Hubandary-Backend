package com.keltron.petshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationApplicationResubmissionUploadDto {

    private String fileName;

    private String filePath;
}