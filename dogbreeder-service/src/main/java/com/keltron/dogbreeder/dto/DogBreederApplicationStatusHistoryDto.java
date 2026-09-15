package com.keltron.dogbreeder.dto;

import java.sql.Timestamp;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederApplicationStatusHistory;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederApplicationStatusHistoryDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private DropdownPayload<Long> application;

    private ApplicationStatus fromStatus;

    private ApplicationStatus toStatus;

    private String changedBy;

    private Long changedAt;

    private String remarks;

    private String actionType;

    /*
     * Audit fields from DB
     */
    private String createdBy;

    private Long createdAt;

    private String lastModifiedBy;

    private Long lastModifiedAt;

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederApplicationStatusHistory toEntity() {

        DogBreederApplicationStatusHistory entity =
                new DogBreederApplicationStatusHistory();

        if (ValidationUtils.isValid(id)) {
            entity.setId(id);
        }

        if (ValidationUtils.isValid(application)) {
            entity.setApplication(
                    new RegistrationApplication(application.getId()));
        }

        entity.setFromStatus(fromStatus);
        entity.setToStatus(toStatus);
        entity.setChangedBy(changedBy);
        entity.setRemarks(remarks);
        entity.setActionType(actionType);

        if (changedAt != null) {
            entity.setChangedAt(
                    new Timestamp(changedAt));
        }

        entity.setCreatedBy(createdBy);

        if (createdAt != null) {
            entity.setCreatedAt(
                    new Timestamp(createdAt));
        }

        entity.setLastModifiedBy(lastModifiedBy);

        if (lastModifiedAt != null) {
            entity.setLastModifiedAt(
                    new Timestamp(lastModifiedAt));
        }

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod == HttpMethod.POST
                || httpMethod == HttpMethod.PATCH) {

            if (application == null
                    || !ValidationUtils.isValid(
                            application.getId())) {

                addError(
                        "application",
                        application);
            }

            if (toStatus == null) {
                addError(
                        "toStatus",
                        toStatus);
            }
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}