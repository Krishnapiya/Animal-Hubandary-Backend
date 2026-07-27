package com.keltron.petshop.dto;

import java.time.LocalDate;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.RegistrationInspection;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationInspectionDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long applicationId;

    private LocalDate inspectionDate;

    private String inspectionRemarks;

    private String inspectionReport;

    private String recommendation;

    private String status;

 

    @Override
    public RegistrationInspection toEntity() {

        RegistrationInspection entity =
                new RegistrationInspection();

        entity.setId(id);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

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

        return getErrors() == null
                || getErrors().isEmpty();
    }

    @Override
    public String toString() {

        return "RegistrationInspectionDto{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", inspectionDate=" + inspectionDate +
                ", recommendation='" + recommendation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}