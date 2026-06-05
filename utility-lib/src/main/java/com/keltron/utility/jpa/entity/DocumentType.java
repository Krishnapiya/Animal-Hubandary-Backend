package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.DocumentTypeDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "document_type", schema = "awb")
@NoArgsConstructor
@ToString
public class DocumentType extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "entity_scope", nullable = false)
    private String entityScope;

    @Column(nullable = false)
    private Boolean mandatory;

    @Column(nullable = false)
    private Boolean active;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DocumentTypeDto documentDto = (DocumentTypeDto) dto;

        if (ValidationUtils.isValid(documentDto.getId()))
            id = documentDto.getId();

        if (ValidationUtils.isValid(documentDto.getCode()))
            code = documentDto.getCode();

        if (ValidationUtils.isValid(documentDto.getName()))
            name = documentDto.getName();

        if (ValidationUtils.isValid(documentDto.getEntityScope()))
            entityScope = documentDto.getEntityScope();

        if (documentDto.getMandatory() != null)
            mandatory = documentDto.getMandatory();

        if (documentDto.getActive() != null)
            active = documentDto.getActive();
    }

    @Override
    public DocumentTypeDto toDTO() {

        DocumentTypeDto dto = new DocumentTypeDto();

        dto.setId(id);
        dto.setCode(code);
        dto.setName(name);
        dto.setEntityScope(entityScope);
        dto.setMandatory(mandatory);
        dto.setActive(active);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(name);

        return payload;
    }
}