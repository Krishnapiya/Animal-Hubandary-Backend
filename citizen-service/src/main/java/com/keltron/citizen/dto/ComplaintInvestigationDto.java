package com.keltron.citizen.dto;

import java.time.LocalDate;

import org.springframework.http.HttpMethod;

import com.keltron.citizen.entity.ComplaintInvestigation;
import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComplaintInvestigationDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long complaintId;

    private LocalDate investigationDate;

    private String investigationRemarks;

    private String investigationReportPath;

    private String reportRemarks;

    private String recommendation;

    @Override
    public ComplaintInvestigation toEntity() {

        ComplaintInvestigation entity =
                new ComplaintInvestigation();

        entity.setId(id);

        entity.setInvestigationDate(
                investigationDate);

        entity.setInvestigationRemarks(
                investigationRemarks);

        entity.setInvestigationReportPath(
                investigationReportPath);

        entity.setReportRemarks(
                reportRemarks);

        entity.setRecommendation(
                recommendation);

        if (ValidationUtils.isValid(complaintId)) {

            entity.setComplaint(
                    new ComplaintRegistration(complaintId));
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