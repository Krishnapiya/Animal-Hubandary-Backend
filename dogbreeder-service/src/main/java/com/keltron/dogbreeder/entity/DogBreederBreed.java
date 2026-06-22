package com.keltron.dogbreeder.entity;

import com.keltron.dogbreeder.dto.DogBreederBreedDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
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
@Table(name = "dog_breeder_breed", schema = "awb")
@Entity
@ToString
@NoArgsConstructor
public class DogBreederBreed extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dog_breeder_detail_id", referencedColumnName = "id", nullable = false)
    private DogBreederDetail dogBreederDetail;

    @Column(name = "breed_name", nullable = false, length = 200)
    private String breedName;

    @Column(name = "dog_count", nullable = false)
    private Integer dogCount = 0;
    
    @Column(name = "age_description", length = 200)
    private String ageDescription;


    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DogBreederBreedDto breedDto = (DogBreederBreedDto) dto;

        if (ValidationUtils.isValid(breedDto.getId())) {
            id = breedDto.getId();
        }

        if (breedDto.getDogBreederDetail() != null
                && breedDto.getDogBreederDetail().getId() != null) {
            dogBreederDetail = new DogBreederDetail(breedDto.getDogBreederDetail().getId());
        }

        if (ValidationUtils.isValid(breedDto.getBreedName())) {
            breedName = breedDto.getBreedName();
        }

        if (breedDto.getDogCount() != null) {
            dogCount = breedDto.getDogCount();
        }
        this.ageDescription = breedDto.getAgeDescription();

    }

    @SuppressWarnings("unchecked")
    @Override
    public DogBreederBreedDto toDTO() {

        DogBreederBreedDto dto = new DogBreederBreedDto();

        dto.setId(id);
        dto.setBreedName(breedName);
        dto.setDogCount(dogCount);

        if (dogBreederDetail != null) {
            DropdownPayload<Long> detailPayload = new DropdownPayload<>();
            detailPayload.setId(dogBreederDetail.getId());
            detailPayload.setName(dogBreederDetail.getBreederName());
            dto.setDogBreederDetail(detailPayload);
        }
        dto.setAgeDescription(ageDescription);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();
        payload.setId(id);
        payload.setName(breedName);

        return payload;
    }

    public DogBreederBreed(Long id) {
        this.id = id;
    }
}