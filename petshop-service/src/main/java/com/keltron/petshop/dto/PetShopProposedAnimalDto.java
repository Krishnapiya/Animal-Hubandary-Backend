package com.keltron.petshop.dto;

import java.math.BigDecimal;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.PetShopProposedAnimal;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.jpa.entity.RegistrationApplication;


import lombok.Getter;
import lombok.Setter;
import com.keltron.utility.responses.payload.DropdownPayload;
@Getter
@Setter
public class PetShopProposedAnimalDto extends AbstractDto {

	private static final long serialVersionUID = 1L;

	private Long id;

	private DropdownPayload<Long> application;

	private String recordKind;

	private DropdownPayload<Long> species;

	private String breed;

	private String customSpeciesName;

	private String customBreedName;

	private Integer quantity;

	private String description;

	private BigDecimal priceOffered;

	private String ageDescription;

	private Integer displayOrder;
	
	private String gender;

	@SuppressWarnings("unchecked")
	@Override
	public PetShopProposedAnimal toEntity() {

		PetShopProposedAnimal entity = new PetShopProposedAnimal();

		entity.setId(id);

		if (application != null && application.getId() != null) {
			RegistrationApplication app = new RegistrationApplication();
			app.setId(application.getId());
			entity.setApplication(app);
		}

		entity.setRecordKind(
				ValidationUtils.isValid(recordKind) ? recordKind : "PROPOSED"
		);

		if (species != null && species.getId() != null) {

		    AnimalSpecies animalSpecies = new AnimalSpecies();
		    animalSpecies.setId(species.getId());

		    entity.setSpecies(animalSpecies);
		}
		entity.setBreed(breed);
		entity.setCustomSpeciesName(
				ValidationUtils.isValid(customSpeciesName) ? customSpeciesName.trim() : null
		);
		entity.setCustomBreedName(
				ValidationUtils.isValid(customBreedName) ? customBreedName.trim() : null
		);
		entity.setGender(gender);
		entity.setQuantity(quantity);
		entity.setDescription(description);
		entity.setPriceOffered(priceOffered);
		entity.setAgeDescription(ageDescription);

		entity.setDisplayOrder(displayOrder != null ? displayOrder : 0);

		return entity;
	}

	@Override
	public boolean isValid(HttpMethod httpMethod) {

		if (httpMethod == null) {
			return false;
		}

		if (httpMethod.equals(HttpMethod.POST)) {

			if (application == null || application.getId() == null) {
				addError("application_id", application);
			}

			if (species == null || species.getId() == null) {
			    addError("species", species);
			}
			if (ValidationUtils.isValid(gender)) {

			    if (!gender.equals("Male")
			            && !gender.equals("Female")
			            && !gender.equals("Both")) {

			        addError("gender", gender);
			    }
			}
			if (ValidationUtils.isValid(recordKind)) {
				if (!recordKind.equals("PROPOSED") && !recordKind.equals("RENEWAL_STOCK")) {
					addError("record_kind", recordKind);
				}
			}

			if (quantity != null && quantity < 0) {
				addError("quantity", quantity);
			}

			if (priceOffered != null && priceOffered.compareTo(BigDecimal.ZERO) < 0) {
				addError("price_offered", priceOffered);
			}

			if (displayOrder != null && displayOrder < 0) {
				addError("display_order", displayOrder);
			}

			if (customSpeciesName != null && customSpeciesName.trim().length() > 120) {
				addError("customSpeciesName", "Custom species name cannot exceed 120 characters");
			}

			if (customBreedName != null && customBreedName.trim().length() > 120) {
				addError("customBreedName", "Custom breed name cannot exceed 120 characters");
			}

			if (species != null && species.getName() != null && species.getName().equalsIgnoreCase("Other")) {
				if (!ValidationUtils.isValid(customSpeciesName) || customSpeciesName.trim().isEmpty()) {
					addError("customSpeciesName", "Custom species name is required when 'Other' species is selected");
				}
			}

			if (breed != null && breed.equalsIgnoreCase("Other")) {
				if (!ValidationUtils.isValid(customBreedName) || customBreedName.trim().isEmpty()) {
					addError("customBreedName", "Custom breed name is required when 'Other' breed is selected");
				}
			}

		} else if (httpMethod.equals(HttpMethod.PATCH)) {

			if (!ValidationUtils.isValid(id)) {
				addError("id", id);
			}

			if (ValidationUtils.isValid(recordKind)) {
				if (!recordKind.equals("PROPOSED") && !recordKind.equals("RENEWAL_STOCK")) {
					addError("record_kind", recordKind);
				}
			}

			if (quantity != null && quantity < 0) {
				addError("quantity", quantity);
			}

			if (priceOffered != null && priceOffered.compareTo(BigDecimal.ZERO) < 0) {
				addError("price_offered", priceOffered);
			}

			if (displayOrder != null && displayOrder < 0) {
				addError("display_order", displayOrder);
			}

			if (customSpeciesName != null && customSpeciesName.trim().length() > 120) {
				addError("customSpeciesName", "Custom species name cannot exceed 120 characters");
			}

			if (customBreedName != null && customBreedName.trim().length() > 120) {
				addError("customBreedName", "Custom breed name cannot exceed 120 characters");
			}

			if (species != null && species.getName() != null && species.getName().equalsIgnoreCase("Other")) {
				if (!ValidationUtils.isValid(customSpeciesName) || customSpeciesName.trim().isEmpty()) {
					addError("customSpeciesName", "Custom species name is required when 'Other' species is selected");
				}
			}

			if (breed != null && breed.equalsIgnoreCase("Other")) {
				if (!ValidationUtils.isValid(customBreedName) || customBreedName.trim().isEmpty()) {
					addError("customBreedName", "Custom breed name is required when 'Other' breed is selected");
				}
			}
		}

		return getErrors() == null || getErrors().isEmpty();
	}
}