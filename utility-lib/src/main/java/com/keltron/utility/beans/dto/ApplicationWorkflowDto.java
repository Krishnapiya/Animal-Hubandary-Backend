package com.keltron.utility.beans.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.ApplicationWorkflow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationWorkflowDto extends AbstractDto {

    private Long id;

    private String moduleName;
    private Long applicationId;

    private Long fromStatusId;
    private Long toStatusId;

    private Long actionBy;

    private String remarks;

    private LocalDateTime actionDate;

    @Override
    public ApplicationWorkflow toEntity() {
        ApplicationWorkflow entity =
                new ApplicationWorkflow();

        entity.copyFromDTO(this);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod method) {

        if (method == HttpMethod.PATCH &&
                !ValidationUtils.isValid(id)) {

            addError("id", id);
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}