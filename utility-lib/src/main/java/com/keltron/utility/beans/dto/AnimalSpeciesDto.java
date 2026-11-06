package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AnimalSpecies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalSpeciesDto extends AbstractDto {

    private Long id;
    private String speciesName;
    private Boolean isActive;
    private Long parentId;

    @Override
    public AnimalSpecies toEntity() {

        AnimalSpecies entity = new AnimalSpecies();

        entity.setId(id);
        entity.setSpeciesName(speciesName);
        entity.setIsActive(isActive);
        entity.setParentId(parentId);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod method) {

        if (!ValidationUtils.isValid(speciesName)) {
            addError("speciesName", speciesName);
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}