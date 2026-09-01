package com.keltron.citizen.entity;

import java.time.LocalDate;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.keltron.citizen.dto.ComplaintRegistrationDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "complaint_registration", schema = "awb")
@NoArgsConstructor
@ToString
public class ComplaintRegistration extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_number")
    private String complaintNumber;

    @Column(name = "citizen_user_id")
    private Long citizenUserId;

    @Column(name = "place_of_incident")
    private String placeOfIncident;

    @Column(name = "pet_animal_name")
    private String petAnimalName;

    @Column(name = "complaint_description")
    private String complaintDescription;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "document_path")
    private String documentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private ApplicationStatusMaster status;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        ComplaintRegistrationDto complaintDto =
                (ComplaintRegistrationDto) dto;

        if (ValidationUtils.isValid(complaintDto.getId()))
            this.id = complaintDto.getId();

        if (ValidationUtils.isValid(complaintDto.getComplaintNumber()))
            this.complaintNumber = complaintDto.getComplaintNumber();

        if (ValidationUtils.isValid(complaintDto.getCitizenUserId()))
            this.citizenUserId = complaintDto.getCitizenUserId();

        if (ValidationUtils.isValid(complaintDto.getPlaceOfIncident()))
            this.placeOfIncident = complaintDto.getPlaceOfIncident();

        if (ValidationUtils.isValid(complaintDto.getPetAnimalName()))
            this.petAnimalName = complaintDto.getPetAnimalName();

        if (ValidationUtils.isValid(complaintDto.getComplaintDescription()))
            this.complaintDescription = complaintDto.getComplaintDescription();

        if (ValidationUtils.isValid(complaintDto.getIncidentDate()))
            this.incidentDate = complaintDto.getIncidentDate();

        if (ValidationUtils.isValid(complaintDto.getPhotoPath()))
            this.photoPath = complaintDto.getPhotoPath();

        if (ValidationUtils.isValid(complaintDto.getVideoPath()))
            this.videoPath = complaintDto.getVideoPath();

        if (ValidationUtils.isValid(complaintDto.getDocumentPath()))
            this.documentPath = complaintDto.getDocumentPath();

        if (ValidationUtils.isValid(complaintDto.getStatusId())) {
            this.status = new ApplicationStatusMaster(complaintDto.getStatusId());
        }
    }

    @Override
    public ComplaintRegistrationDto toDTO() {

        ComplaintRegistrationDto dto =
                new ComplaintRegistrationDto();

        dto.setId(id);
        dto.setComplaintNumber(complaintNumber);
        dto.setCitizenUserId(citizenUserId);
        dto.setPlaceOfIncident(placeOfIncident);
        dto.setPetAnimalName(petAnimalName);
        dto.setComplaintDescription(complaintDescription);
        dto.setIncidentDate(incidentDate);
        dto.setPhotoPath(photoPath);
        dto.setVideoPath(videoPath);
        dto.setDocumentPath(documentPath);
        if (status != null) {
            dto.setStatusId(status.getId());
            dto.setStatus(status.toDropDownPayload());
        }

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(complaintNumber);

        return payload;
    }

    public ComplaintRegistration(Long id) {
        this.id = id;
    }
}