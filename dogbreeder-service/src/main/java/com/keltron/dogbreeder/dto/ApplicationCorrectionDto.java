package com.keltron.dogbreeder.dto;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.ApplicationCorrection;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.Users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationCorrectionDto extends AbstractDto {

    private static final long serialVersionUID = 1L;
    private String submittedByName;
    private Long id;
    private Long applicationId;
    private String correctionSummary;
    private Long submittedBy;

    @Override
    public ApplicationCorrection toEntity() {

        ApplicationCorrection entity =
                new ApplicationCorrection();

        entity.setId(id);
        entity.setApplicationId(applicationId);
        entity.setCorrectionSummary(correctionSummary);
        if (submittedBy != null) {
            entity.setSubmittedBy(
                    new Users(submittedBy));
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