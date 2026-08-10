package com.keltron.citizen.dto;

import java.time.LocalDate;

import org.springframework.http.HttpMethod;

import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.responses.payload.DropdownPayload;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRegistrationDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

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

    // Status
    private Long statusId;
    private DropdownPayload<Long> status;

    @Override
    public ComplaintRegistration toEntity() {

        ComplaintRegistration entity = new ComplaintRegistration();

        entity.setId(id);
        entity.setComplaintNumber(complaintNumber);
        entity.setCitizenUserId(citizenUserId);
        entity.setPlaceOfIncident(placeOfIncident);
        entity.setPetAnimalName(petAnimalName);
        entity.setComplaintDescription(complaintDescription);
        entity.setIncidentDate(incidentDate);
        entity.setPhotoPath(photoPath);
        entity.setVideoPath(videoPath);
        entity.setDocumentPath(documentPath);

        if (ValidationUtils.isValid(statusId)) {
            entity.setStatus(new ApplicationStatusMaster(statusId));
        }

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

        return getErrors() == null
                || getErrors().isEmpty();
    }
}