package com.keltron.citizen.entity;

import java.time.LocalDate;

import com.keltron.citizen.dto.ComplaintInvestigationDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "complaint_investigation", schema = "awb")
@NoArgsConstructor
@ToString
public class ComplaintInvestigation extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private ComplaintRegistration complaint;

    @Column(name = "investigation_date")
    private LocalDate investigationDate;

    @Column(name = "investigation_remarks")
    private String investigationRemarks;

    @Column(name = "investigation_report_path")
    private String investigationReportPath;

    @Column(name = "report_remarks")
    private String reportRemarks;

    @Column(name = "recommendation")
    private String recommendation;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        ComplaintInvestigationDto investigationDto =
                (ComplaintInvestigationDto) dto;

        if (ValidationUtils.isValid(
                investigationDto.getId())) {

            this.id = investigationDto.getId();
        }

        if (ValidationUtils.isValid(
                investigationDto.getInvestigationDate())) {

            this.investigationDate =
                    investigationDto.getInvestigationDate();
        }

        if (ValidationUtils.isValid(
                investigationDto.getInvestigationRemarks())) {

            this.investigationRemarks =
                    investigationDto.getInvestigationRemarks();
        }

        if (ValidationUtils.isValid(
                investigationDto.getInvestigationReportPath())) {

            this.investigationReportPath =
                    investigationDto.getInvestigationReportPath();
        }

        if (ValidationUtils.isValid(
                investigationDto.getReportRemarks())) {

            this.reportRemarks =
                    investigationDto.getReportRemarks();
        }

        if (ValidationUtils.isValid(
                investigationDto.getRecommendation())) {

            this.recommendation =
                    investigationDto.getRecommendation();
        }

        if (ValidationUtils.isValid(
                investigationDto.getComplaintId())) {

            this.complaint =
                    new ComplaintRegistration(
                            investigationDto.getComplaintId());
        }
    }

    @Override
    public ComplaintInvestigationDto toDTO() {

        ComplaintInvestigationDto dto =
                new ComplaintInvestigationDto();

        dto.setId(id);

        if (complaint != null) {

            dto.setComplaintId(
                    complaint.getId());
        }

        dto.setInvestigationDate(
                investigationDate);

        dto.setInvestigationRemarks(
                investigationRemarks);

        dto.setInvestigationReportPath(
                investigationReportPath);

        dto.setReportRemarks(
                reportRemarks);

        dto.setRecommendation(
                recommendation);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);

        return payload;
    }

    public ComplaintInvestigation(Long id) {
        this.id = id;
    }
}