package com.keltron.dogbreeder.dto;

import java.math.BigDecimal;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederBreedDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private DropdownPayload<Long> dogBreederDetail;

    private DropdownPayload<Long> species;

    private String breedName;

    private Integer dogCount;

    private String ageDescription;

    private String gender;

    private String customSpeciesName;

    private String customBreedName;

    private String description;

    private BigDecimal priceOffered;

    private Integer displayOrder = 0;

    public Long getSpeciesId() {
        return species != null ? species.getId() : null;
    }

    public void setSpeciesId(Long speciesId) {
        if (speciesId != null) {
            if (species == null) {
                species = new DropdownPayload<>();
            }
            species.setId(speciesId);
        }
    }

    /**
     * Convert DTO to Entity
     */
    @SuppressWarnings("unchecked")
    @Override
    public DogBreederBreed toEntity() {

        DogBreederBreed entity = new DogBreederBreed();

        if (id != null) {
            entity.setId(id);
        }

        if (breedName != null) {
            entity.setBreedName(
                    breedName.trim()
            );
        }

        if (dogCount != null) {
            entity.setDogCount(dogCount);
        } else {
            entity.setDogCount(0);
        }

        if (dogBreederDetail != null
                && dogBreederDetail.getId() != null) {

            entity.setDogBreederDetail(
                    new DogBreederDetail(
                            dogBreederDetail.getId()
                    )
            );
        }

        if (ageDescription != null) {
            entity.setAgeDescription(
                    ageDescription.trim()
            );
        } else {
            entity.setAgeDescription(null);
        }

        if (gender != null) {
            entity.setGender(
                    gender.trim().toUpperCase()
            );
        } else {
            entity.setGender(null);
        }

        if (species != null && species.getId() != null) {
            AnimalSpecies animalSpecies = new AnimalSpecies();
            animalSpecies.setId(species.getId());
            entity.setSpecies(animalSpecies);
        } else {
            entity.setSpecies(null);
        }

        if (ValidationUtils.isValid(customSpeciesName)) {
            entity.setCustomSpeciesName(customSpeciesName.trim());
        } else {
            entity.setCustomSpeciesName(null);
        }

        if (ValidationUtils.isValid(customBreedName)) {
            entity.setCustomBreedName(customBreedName.trim());
        } else {
            entity.setCustomBreedName(null);
        }

        if (ValidationUtils.isValid(description)) {
            entity.setDescription(description.trim());
        } else {
            entity.setDescription(null);
        }

        if (priceOffered != null) {
            entity.setPriceOffered(priceOffered);
        } else {
            entity.setPriceOffered(null);
        }

        if (displayOrder != null) {
            entity.setDisplayOrder(displayOrder);
        } else {
            entity.setDisplayOrder(0);
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

            /*
             * Custom species name length
             */
            if (customSpeciesName != null && customSpeciesName.trim().length() > 120) {
                addError(
                        "custom_species_name",
                        "Custom species name cannot exceed 120 characters"
                );
            }

            /*
             * Custom breed name length
             */
            if (customBreedName != null && customBreedName.trim().length() > 120) {
                addError(
                        "custom_breed_name",
                        "Custom breed name cannot exceed 120 characters"
                );
            }

            /*
             * Price offered validation
             */
            if (priceOffered != null && priceOffered.compareTo(BigDecimal.ZERO) < 0) {
                addError(
                        "price_offered",
                        "Price offered cannot be negative"
                );
            }

            /*
             * Display order validation
             */
            if (displayOrder != null && displayOrder < 0) {
                addError(
                        "display_order",
                        "Display order cannot be negative"
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

            /*
             * Custom species name length
             */
            if (customSpeciesName != null && customSpeciesName.trim().length() > 120) {
                addError(
                        "custom_species_name",
                        "Custom species name cannot exceed 120 characters"
                );
            }

            /*
             * Custom breed name length
             */
            if (customBreedName != null && customBreedName.trim().length() > 120) {
                addError(
                        "custom_breed_name",
                        "Custom breed name cannot exceed 120 characters"
                );
            }

            /*
             * Price offered validation
             */
            if (priceOffered != null && priceOffered.compareTo(BigDecimal.ZERO) < 0) {
                addError(
                        "price_offered",
                        "Price offered cannot be negative"
                );
            }

            /*
             * Display order validation
             */
            if (displayOrder != null && displayOrder < 0) {
                addError(
                        "display_order",
                        "Display order cannot be negative"
                );
            }
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}
