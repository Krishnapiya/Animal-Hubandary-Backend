package com.keltron.dogbreeder.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.keltron.dogbreeder.dto.DogBreederBreedDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(
        name = "dog_breeder_breed",
        schema = "awb"
)
@Entity
@ToString
@NoArgsConstructor
public class DogBreederBreed extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /*
     * ============================================================
     * PRIMARY KEY
     * ============================================================
     */

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            unique = true,
            nullable = false
    )
    private Long id;

    /*
     * ============================================================
     * DOG BREEDER DETAIL
     * ============================================================
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "dog_breeder_detail_id",
            referencedColumnName = "id",
            nullable = false
    )
    private DogBreederDetail dogBreederDetail;

    /*
     * ============================================================
     * BREED NAME
     * ============================================================
     */

    @Column(
            name = "breed_name",
            nullable = false,
            length = 200
    )
    private String breedName;

    /*
     * ============================================================
     * NUMBER OF DOGS
     * ============================================================
     */

    @Column(
            name = "dog_count",
            nullable = false
    )
    private Integer dogCount = 0;

    /*
     * ============================================================
     * AGE DESCRIPTION
     * ============================================================
     */

    @Column(
            name = "age_description",
            length = 255
    )
    private String ageDescription;

    /*
     * ============================================================
     * GENDER
     * ============================================================
     */

    @Column(
            name = "gender",
            length = 20
    )
    private String gender;

    /*
     * ============================================================
     * ANIMAL SPECIES
     * ============================================================
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "species_id",
            referencedColumnName = "id"
    )
    private AnimalSpecies species;

    /*
     * ============================================================
     * CUSTOM SPECIES NAME
     * ============================================================
     */

    @Column(
            name = "custom_species_name",
            length = 120
    )
    private String customSpeciesName;

    /*
     * ============================================================
     * CUSTOM BREED NAME
     * ============================================================
     */

    @Column(
            name = "custom_breed_name",
            length = 120
    )
    private String customBreedName;

    /*
     * ============================================================
     * DESCRIPTION
     * ============================================================
     */

    @Column(
            name = "description",
            columnDefinition = "text"
    )
    private String description;

    /*
     * ============================================================
     * PRICE OFFERED
     * ============================================================
     */

    @Column(
            name = "price_offered",
            precision = 12,
            scale = 2
    )
    private BigDecimal priceOffered;

    /*
     * ============================================================
     * DISPLAY ORDER
     * ============================================================
     */

    @Column(
            name = "display_order",
            nullable = false
    )
    private Integer displayOrder = 0;

    /*
     * ============================================================
     * PRE PERSIST
     * ============================================================
     */

    @PrePersist
    public void prePersist() {

        Timestamp now =
                new Timestamp(
                        System.currentTimeMillis()
                );

        if (getCreatedAt() == null) {
            setCreatedAt(now);
        }

        if (getLastModifiedAt() == null) {
            setLastModifiedAt(now);
        }

        if (getCreatedBy() == null
                || getCreatedBy().isBlank()) {
            setCreatedBy("SYSTEM");
        }

        if (getLastModifiedBy() == null
                || getLastModifiedBy().isBlank()) {
            setLastModifiedBy(
                    getCreatedBy()
            );
        }

        if (gender != null) {
            gender = gender.trim().toUpperCase();
        }

        if (breedName != null) {
            breedName = breedName.trim();
        }

        if (customSpeciesName != null) {
            customSpeciesName = customSpeciesName.trim();
        }

        if (customBreedName != null) {
            customBreedName = customBreedName.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (dogCount == null) {
            dogCount = 0;
        }

        if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    /*
     * ============================================================
     * PRE UPDATE
     * ============================================================
     */

    @PreUpdate
    public void preUpdate() {

        setLastModifiedAt(
                new Timestamp(
                        System.currentTimeMillis()
                )
        );

        if (getLastModifiedBy() == null
                || getLastModifiedBy().isBlank()) {
            setLastModifiedBy("SYSTEM");
        }

        if (getCreatedAt() == null) {
            setCreatedAt(
                    new Timestamp(
                            System.currentTimeMillis()
                    )
            );
        }

        if (getCreatedBy() == null
                || getCreatedBy().isBlank()) {
            setCreatedBy("SYSTEM");
        }

        if (gender != null) {
            gender = gender.trim().toUpperCase();
        }

        if (breedName != null) {
            breedName = breedName.trim();
        }

        if (customSpeciesName != null) {
            customSpeciesName = customSpeciesName.trim();
        }

        if (customBreedName != null) {
            customBreedName = customBreedName.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (dogCount == null) {
            dogCount = 0;
        }

        if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    /*
     * ============================================================
     * COPY DTO -> ENTITY
     * ============================================================
     */

    @Override
    public <K extends AbstractDto> void copyFromDTO(
            K dto) {

        DogBreederBreedDto breedDto =
                (DogBreederBreedDto) dto;

        if (ValidationUtils.isValid(
                breedDto.getId())) {
            id = breedDto.getId();
        }

        if (breedDto.getDogBreederDetail() != null
                && breedDto
                        .getDogBreederDetail()
                        .getId() != null) {

            dogBreederDetail =
                    new DogBreederDetail(
                            breedDto
                                    .getDogBreederDetail()
                                    .getId()
                    );
        }

        if (ValidationUtils.isValid(
                breedDto.getBreedName())) {
            breedName =
                    breedDto
                            .getBreedName()
                            .trim();
        }

        if (breedDto.getDogCount() != null) {
            dogCount = breedDto.getDogCount();
        } else {
            dogCount = 0;
        }

        if (breedDto.getAgeDescription() != null) {
            ageDescription =
                    breedDto
                            .getAgeDescription()
                            .trim();
        } else {
            ageDescription = null;
        }

        if (breedDto.getGender() != null) {
            gender =
                    breedDto
                            .getGender()
                            .trim()
                            .toUpperCase();
        } else {
            gender = null;
        }

        if (breedDto.getSpecies() != null
                && breedDto.getSpecies().getId() != null) {
            AnimalSpecies animalSpecies = new AnimalSpecies();
            animalSpecies.setId(breedDto.getSpecies().getId());
            species = animalSpecies;
        } else if (breedDto.getSpeciesId() != null) {
            AnimalSpecies animalSpecies = new AnimalSpecies();
            animalSpecies.setId(breedDto.getSpeciesId());
            species = animalSpecies;
        } else {
            species = null;
        }

        if (ValidationUtils.isValid(breedDto.getCustomSpeciesName())) {
            customSpeciesName = breedDto.getCustomSpeciesName().trim();
        } else {
            customSpeciesName = null;
        }

        if (ValidationUtils.isValid(breedDto.getCustomBreedName())) {
            customBreedName = breedDto.getCustomBreedName().trim();
        } else {
            customBreedName = null;
        }

        if (ValidationUtils.isValid(breedDto.getDescription())) {
            description = breedDto.getDescription().trim();
        } else {
            description = null;
        }

        if (breedDto.getPriceOffered() != null) {
            priceOffered = breedDto.getPriceOffered();
        } else {
            priceOffered = null;
        }

        if (breedDto.getDisplayOrder() != null) {
            displayOrder = breedDto.getDisplayOrder();
        } else if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    /*
     * ============================================================
     * ENTITY -> DTO
     * ============================================================
     */

    @SuppressWarnings("unchecked")
    @Override
    public DogBreederBreedDto toDTO() {

        DogBreederBreedDto dto =
                new DogBreederBreedDto();

        dto.setId(id);
        dto.setBreedName(breedName);
        dto.setDogCount(dogCount);
        dto.setAgeDescription(ageDescription);
        dto.setGender(gender);

        if (dogBreederDetail != null) {
            DropdownPayload<Long>
                    detailPayload =
                    new DropdownPayload<>();

            detailPayload.setId(
                    dogBreederDetail.getId()
            );

            detailPayload.setName(
                    dogBreederDetail.getBreederName()
            );

            dto.setDogBreederDetail(
                    detailPayload
            );
        }

        if (species != null) {
            DropdownPayload<Long> speciesPayload =
                    new DropdownPayload<>();
            speciesPayload.setId(species.getId());
            speciesPayload.setName(species.getSpeciesName());
            dto.setSpecies(speciesPayload);
        }

        dto.setCustomSpeciesName(customSpeciesName);
        dto.setCustomBreedName(customBreedName);
        dto.setDescription(description);
        dto.setPriceOffered(priceOffered);
        dto.setDisplayOrder(displayOrder != null ? displayOrder : 0);

        return dto;
    }

    /*
     * ============================================================
     * DROPDOWN PAYLOAD
     * ============================================================
     */

    @Override
    public DropdownPayload<Long>
    toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(breedName);

        return payload;
    }

    /*
     * ============================================================
     * CONSTRUCTOR WITH ID
     * ============================================================
     */

    public DogBreederBreed(Long id) {
        this.id = id;
    }
}
