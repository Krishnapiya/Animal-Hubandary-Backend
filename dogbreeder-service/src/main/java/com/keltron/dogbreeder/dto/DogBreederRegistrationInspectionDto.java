package com.keltron.dogbreeder.dto;

import java.time.LocalDate;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederRegistrationInspection;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class DogBreederRegistrationInspectionDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long applicationId;

    private LocalDate inspectionDate;

    private String inspectionRemarks;

    private String inspectionReport;

    private String recommendation;

   

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederRegistrationInspection toEntity() {

        DogBreederRegistrationInspection entity =
                new DogBreederRegistrationInspection();

        entity.setId(id);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.POST)) {

            if (!ValidationUtils.isValid(applicationId)) {
                addError("applicationId", applicationId);
            }

            if (inspectionDate == null) {
                addError("inspectionDate", inspectionDate);
            }
        }

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }

    @Override
    public String toString() {
        return "DogBreederRegistrationInspectionDto{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", inspectionDate=" + inspectionDate +
                ", inspectionRemarks='" + inspectionRemarks + '\'' +
                ", inspectionReport='" + inspectionReport + '\'' +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}