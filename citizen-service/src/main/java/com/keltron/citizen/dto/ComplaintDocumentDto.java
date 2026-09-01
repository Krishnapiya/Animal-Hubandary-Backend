package com.keltron.citizen.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.citizen.entity.ComplaintDocument;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.jpa.entity.Users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintDocumentDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long complaintId;
    private String complaintNumber;

    private String uploadedByName;

    private Long documentTypeId;
    private String documentTypeName;

    private String fileName;

    private String filePath;

    private String mimeType;

    private Long fileSizeBytes;

    private Long uploadedBy;

    private LocalDateTime uploadedAt;

    @Override
    public ComplaintDocument toEntity() {

        ComplaintDocument entity =
                new ComplaintDocument();

        if (ValidationUtils.isValid(id)) {
            entity.setId(id);
        }

        if (ValidationUtils.isValid(complaintId)) {
            entity.setComplaint(
                    new com.keltron.citizen.entity.ComplaintRegistration(
                            complaintId));
        }

        if (ValidationUtils.isValid(documentTypeId)) {
            entity.setDocumentType(
                    new DocumentType(documentTypeId));
        }

        entity.setFileName(fileName);
        entity.setFilePath(filePath);
        entity.setMimeType(mimeType);
        entity.setFileSizeBytes(fileSizeBytes);

        if (ValidationUtils.isValid(uploadedBy)) {
            entity.setUploadedBy(
                    new Users(uploadedBy));
        }

        entity.setUploadedAt(uploadedAt);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.POST)) {

            if (!ValidationUtils.isValid(complaintId))
                addError("complaintId", complaintId);

            if (!ValidationUtils.isValid(documentTypeId))
                addError("documentTypeId", documentTypeId);

            if (!ValidationUtils.isValid(fileName))
                addError("fileName", fileName);

            if (!ValidationUtils.isValid(filePath))
                addError("filePath", filePath);

            if (!ValidationUtils.isValid(uploadedBy))
                addError("uploadedBy", uploadedBy);

            if (uploadedAt == null)
                addError("uploadedAt", uploadedAt);
        }

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id))
                addError("id", id);
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}