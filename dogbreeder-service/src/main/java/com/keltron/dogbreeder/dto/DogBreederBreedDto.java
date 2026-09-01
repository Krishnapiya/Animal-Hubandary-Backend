package com.keltron.dogbreeder.dto;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederBreedDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private DropdownPayload<Long> dogBreederDetail;

    private String breedName;

    private Integer dogCount;

    private String ageDescription;

    private String gender;

    /**
     * Convert DTO to Entity
     */
    @SuppressWarnings("unchecked")
    @Override
    public DogBreederBreed toEntity() {

        DogBreederBreed entity = new DogBreederBreed();

        /*
         * ID
         */
        if (id != null) {
            entity.setId(id);
        }

        /*
         * Breed name
         */
        if (breedName != null) {
            entity.setBreedName(
                    breedName.trim()
            );
        }

        /*
         * Dog count
         */
        if (dogCount != null) {
            entity.setDogCount(dogCount);
        } else {
            entity.setDogCount(0);
        }

        /*
         * Dog breeder detail
         */
        if (dogBreederDetail != null
                && dogBreederDetail.getId() != null) {

            entity.setDogBreederDetail(
                    new DogBreederDetail(
                            dogBreederDetail.getId()
                    )
            );
        }

        /*
         * Age description
         */
        if (ageDescription != null) {
            entity.setAgeDescription(
                    ageDescription.trim()
            );
        } else {
            entity.setAgeDescription(null);
        }

        /*
         * IMPORTANT:
         * Save gender to entity
         */
        if (gender != null) {
            entity.setGender(
                    gender.trim().toUpperCase()
            );
        } else {
            entity.setGender(null);
        }

        return entity;
    }

    /**
     * Validate DTO
     */
    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        /*
         * ============================================================
         * POST
         * ============================================================
         */
        if (httpMethod.equals(HttpMethod.POST)) {

            /*
             * Dog breeder detail ID
             */
            if (dogBreederDetail == null
                    || dogBreederDetail.getId() == null) {

                addError(
                        "dog_breeder_detail_id",
                        dogBreederDetail
                );
            }

            /*
             * Breed name
             */
            if (!ValidationUtils.isValid(breedName)) {

                addError(
                        "breed_name",
                        breedName
                );
            }

            /*
             * Gender
             */
            if (!ValidationUtils.isValid(gender)) {

                addError(
                        "gender",
                        gender
                );

            } else {

                String normalizedGender =
                        gender.trim().toUpperCase();

                if (!normalizedGender.equals("MALE")
                        && !normalizedGender.equals("FEMALE")) {

                    addError(
                            "gender",
                            gender
                    );
                }
            }

            /*
             * Dog count
             */
            if (dogCount == null) {

                addError(
                        "dog_count",
                        dogCount
                );

            } else if (dogCount < 0) {

                addError(
                        "dog_count",
                        dogCount
                );
            }
        }

        /*
         * ============================================================
         * PATCH
         * ============================================================
         */
        else if (httpMethod.equals(HttpMethod.PATCH)) {

            /*
             * ID
             */
            if (!ValidationUtils.isValid(id)) {

                addError(
                        "id",
                        id
                );
            }

            /*
             * Dog breeder detail ID
             */
            if (dogBreederDetail == null
                    || dogBreederDetail.getId() == null) {

                addError(
                        "dog_breeder_detail_id",
                        dogBreederDetail
                );
            }

            /*
             * Breed name
             */
            if (!ValidationUtils.isValid(breedName)) {

                addError(
                        "breed_name",
                        breedName
                );
            }

            /*
             * Gender
             */
            if (!ValidationUtils.isValid(gender)) {

                addError(
                        "gender",
                        gender
                );

            } else {

                String normalizedGender =
                        gender.trim().toUpperCase();

                if (!normalizedGender.equals("MALE")
                        && !normalizedGender.equals("FEMALE")) {

                    addError(
                            "gender",
                            gender
                    );
                }
            }

            /*
             * Dog count
             */
            if (dogCount == null) {

                addError(
                        "dog_count",
                        dogCount
                );

            } else if (dogCount < 0) {

                addError(
                        "dog_count",
                        dogCount
                );
            }
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}