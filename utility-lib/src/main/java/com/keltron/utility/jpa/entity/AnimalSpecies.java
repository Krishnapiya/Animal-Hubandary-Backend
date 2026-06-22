package com.keltron.utility.jpa.entity;

import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.AnimalSpeciesDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "animal_species", schema = "master")
public class AnimalSpecies extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "species_name")
    private String speciesName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {
        AnimalSpeciesDto animalDto = (AnimalSpeciesDto) dto;

        this.id = animalDto.getId();
        this.speciesName = animalDto.getSpeciesName();
        this.isActive = animalDto.getIsActive();
    }

    @Override
    public AnimalSpeciesDto toDTO() {

        AnimalSpeciesDto dto = new AnimalSpeciesDto();

        dto.setId(id);
        dto.setSpeciesName(speciesName);
        dto.setIsActive(isActive);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(speciesName);

        return payload;
    }
}