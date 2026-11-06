package com.keltron.petshop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopCertificateDto {

    private Long id;
    private Long applicationId;
    private String applicationNumber;
    private String certificateNumber;
    private Integer districtId;
    private String districtName;
    private String districtCode;
    private Integer issueYear;
    private Integer serialNumber;
    private String fileName;
    private LocalDate issueDate;
    private LocalDateTime generatedAt;
    private Long generatedBy;
    private Boolean isPublished;
}
