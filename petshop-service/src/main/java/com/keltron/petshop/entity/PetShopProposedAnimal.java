package com.keltron.petshop.entity;

import java.math.BigDecimal;

import com.keltron.petshop.dto.PetShopProposedAnimalDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "pet_shop_proposed_animal", schema = "awb")
@Entity
@ToString
@NoArgsConstructor
public class PetShopProposedAnimal extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "application_id", referencedColumnName = "id", nullable = false)
	private RegistrationApplication application;

	@Column(name = "record_kind", nullable = false, length = 20)
	private String recordKind = "PROPOSED";

	@Column(name = "species", nullable = false, length = 120)
	private String species;

	@Column(name = "breed", length = 120)
	private String breed;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "description", columnDefinition = "text")
	private String description;

	@Column(name = "price_offered", precision = 12, scale = 2)
	private BigDecimal priceOffered;

	@Column(name = "age_description", length = 200)
	private String ageDescription;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;

	public PetShopProposedAnimal(Long id) {
		this.id = id;
	}

	@Override
	public <K extends AbstractDto> void copyFromDTO(K dto) {

		PetShopProposedAnimalDto animalDto = (PetShopProposedAnimalDto) dto;

		if (ValidationUtils.isValid(animalDto.getId())) {
			id = animalDto.getId();
		}

		if (animalDto.getApplication() != null && animalDto.getApplication().getId() != null) {
			application = new RegistrationApplication();
			application.setId(animalDto.getApplication().getId());
		} else {
			application = null;
		}

		if (ValidationUtils.isValid(animalDto.getRecordKind())) {
			recordKind = animalDto.getRecordKind();
		}

		if (ValidationUtils.isValid(animalDto.getSpecies())) {
			species = animalDto.getSpecies();
		}

		if (ValidationUtils.isValid(animalDto.getBreed())) {
			breed = animalDto.getBreed();
		}

		// Null check used because 0 is valid in DB
		if (animalDto.getQuantity() != null) {
			quantity = animalDto.getQuantity();
		}

		if (ValidationUtils.isValid(animalDto.getDescription())) {
			description = animalDto.getDescription();
		}

		// Null check used because 0.00 is valid in DB
		if (animalDto.getPriceOffered() != null) {
			priceOffered = animalDto.getPriceOffered();
		}

		if (ValidationUtils.isValid(animalDto.getAgeDescription())) {
			ageDescription = animalDto.getAgeDescription();
		}

		// Null check used because 0 is valid in DB
		if (animalDto.getDisplayOrder() != null) {
			displayOrder = animalDto.getDisplayOrder();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PetShopProposedAnimalDto toDTO() {

		PetShopProposedAnimalDto dto = new PetShopProposedAnimalDto();

		dto.setId(id);

		if (application != null) {
			DropdownPayload<Long> applicationPayload = new DropdownPayload<>();
			applicationPayload.setId(application.getId());
			applicationPayload.setName(String.valueOf(application.getId()));
			dto.setApplication(applicationPayload);
		}

		dto.setRecordKind(recordKind);
		dto.setSpecies(species);
		dto.setBreed(breed);
		dto.setQuantity(quantity);
		dto.setDescription(description);
		dto.setPriceOffered(priceOffered);
		dto.setAgeDescription(ageDescription);
		dto.setDisplayOrder(displayOrder);

		return dto;
	}

	@Override
	public DropdownPayload<Long> toDropDownPayload() {

		DropdownPayload<Long> payload = new DropdownPayload<>();
		payload.setId(id);
		payload.setName(species);

		return payload;
	}
}