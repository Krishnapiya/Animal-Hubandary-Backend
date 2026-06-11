package com.keltron.utility.beans.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationApplicationDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String applicationNumber;
    private String entityType;
    private String applicationKind;

    private Long statusId;
    private Long applicantUserId;
    private Integer districtId;
    private Long assignedOfficeId;
    private Long cvOfficeId;
    private Long paymentId;
    private Long forwardedToCvoBy;

    private LocalDateTime submittedAt;
    private LocalDateTime forwardedToCvoAt;
    private DropdownPayload<Long> status;
    private DropdownPayload<Integer> district;

    @Override
    public RegistrationApplication toEntity() {

        RegistrationApplication entity =
                new RegistrationApplication();

        entity.setId(id);
        entity.setApplicationNumber(applicationNumber);
        entity.setEntityType(entityType);
        entity.setApplicationKind(applicationKind);

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
    @Override
    public String toString() {
        return "RegistrationApplicationDto{" +
                "id=" + id +
                ", applicationNumber='" + applicationNumber + '\'' +
                ", statusId=" + statusId +
                ", districtId=" + districtId +
                ", applicantUserId=" + applicantUserId +
                ", assignedOfficeId=" + assignedOfficeId +
                ", cvOfficeId=" + cvOfficeId +
                '}';
    }
}