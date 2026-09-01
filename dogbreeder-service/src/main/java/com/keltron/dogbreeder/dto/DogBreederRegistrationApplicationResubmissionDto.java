package com.keltron.dogbreeder.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplicationResubmission;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederRegistrationApplicationResubmissionDto
        extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long applicationId;

    private String remarks;

    private String[] documents;

    private LocalDateTime resubmittedAt;

    private Long resubmittedBy;

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederRegistrationApplicationResubmission toEntity() {

        DogBreederRegistrationApplicationResubmission entity =
                new DogBreederRegistrationApplicationResubmission();

        entity.copyFromDTO(this);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod.equals(HttpMethod.PATCH) || httpMethod.equals(HttpMethod.POST)) {

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

        return "DogBreederRegistrationApplicationResubmissionDto{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", remarks='" + remarks + '\'' +
                ", resubmittedBy=" + resubmittedBy +
                '}';
    }
}