package com.keltron.petshop.entity;

import java.time.LocalDate;

import com.keltron.petshop.dto.RegistrationInspectionDto;
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
@Entity
@Table(name = "registration_inspection", schema = "awb")
@NoArgsConstructor
@ToString
public class RegistrationInspection extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private PetShopRegistrationApplication application;

    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @Column(name = "inspection_remarks")
    private String inspectionRemarks;

    @Column(name = "inspection_report")
    private String inspectionReport;

    @Column(name = "recommendation")
    private String recommendation;

//    @Column(name = "status")
//    private String status;

    

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        RegistrationInspectionDto inspectionDto =
                (RegistrationInspectionDto) dto;

        if (ValidationUtils.isValid(inspectionDto.getId())) {
            this.id = inspectionDto.getId();
        }

        if (ValidationUtils.isValid(
                inspectionDto.getApplicationId())) {

            this.application =
                    new PetShopRegistrationApplication(
                            inspectionDto.getApplicationId());
        }

        this.inspectionDate =
                inspectionDto.getInspectionDate();

        this.inspectionRemarks =
                inspectionDto.getInspectionRemarks();

        this.inspectionReport =
                inspectionDto.getInspectionReport();

        this.recommendation =
                inspectionDto.getRecommendation();

//        this.status =
//                inspectionDto.getStatus();

        
    }

    @Override
    public RegistrationInspectionDto toDTO() {

        RegistrationInspectionDto dto =
                new RegistrationInspectionDto();

        dto.setId(id);

        if (application != null) {
            dto.setApplicationId(application.getId());
        }

        dto.setInspectionDate(inspectionDate);
        dto.setInspectionRemarks(inspectionRemarks);
        dto.setInspectionReport(inspectionReport);
        dto.setRecommendation(recommendation);
//        dto.setStatus(status);
      

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(
                inspectionDate != null
                        ? inspectionDate.toString()
                        : "Inspection");

        return payload;
    }

    public RegistrationInspection(Long id) {
        this.id = id;
    }
}