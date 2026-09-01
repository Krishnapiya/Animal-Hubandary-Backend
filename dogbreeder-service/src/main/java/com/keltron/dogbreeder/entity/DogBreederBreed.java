package com.keltron.dogbreeder.entity;

import java.sql.Timestamp;

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
            length = 200
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
     * PRE PERSIST
     * ============================================================
     */

    @PrePersist
    public void prePersist() {

        Timestamp now =
                new Timestamp(
                        System.currentTimeMillis()
                );

        /*
         * Created at
         */
        if (getCreatedAt() == null) {
            setCreatedAt(now);
        }

        /*
         * Last modified at
         */
        if (getLastModifiedAt() == null) {
            setLastModifiedAt(now);
        }

        /*
         * Created by
         */
        if (getCreatedBy() == null
                || getCreatedBy().isBlank()) {

            setCreatedBy("SYSTEM");
        }

        /*
         * Last modified by
         */
        if (getLastModifiedBy() == null
                || getLastModifiedBy().isBlank()) {

            setLastModifiedBy(
                    getCreatedBy()
            );
        }

        /*
         * Normalize gender
         */
        if (gender != null) {
            gender =
                    gender.trim().toUpperCase();
        }

        /*
         * Normalize breed name
         */
        if (breedName != null) {
            breedName =
                    breedName.trim();
        }

        /*
         * Default dog count
         */
        if (dogCount == null) {
            dogCount = 0;
        }
    }

    /*
     * ============================================================
     * PRE UPDATE
     * ============================================================
     */

    @PreUpdate
    public void preUpdate() {

        /*
         * Last modified time
         */
        setLastModifiedAt(
                new Timestamp(
                        System.currentTimeMillis()
                )
        );

        /*
         * Last modified by
         */
        if (getLastModifiedBy() == null
                || getLastModifiedBy().isBlank()) {

            setLastModifiedBy("SYSTEM");
        }

        /*
         * Created at
         */
        if (getCreatedAt() == null) {

            setCreatedAt(
                    new Timestamp(
                            System.currentTimeMillis()
                    )
            );
        }

        /*
         * Created by
         */
        if (getCreatedBy() == null
                || getCreatedBy().isBlank()) {

            setCreatedBy("SYSTEM");
        }

        /*
         * Normalize gender
         */
        if (gender != null) {
            gender =
                    gender.trim().toUpperCase();
        }

        /*
         * Normalize breed name
         */
        if (breedName != null) {
            breedName =
                    breedName.trim();
        }

        /*
         * Default dog count
         */
        if (dogCount == null) {
            dogCount = 0;
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

        /*
         * ID
         */
        if (ValidationUtils.isValid(
                breedDto.getId())) {

            id = breedDto.getId();
        }

        /*
         * Dog breeder detail
         */
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

        /*
         * Breed name
         */
        if (ValidationUtils.isValid(
                breedDto.getBreedName())) {

            breedName =
                    breedDto
                            .getBreedName()
                            .trim();
        }

        /*
         * Dog count
         */
        if (breedDto.getDogCount() != null) {

            dogCount =
                    breedDto.getDogCount();

        } else {

            dogCount = 0;
        }

        /*
         * Age description
         */
        if (breedDto.getAgeDescription() != null) {

            ageDescription =
                    breedDto
                            .getAgeDescription()
                            .trim();

        } else {

            ageDescription = null;
        }

        /*
         * ========================================================
         * IMPORTANT: GENDER
         * ========================================================
         */

        if (breedDto.getGender() != null) {

            gender =
                    breedDto
                            .getGender()
                            .trim()
                            .toUpperCase();

        } else {

            gender = null;
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

        /*
         * ID
         */
        dto.setId(id);

        /*
         * Breed name
         */
        dto.setBreedName(
                breedName
        );

        /*
         * Dog count
         */
        dto.setDogCount(
                dogCount
        );

        /*
         * Age
         */
        dto.setAgeDescription(
                ageDescription
        );

        /*
         * ========================================================
         * IMPORTANT: GENDER
         * ========================================================
         */

        dto.setGender(
                gender
        );

        /*
         * Dog breeder detail
         */
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

        payload.setName(
                breedName
        );

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