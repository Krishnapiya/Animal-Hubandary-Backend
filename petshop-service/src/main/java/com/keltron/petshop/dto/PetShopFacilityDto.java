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
    private Boolean ventilationAvailable;
    private Boolean lightingAvailable;
    private String lightingArrangement;

    private Boolean fireSafetyAvailable;
    private String fireSafetyArrangement;

    private Boolean heatingCoolingAvailable;
    private String heatingCoolingArrangement;

    private Boolean powerBackupAvailable;
    private String powerBackupArrangement;

    private Boolean foodStorageAvailable;
    private String foodStorageArrangement;

    private Boolean cleanlinessWasteAvailable;
    private String cleanlinessWasteArrangement;

    private Boolean deadAnimalDisposalAvailable;
    private String deadAnimalDisposalArrangement;

    private Boolean veterinarySupportAvailable;
    private String veterinarySupportArrangement;

    @Override
    public PetShopFacility toEntity() {

        PetShopFacility entity = new PetShopFacility();

        entity.setId(id);
        entity.setPetShopDetailId(petShopDetailId);

        entity.setAccommodationInfrastructure(accommodationInfrastructure);
        entity.setWorkingHours(workingHours);
        entity.setRestDay(restDay);

        entity.setVentilationAvailable(ventilationAvailable);
        entity.setVentilationArrangement(ventilationArrangement);

        entity.setLightingAvailable(lightingAvailable);
        entity.setLightingArrangement(lightingArrangement);

        entity.setFireSafetyAvailable(fireSafetyAvailable);
        entity.setFireSafetyArrangement(fireSafetyArrangement);

        entity.setHeatingCoolingAvailable(heatingCoolingAvailable);
        entity.setHeatingCoolingArrangement(heatingCoolingArrangement);

        entity.setPowerBackupAvailable(powerBackupAvailable);
        entity.setPowerBackupArrangement(powerBackupArrangement);

        entity.setFoodStorageAvailable(foodStorageAvailable);
        entity.setFoodStorageArrangement(foodStorageArrangement);

        entity.setCleanlinessWasteAvailable(cleanlinessWasteAvailable);
        entity.setCleanlinessWasteArrangement(cleanlinessWasteArrangement);

        entity.setDeadAnimalDisposalAvailable(deadAnimalDisposalAvailable);
        entity.setDeadAnimalDisposalArrangement(deadAnimalDisposalArrangement);

        entity.setVeterinarySupportAvailable(veterinarySupportAvailable);
        entity.setVeterinarySupportArrangement(veterinarySupportArrangement);

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
        if (Boolean.TRUE.equals(ventilationAvailable)
                && !ValidationUtils.isValid(ventilationArrangement)) {

            addError(
                    "ventilationArrangement",
                    ventilationArrangement);
        }

        if (Boolean.TRUE.equals(lightingAvailable)
                && !ValidationUtils.isValid(lightingArrangement)) {

            addError(
                    "lightingArrangement",
                    lightingArrangement);
        }

        if (Boolean.TRUE.equals(fireSafetyAvailable)
                && !ValidationUtils.isValid(fireSafetyArrangement)) {

            addError(
                    "fireSafetyArrangement",
                    fireSafetyArrangement);
        }

        if (Boolean.TRUE.equals(heatingCoolingAvailable)
                && !ValidationUtils.isValid(heatingCoolingArrangement)) {

            addError(
                    "heatingCoolingArrangement",
                    heatingCoolingArrangement);
        }

        if (Boolean.TRUE.equals(powerBackupAvailable)
                && !ValidationUtils.isValid(powerBackupArrangement)) {

            addError(
                    "powerBackupArrangement",
                    powerBackupArrangement);
        }

        if (Boolean.TRUE.equals(foodStorageAvailable)
                && !ValidationUtils.isValid(foodStorageArrangement)) {

            addError(
                    "foodStorageArrangement",
                    foodStorageArrangement);
        }

        if (Boolean.TRUE.equals(cleanlinessWasteAvailable)
                && !ValidationUtils.isValid(cleanlinessWasteArrangement)) {

            addError(
                    "cleanlinessWasteArrangement",
                    cleanlinessWasteArrangement);
        }

        if (Boolean.TRUE.equals(deadAnimalDisposalAvailable)
                && !ValidationUtils.isValid(deadAnimalDisposalArrangement)) {

            addError(
                    "deadAnimalDisposalArrangement",
                    deadAnimalDisposalArrangement);
        }

        if (Boolean.TRUE.equals(veterinarySupportAvailable)
                && !ValidationUtils.isValid(veterinarySupportArrangement)) {

            addError(
                    "veterinarySupportArrangement",
                    veterinarySupportArrangement);
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}

