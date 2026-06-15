package com.keltron.petshop.dto;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.PetShopFacility;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopFacilityDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long petShopDetailId;

    private String accommodationInfrastructure;
    private String workingHours;
    private String restDay;
    private String ventilationArrangement;
    private String lightingArrangement;
    private String fireSafetyArrangement;
    private String heatingCoolingArrangement;
    private String powerBackupArrangement;
    private String foodStorageArrangement;
    private String cleanlinessWasteArrangement;
    private String deadAnimalDisposalArrangement;
    private String veterinarySupportArrangement;

    @Override
    public PetShopFacility toEntity() {

        PetShopFacility entity =
                new PetShopFacility();

        entity.setId(id);
        entity.setPetShopDetailId(petShopDetailId);
        entity.setAccommodationInfrastructure(
                accommodationInfrastructure);
        entity.setWorkingHours(workingHours);
        entity.setRestDay(restDay);
        entity.setVentilationArrangement(
                ventilationArrangement);
        entity.setLightingArrangement(
                lightingArrangement);
        entity.setFireSafetyArrangement(
                fireSafetyArrangement);
        entity.setHeatingCoolingArrangement(
                heatingCoolingArrangement);
        entity.setPowerBackupArrangement(
                powerBackupArrangement);
        entity.setFoodStorageArrangement(
                foodStorageArrangement);
        entity.setCleanlinessWasteArrangement(
                cleanlinessWasteArrangement);
        entity.setDeadAnimalDisposalArrangement(
                deadAnimalDisposalArrangement);
        entity.setVeterinarySupportArrangement(
                veterinarySupportArrangement);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        if (!ValidationUtils.isValid(
                petShopDetailId)) {

            addError(
                    "petShopDetailId",
                    petShopDetailId);
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}

