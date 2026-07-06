package com.keltron.petshop.entity;

import java.time.LocalDateTime;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

// ***** CHANGED *****
// OLD:
// import com.keltron.utility.beans.dto.ApplicationDocumentDto;
import com.keltron.petshop.dto.PetShopApplicationDocumentDto;

// ***** NEW *****
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.jpa.entity.Users;

import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "application_document", schema = "awb")
@Entity
@ToString
@NoArgsConstructor
public class PetShopApplicationDocument extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private RegistrationApplication application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @Column(name = "mime_type", length = 120)
    private String mimeType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploaded_by")
    private Users uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        // ***** CHANGED *****
        PetShopApplicationDocumentDto documentDto =
                (PetShopApplicationDocumentDto) dto;

        if (ValidationUtils.isValid(documentDto.getId()))
            id = documentDto.getId();

        if (ValidationUtils.isValid(documentDto.getApplicationId()))
            application =
                    new RegistrationApplication(documentDto.getApplicationId());

        if (ValidationUtils.isValid(documentDto.getDocumentTypeId()))
            documentType =
                    new DocumentType(documentDto.getDocumentTypeId());

        if (ValidationUtils.isValid(documentDto.getFileName()))
            fileName = documentDto.getFileName();

        if (ValidationUtils.isValid(documentDto.getFilePath()))
            filePath = documentDto.getFilePath();

        if (ValidationUtils.isValid(documentDto.getMimeType()))
            mimeType = documentDto.getMimeType();

        if (ValidationUtils.isValid(documentDto.getFileSizeBytes()))
            fileSizeBytes = documentDto.getFileSizeBytes();

        if (ValidationUtils.isValid(documentDto.getUploadedBy()))
            uploadedBy = new Users(documentDto.getUploadedBy());

        if (documentDto.getUploadedAt() != null)
            uploadedAt = documentDto.getUploadedAt();
    }

    @SuppressWarnings("unchecked")
    @Override

    // ***** CHANGED *****
    public PetShopApplicationDocumentDto toDTO() {

        // ***** CHANGED *****
        PetShopApplicationDocumentDto dto =
                new PetShopApplicationDocumentDto();

        dto.setId(id);

        if (application != null) {
            dto.setApplicationId(application.getId());
            dto.setApplicationNumber(application.getApplicationNumber());
        }

        if (documentType != null) {

            dto.setDocumentTypeId(documentType.getId());

            // ***** NOTE *****
            // If this line gives an error,
            // send me DocumentType.java
            dto.setDocumentTypeName(documentType.getName());
        }

        dto.setFileName(fileName);
        dto.setFilePath(filePath);
        dto.setMimeType(mimeType);
        dto.setFileSizeBytes(fileSizeBytes);

        if (uploadedBy != null) {

            dto.setUploadedBy(uploadedBy.getId());

            String name = "";

            if (uploadedBy.getFname() != null)
                name += uploadedBy.getFname();

            if (uploadedBy.getLname() != null)
                name += " " + uploadedBy.getLname();

            name = name.trim();

            if (name.isBlank() && uploadedBy.getUsername() != null)
                name = uploadedBy.getUsername();

            dto.setUploadedByName(name);
        }

        dto.setUploadedAt(uploadedAt);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(fileName);

        return payload;
    }

    public PetShopApplicationDocument(Long id) {
        this.id = id;
    }
}