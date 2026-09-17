package com.keltron.citizen.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRegistrationViewDto {

    private Long id;

    private String complaintNumber;

    private Long citizenUserId;

    private String placeOfIncident;

    private String petAnimalName;

    private String complaintDescription;

    private LocalDate incidentDate;

    private String photoPath;

    private String videoPath;

    private String documentPath;

    private Long statusId;

    private String status;

    private List<ComplaintDocumentDto> supportingDocuments;
}