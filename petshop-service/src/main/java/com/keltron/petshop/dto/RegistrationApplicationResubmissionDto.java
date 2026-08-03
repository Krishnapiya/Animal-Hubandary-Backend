package com.keltron.petshop.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.RegistrationApplicationResubmission;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationApplicationResubmissionDto
        extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long applicationId;

    private String remarks;

    private String[] documents;

    private LocalDateTime resubmittedAt;

    private Long resubmittedBy;

    @Override
    public RegistrationApplicationResubmission toEntity() {

        RegistrationApplicationResubmission entity =
                new RegistrationApplicationResubmission();

        entity.copyFromDTO(this);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(applicationId)) {
                addError("applicationId", applicationId);
            }

            if (documents == null || documents.length == 0) {
                addError("documents", documents);
            }
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }

    @Override
    public String toString() {

        return "RegistrationApplicationResubmissionDto{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", remarks='" + remarks + '\'' +
                ", resubmittedBy=" + resubmittedBy +
                '}';
    }
}