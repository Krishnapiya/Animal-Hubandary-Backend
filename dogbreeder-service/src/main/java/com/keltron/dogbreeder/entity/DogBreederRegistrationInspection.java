package com.keltron.dogbreeder.entity;

import java.time.LocalDate;

import com.keltron.dogbreeder.dto.DogBreederRegistrationInspectionDto;
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
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "dogbreeder_registration_inspection", schema = "awb")
public class DogBreederRegistrationInspection extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private DogBreederRegistrationApplication application;

    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @Column(name = "inspection_remarks")
    private String inspectionRemarks;

    @Column(name = "inspection_report")
    private String inspectionReport;

    @Column(name = "recommendation")
    private String recommendation;


    public DogBreederRegistrationInspection(Long id) {
        this.id = id;
    }

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DogBreederRegistrationInspectionDto inspectionDto =
                (DogBreederRegistrationInspectionDto) dto;

        if (ValidationUtils.isValid(inspectionDto.getId())) {
            this.id = inspectionDto.getId();
        }

        if (ValidationUtils.isValid(inspectionDto.getApplicationId())) {
            this.application =
                    new DogBreederRegistrationApplication(
                            inspectionDto.getApplicationId());
        }

        this.inspectionDate = inspectionDto.getInspectionDate();
        this.inspectionRemarks = inspectionDto.getInspectionRemarks();
        this.inspectionReport = inspectionDto.getInspectionReport();
        this.recommendation = inspectionDto.getRecommendation();
    }

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederRegistrationInspectionDto toDTO() {

        DogBreederRegistrationInspectionDto dto =
                new DogBreederRegistrationInspectionDto();

        dto.setId(id);

        if (application != null) {
            dto.setApplicationId(application.getId());
        }

        dto.setInspectionDate(inspectionDate);
        dto.setInspectionRemarks(inspectionRemarks);
        dto.setInspectionReport(inspectionReport);
        dto.setRecommendation(recommendation);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(
                inspectionDate != null
                        ? inspectionDate.toString()
                        : "Inspection");

        return payload;
    }
}