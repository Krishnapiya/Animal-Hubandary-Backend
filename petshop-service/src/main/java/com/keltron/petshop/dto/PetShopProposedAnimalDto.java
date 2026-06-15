package com.keltron.petshop.dto;

import java.math.BigDecimal;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.PetShopProposedAnimal;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.responses.payload.DropdownPayload;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopProposedAnimalDto extends AbstractDto {

	private static final long serialVersionUID = 1L;

	private Long id;

	private DropdownPayload<Long> application;

	private String recordKind;

	private String species;

	private String breed;

	private Integer quantity;

	private String description;

	private BigDecimal priceOffered;

	private String ageDescription;

	private Integer displayOrder;

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

		entity.setSpecies(species);
		entity.setBreed(breed);
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

			if (!ValidationUtils.isValid(species)) {
				addError("species", species);
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
		}

		return getErrors() == null || getErrors().isEmpty();
	}
}