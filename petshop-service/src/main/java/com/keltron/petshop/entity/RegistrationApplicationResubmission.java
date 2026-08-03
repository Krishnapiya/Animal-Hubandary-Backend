package com.keltron.petshop.entity;

import java.time.LocalDateTime;

import com.keltron.petshop.dto.RegistrationApplicationResubmissionDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "registration_application_resubmission",
        schema = "awb")
public class RegistrationApplicationResubmission
        extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private PetShopRegistrationApplication application;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "documents", columnDefinition = "text[]")
    private String[] documents;

    @Column(name = "resubmitted_at")
    private LocalDateTime resubmittedAt;

    @Column(name = "resubmitted_by")
    private Long resubmittedBy;

    public RegistrationApplicationResubmission(Long id) {
        this.id = id;
    }

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        RegistrationApplicationResubmissionDto resubmissionDto =
                (RegistrationApplicationResubmissionDto) dto;

        if (ValidationUtils.isValid(resubmissionDto.getId())) {
            this.id = resubmissionDto.getId();
        }

        if (ValidationUtils.isValid(
                resubmissionDto.getApplicationId())) {

            this.application =
                    new PetShopRegistrationApplication(
                            resubmissionDto.getApplicationId());
        }

        this.remarks = resubmissionDto.getRemarks();
        this.documents = resubmissionDto.getDocuments();
        this.resubmittedAt =
                resubmissionDto.getResubmittedAt();
        this.resubmittedBy =
                resubmissionDto.getResubmittedBy();
    }

    @Override
    public RegistrationApplicationResubmissionDto toDTO() {

        RegistrationApplicationResubmissionDto dto =
                new RegistrationApplicationResubmissionDto();

        dto.setId(id);

        if (application != null) {
            dto.setApplicationId(application.getId());
        }

        dto.setRemarks(remarks);
        dto.setDocuments(documents);
        dto.setResubmittedAt(resubmittedAt);
        dto.setResubmittedBy(resubmittedBy);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName("Resubmission " + id);

        return payload;
    }
}