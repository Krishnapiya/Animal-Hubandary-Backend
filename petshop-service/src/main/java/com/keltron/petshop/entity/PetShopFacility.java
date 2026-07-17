package com.keltron.petshop.entity;

import com.keltron.petshop.dto.PetShopFacilityDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "pet_shop_facility", schema = "awb")
@NoArgsConstructor
@ToString
public class PetShopFacility extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_shop_detail_id", nullable = false, unique = true)
    private Long petShopDetailId;

    @Column(name = "accommodation_infrastructure")
    private String accommodationInfrastructure;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "rest_day")
    private String restDay;

    @Column(name = "ventilation_arrangement")
    private String ventilationArrangement;

    @Column(name = "lighting_arrangement")
    private String lightingArrangement;

    @Column(name = "fire_safety_arrangement")
    private String fireSafetyArrangement;

    @Column(name = "heating_cooling_arrangement")
    private String heatingCoolingArrangement;

    @Column(name = "power_backup_arrangement")
    private String powerBackupArrangement;

    @Column(name = "food_storage_arrangement")
    private String foodStorageArrangement;

    @Column(name = "cleanliness_waste_arrangement")
    private String cleanlinessWasteArrangement;

    @Column(name = "dead_animal_disposal_arrangement")
    private String deadAnimalDisposalArrangement;

    @Column(name = "veterinary_support_arrangement")
    private String veterinarySupportArrangement;
    
    @Column(name = "ventilation_available")
    private Boolean ventilationAvailable;

    @Column(name = "lighting_available")
    private Boolean lightingAvailable;

    @Column(name = "fire_safety_available")
    private Boolean fireSafetyAvailable;

    @Column(name = "heating_cooling_available")
    private Boolean heatingCoolingAvailable;

    @Column(name = "power_backup_available")
    private Boolean powerBackupAvailable;

    @Column(name = "food_storage_available")
    private Boolean foodStorageAvailable;

    @Column(name = "cleanliness_waste_available")
    private Boolean cleanlinessWasteAvailable;

    @Column(name = "dead_animal_disposal_available")
    private Boolean deadAnimalDisposalAvailable;

    @Column(name = "veterinary_support_available")
    private Boolean veterinarySupportAvailable;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        PetShopFacilityDto facilityDto = (PetShopFacilityDto) dto;

        if (ValidationUtils.isValid(facilityDto.getId()))
            this.id = facilityDto.getId();

        if (ValidationUtils.isValid(facilityDto.getPetShopDetailId()))
            this.petShopDetailId = facilityDto.getPetShopDetailId();

        this.accommodationInfrastructure =
                facilityDto.getAccommodationInfrastructure();

        this.workingHours =
                facilityDto.getWorkingHours();

        this.restDay =
                facilityDto.getRestDay();
        
        this.ventilationAvailable =
                facilityDto.getVentilationAvailable();

        this.lightingAvailable =
                facilityDto.getLightingAvailable();

        this.fireSafetyAvailable =
                facilityDto.getFireSafetyAvailable();

        this.heatingCoolingAvailable =
                facilityDto.getHeatingCoolingAvailable();

        this.powerBackupAvailable =
                facilityDto.getPowerBackupAvailable();

        this.foodStorageAvailable =
                facilityDto.getFoodStorageAvailable();

        this.cleanlinessWasteAvailable =
                facilityDto.getCleanlinessWasteAvailable();

        this.deadAnimalDisposalAvailable =
                facilityDto.getDeadAnimalDisposalAvailable();

        this.veterinarySupportAvailable =
                facilityDto.getVeterinarySupportAvailable();

        this.ventilationArrangement =
                facilityDto.getVentilationArrangement();

        this.lightingArrangement =
                facilityDto.getLightingArrangement();

        this.fireSafetyArrangement =
                facilityDto.getFireSafetyArrangement();

        this.heatingCoolingArrangement =
                facilityDto.getHeatingCoolingArrangement();

        this.powerBackupArrangement =
                facilityDto.getPowerBackupArrangement();

        this.foodStorageArrangement =
                facilityDto.getFoodStorageArrangement();

        this.cleanlinessWasteArrangement =
                facilityDto.getCleanlinessWasteArrangement();

        this.deadAnimalDisposalArrangement =
                facilityDto.getDeadAnimalDisposalArrangement();

        this.veterinarySupportArrangement =
                facilityDto.getVeterinarySupportArrangement();
    }

    @Override
    public PetShopFacilityDto toDTO() {

        PetShopFacilityDto dto = new PetShopFacilityDto();

        dto.setId(id);
        dto.setPetShopDetailId(petShopDetailId);

        dto.setAccommodationInfrastructure(accommodationInfrastructure);
        dto.setWorkingHours(workingHours);
        dto.setRestDay(restDay);

        dto.setVentilationAvailable(ventilationAvailable);
        dto.setVentilationArrangement(ventilationArrangement);

        dto.setLightingAvailable(lightingAvailable);
        dto.setLightingArrangement(lightingArrangement);

        dto.setFireSafetyAvailable(fireSafetyAvailable);
        dto.setFireSafetyArrangement(fireSafetyArrangement);

        dto.setHeatingCoolingAvailable(heatingCoolingAvailable);
        dto.setHeatingCoolingArrangement(heatingCoolingArrangement);

        dto.setPowerBackupAvailable(powerBackupAvailable);
        dto.setPowerBackupArrangement(powerBackupArrangement);

        dto.setFoodStorageAvailable(foodStorageAvailable);
        dto.setFoodStorageArrangement(foodStorageArrangement);

        dto.setCleanlinessWasteAvailable(cleanlinessWasteAvailable);
        dto.setCleanlinessWasteArrangement(cleanlinessWasteArrangement);

        dto.setDeadAnimalDisposalAvailable(deadAnimalDisposalAvailable);
        dto.setDeadAnimalDisposalArrangement(deadAnimalDisposalArrangement);

        dto.setVeterinarySupportAvailable(veterinarySupportAvailable);
        dto.setVeterinarySupportArrangement(veterinarySupportArrangement);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName("Facility - " + petShopDetailId);

        return payload;
    }

    public PetShopFacility(Long id) {
        this.id = id;
    }
}
