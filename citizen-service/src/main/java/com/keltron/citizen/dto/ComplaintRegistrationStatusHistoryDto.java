package com.keltron.citizen.dto;

import org.springframework.http.HttpMethod;

import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.citizen.entity.ComplaintRegistrationStatusHistory;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRegistrationStatusHistoryDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private DropdownPayload<Long> complaint;

    private String fromStatus;

    private String toStatus;

    private String changedBy;

    private Long changedAt;

    private String remarks;

    private String actionType;

    @Override
    public ComplaintRegistrationStatusHistory toEntity() {

        ComplaintRegistrationStatusHistory entity =
                new ComplaintRegistrationStatusHistory();

        if (ValidationUtils.isValid(id))
            entity.setId(id);

        if (ValidationUtils.isValid(complaint))
            entity.setComplaint(
                    new ComplaintRegistration(
                            complaint.getId()));

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

            if (complaint == null
                    || !ValidationUtils.isValid(complaint.getId())) {

                addError("complaint", complaint);
            }

            if (!ValidationUtils.isValid(toStatus)) {

                addError("toStatus", toStatus);
            }
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}