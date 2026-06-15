package com.keltron.dogbreeder.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.responses.payload.DropdownPayload;
import com.keltron.dogbreeder.dto.ApplicationCorrectionDto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.keltron.utility.jpa.entity.Users;
@Getter
@Setter
@Entity
@Table(name = "application_correction", schema = "awb")
@NoArgsConstructor
@ToString
public class ApplicationCorrection extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "correction_summary", nullable = false)
    private String correctionSummary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "submitted_by", referencedColumnName = "id")
    private Users submittedBy;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        ApplicationCorrectionDto applicationCorrectionDto =
                (ApplicationCorrectionDto) dto;

        if (ValidationUtils.isValid(
                applicationCorrectionDto.getId()))
            this.id =
                    applicationCorrectionDto.getId();

        if (ValidationUtils.isValid(
                applicationCorrectionDto.getApplicationId()))
            this.applicationId =
                    applicationCorrectionDto.getApplicationId();

        if (ValidationUtils.isValid(
                applicationCorrectionDto.getCorrectionSummary()))
            this.correctionSummary =
                    applicationCorrectionDto.getCorrectionSummary();

        if (ValidationUtils.isValid(
                applicationCorrectionDto.getSubmittedBy())) {
            this.submittedBy =
                    new Users(
                            applicationCorrectionDto.getSubmittedBy());
        }
    }

    @Override
    public ApplicationCorrectionDto toDTO() {

        ApplicationCorrectionDto dto =
                new ApplicationCorrectionDto();

        dto.setId(id);
        dto.setApplicationId(applicationId);
        dto.setCorrectionSummary(correctionSummary);
        if (submittedBy != null) {
            dto.setSubmittedBy(submittedBy.getId());
            dto.setSubmittedByName(
                    submittedBy.getFname() + " " +
                    submittedBy.getLname());
        }

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(correctionSummary);

        return payload;
    }

    public ApplicationCorrection(Long id) {
        this.id = id;
    }
}