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

    @SuppressWarnings("unchecked")
    @Override
    public DogBreederBreed toEntity() {

        DogBreederBreed entity = new DogBreederBreed();

        entity.setId(id);
        entity.setBreedName(breedName);

        if (dogCount != null) {
            entity.setDogCount(dogCount);
        } else {
            entity.setDogCount(0);
        }

        if (dogBreederDetail != null && dogBreederDetail.getId() != null) {
            entity.setDogBreederDetail(new DogBreederDetail(dogBreederDetail.getId()));
        }

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.POST)) {

            if (dogBreederDetail == null || dogBreederDetail.getId() == null) {
                addError("dog_breeder_detail_id", dogBreederDetail);
            }

            if (!ValidationUtils.isValid(breedName)) {
                addError("breed_name", breedName);
            }

            if (dogCount == null) {
                addError("dog_count", dogCount);
            } else if (dogCount < 0) {
                addError("dog_count", dogCount);
            }

        } else if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }

            if (dogBreederDetail == null || dogBreederDetail.getId() == null) {
                addError("dog_breeder_detail_id", dogBreederDetail);
            }

            if (!ValidationUtils.isValid(breedName)) {
                addError("breed_name", breedName);
            }

            if (dogCount == null) {
                addError("dog_count", dogCount);
            } else if (dogCount < 0) {
                addError("dog_count", dogCount);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}