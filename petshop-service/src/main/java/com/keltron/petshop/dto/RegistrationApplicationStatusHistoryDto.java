package com.keltron.petshop.dto;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.entity.RegistrationApplicationStatusHistory;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationApplicationStatusHistoryDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;
    private DropdownPayload<Long> application;
    private ApplicationStatus fromStatus;
    private ApplicationStatus toStatus;
    private String changedBy;
    private Long changedAt;
    private String remarks;
    private String actionType;

    @Override
    public RegistrationApplicationStatusHistory toEntity() {

        RegistrationApplicationStatusHistory entity =
                new RegistrationApplicationStatusHistory();

        if (ValidationUtils.isValid(id))
            entity.setId(id);

        if (ValidationUtils.isValid(application))
            entity.setApplication(
                    new PetShopRegistrationApplication(application.getId()));

        entity.setFromStatus(fromStatus);
        entity.setToStatus(toStatus);
        entity.setChangedBy(changedBy);
        entity.setRemarks(remarks);
        entity.setActionType(actionType);

        if (changedAt != null) {
            entity.setChangedAt(
                    new java.sql.Timestamp(changedAt));
        }

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod == HttpMethod.POST
                || httpMethod == HttpMethod.PATCH) {

            if (application == null
                    || !ValidationUtils.isValid(application.getId())) {
                addError("application", application);
            }

            if (toStatus == null) {
                addError("toStatus", toStatus);
            }
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}