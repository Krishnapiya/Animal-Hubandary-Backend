package com.keltron.dogbreeder.entity;

import com.keltron.dogbreeder.dto.DogBreederFacilityDto;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "dog_breeder_facility", schema = "awb")
@NoArgsConstructor
@ToString(exclude = "dogBreederDetail")
public class DogBreederFacility extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: awb.dog_breeder_facility.dog_breeder_detail_id
    // References: awb.dog_breeder_detail.id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dog_breeder_detail_id", nullable = false, unique = true)
    private DogBreederDetail dogBreederDetail;

    @Column(name = "accommodation_infrastructure")
    private String accommodationInfrastructure;

    @Column(name = "working_hours", length = 500)
    private String workingHours;

    @Column(name = "holiday", length = 120)
    private String holiday;
    
    @Column(name = "ventilation_available")
    private Boolean ventilationAvailable;

    @Column(name = "lighting_available")
    private Boolean lightingAvailable;

    @Column(name = "heating_cooling_available")
    private Boolean heatingCoolingAvailable;

    @Column(name = "food_storage_available")
    private Boolean foodStorageAvailable;

    @Column(name = "cleanliness_waste_available")
    private Boolean cleanlinessWasteAvailable;

    @Column(name = "dead_animal_disposal_available")
    private Boolean deadAnimalDisposalAvailable;

    @Column(name = "veterinary_support_available")
    private Boolean veterinarySupportAvailable;
    
    @Column(name = "ventilation_arrangement")
    private String ventilationArrangement;

    @Column(name = "lighting_arrangement")
    private String lightingArrangement;
    @Column(name = "heating_cooling_arrangement")
    private String heatingCoolingArrangement;

    @Column(name = "food_storage_arrangement")
    private String foodStorageArrangement;

    @Column(name = "cleanliness_waste_arrangement")
    private String cleanlinessWasteArrangement;

    @Column(name = "dead_animal_disposal_arrangement")
    private String deadAnimalDisposalArrangement;

    @Column(name = "veterinary_support_arrangement")
    private String veterinarySupportArrangement;

    @Column(name = "cage_enclosure_details")
    private String cageEnclosureDetails;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DogBreederFacilityDto facilityDto = (DogBreederFacilityDto) dto;

        if (ValidationUtils.isValid(facilityDto.getId())) {
            this.id = facilityDto.getId();
        }

        if (ValidationUtils.isValid(facilityDto.getDogBreederDetailId())) {
            this.dogBreederDetail =
                    new DogBreederDetail(facilityDto.getDogBreederDetailId());
        }

        this.accommodationInfrastructure =
                facilityDto.getAccommodationInfrastructure();

        this.workingHours = facilityDto.getWorkingHours();
        this.holiday = facilityDto.getHoliday();        
        
        this.ventilationAvailable = facilityDto.getVentilationAvailable();
        this.lightingAvailable = facilityDto.getLightingAvailable();
        this.heatingCoolingAvailable = facilityDto.getHeatingCoolingAvailable();
        this.foodStorageAvailable = facilityDto.getFoodStorageAvailable();
        this.cleanlinessWasteAvailable = facilityDto.getCleanlinessWasteAvailable();
        this.deadAnimalDisposalAvailable = facilityDto.getDeadAnimalDisposalAvailable();
        this.veterinarySupportAvailable = facilityDto.getVeterinarySupportAvailable();
        
        this.ventilationArrangement = facilityDto.getVentilationArrangement();
        this.lightingArrangement = facilityDto.getLightingArrangement();
        this.heatingCoolingArrangement =
                facilityDto.getHeatingCoolingArrangement();
        this.foodStorageArrangement = facilityDto.getFoodStorageArrangement();
        this.cleanlinessWasteArrangement =
                facilityDto.getCleanlinessWasteArrangement();
        this.deadAnimalDisposalArrangement =
                facilityDto.getDeadAnimalDisposalArrangement();
        this.veterinarySupportArrangement =
                facilityDto.getVeterinarySupportArrangement();
        this.cageEnclosureDetails = facilityDto.getCageEnclosureDetails();
    }

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederFacilityDto toDTO() {

        DogBreederFacilityDto dto = new DogBreederFacilityDto();

        dto.setId(id);

        if (dogBreederDetail != null) {
            dto.setDogBreederDetailId(dogBreederDetail.getId());
        }

        dto.setAccommodationInfrastructure(accommodationInfrastructure);
        dto.setWorkingHours(workingHours);
        dto.setHoliday(holiday);        
        dto.setVentilationAvailable(ventilationAvailable);
        dto.setLightingAvailable(lightingAvailable);
        dto.setHeatingCoolingAvailable(heatingCoolingAvailable);
        dto.setFoodStorageAvailable(foodStorageAvailable);
        dto.setCleanlinessWasteAvailable(cleanlinessWasteAvailable);
        dto.setDeadAnimalDisposalAvailable(deadAnimalDisposalAvailable);
        dto.setVeterinarySupportAvailable(veterinarySupportAvailable);
        
        dto.setVentilationArrangement(ventilationArrangement);
        dto.setLightingArrangement(lightingArrangement);
        dto.setHeatingCoolingArrangement(heatingCoolingArrangement);
        dto.setFoodStorageArrangement(foodStorageArrangement);
        dto.setCleanlinessWasteArrangement(cleanlinessWasteArrangement);
        dto.setDeadAnimalDisposalArrangement(deadAnimalDisposalArrangement);
        dto.setVeterinarySupportArrangement(veterinarySupportArrangement);
        dto.setCageEnclosureDetails(cageEnclosureDetails);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);

        payload.setName(
                "Dog Breeder Facility - "
                        + (dogBreederDetail != null
                                ? dogBreederDetail.getId()
                                : "")
        );

        return payload;
    }

    public DogBreederFacility(Long id) {
        this.id = id;
    }
}