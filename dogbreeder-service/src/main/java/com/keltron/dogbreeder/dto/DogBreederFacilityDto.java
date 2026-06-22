package com.keltron.dogbreeder.dto;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.dogbreeder.entity.DogBreederFacility;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederFacilityDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long dogBreederDetailId;

    private String accommodationInfrastructure;
    private String workingHours;
    private String restDay;
    private String ventilationArrangement;
    private String lightingArrangement;
    private String heatingCoolingArrangement;
    private String foodStorageArrangement;
    private String cleanlinessWasteArrangement;
    private String deadAnimalDisposalArrangement;
    private String veterinarySupportArrangement;
    private String cageEnclosureDetails;

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederFacility toEntity() {

        DogBreederFacility entity = new DogBreederFacility();

        entity.setId(id);

        if (ValidationUtils.isValid(dogBreederDetailId)) {
            entity.setDogBreederDetail(new DogBreederDetail(dogBreederDetailId));
        }

        entity.setAccommodationInfrastructure(accommodationInfrastructure);
        entity.setWorkingHours(workingHours);
        entity.setRestDay(restDay);
        entity.setVentilationArrangement(ventilationArrangement);
        entity.setLightingArrangement(lightingArrangement);
        entity.setHeatingCoolingArrangement(heatingCoolingArrangement);
        entity.setFoodStorageArrangement(foodStorageArrangement);
        entity.setCleanlinessWasteArrangement(cleanlinessWasteArrangement);
        entity.setDeadAnimalDisposalArrangement(deadAnimalDisposalArrangement);
        entity.setVeterinarySupportArrangement(veterinarySupportArrangement);
        entity.setCageEnclosureDetails(cageEnclosureDetails);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.PATCH)) {
            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        if (!ValidationUtils.isValid(dogBreederDetailId)) {
            addError("dogBreederDetailId", dogBreederDetailId);
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}