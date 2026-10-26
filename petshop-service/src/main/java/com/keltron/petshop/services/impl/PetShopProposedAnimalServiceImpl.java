package com.keltron.petshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.petshop.dto.PetShopProposedAnimalDto;
import com.keltron.petshop.entity.PetShopProposedAnimal;
import com.keltron.petshop.repository.AnimalSpeciesRepository;
import com.keltron.petshop.repository.PetShopProposedAnimalRepository;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class PetShopProposedAnimalServiceImpl extends AbstractJpaService<
		PetShopProposedAnimalDto,
		Long,
		PetShopProposedAnimalRepository,
		PetShopProposedAnimal> {

	@Autowired
	private PetShopProposedAnimalRepository petShopProposedAnimalRepository;

	@Autowired
	private AnimalSpeciesRepository speciesRepository;

	private void normalizeCustomFields(PetShopProposedAnimalDto dto) {
		if (dto == null) return;

		// Species check
		boolean isOtherSpecies = false;
		if (dto.getSpecies() != null && dto.getSpecies().getId() != null) {
			AnimalSpecies species = speciesRepository.findById(dto.getSpecies().getId()).orElse(null);
			if (species != null) {
				isOtherSpecies = "Other".equalsIgnoreCase(species.getSpeciesName());
				if (dto.getSpecies().getName() == null || dto.getSpecies().getName().isBlank()) {
					dto.getSpecies().setName(species.getSpeciesName());
				}
			} else if (dto.getSpecies().getName() != null) {
				isOtherSpecies = "Other".equalsIgnoreCase(dto.getSpecies().getName());
			}
		} else if (dto.getSpecies() != null && dto.getSpecies().getName() != null) {
			isOtherSpecies = "Other".equalsIgnoreCase(dto.getSpecies().getName());
			if (isOtherSpecies) {
				speciesRepository.findBySpeciesNameIgnoreCase("Other")
						.ifPresent(s -> dto.getSpecies().setId(s.getId()));
			}
		}

		if (isOtherSpecies) {
			if (dto.getCustomSpeciesName() == null || dto.getCustomSpeciesName().trim().isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Custom species name is required when 'Other' species is selected");
			}
			if (dto.getCustomSpeciesName().trim().length() > 120) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Custom species name cannot exceed 120 characters");
			}
			dto.setCustomSpeciesName(dto.getCustomSpeciesName().trim());
		} else {
			// Clear stale custom species if regular species
			dto.setCustomSpeciesName(null);
		}

		// Breed check
		boolean isOtherBreed = dto.getBreed() != null && "Other".equalsIgnoreCase(dto.getBreed().trim());
		if (isOtherBreed) {
			if (dto.getCustomBreedName() == null || dto.getCustomBreedName().trim().isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Custom breed name is required when 'Other' breed is selected");
			}
			if (dto.getCustomBreedName().trim().length() > 120) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Custom breed name cannot exceed 120 characters");
			}
			dto.setCustomBreedName(dto.getCustomBreedName().trim());
			dto.setBreed("Other");
		} else {
			// Clear stale custom breed if regular breed
			dto.setCustomBreedName(null);
		}
	}

	@Transactional(readOnly = true)
	public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {

		List<PetShopProposedAnimal> animals = petShopProposedAnimalRepository.findAll();

		List<PetShopProposedAnimalDto> dtos = animals.stream()
				.map(PetShopProposedAnimal::toDTO)
				.toList();

		return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
	}

	@Override
	@WriteTransactional
	public PetShopProposedAnimal save(PetShopProposedAnimalDto dto) {
		normalizeCustomFields(dto);
		return super.save(dto);
	}

	@Override
	@WriteTransactional
	public PetShopProposedAnimal update(Long id, PetShopProposedAnimalDto dto) {
		normalizeCustomFields(dto);
		return super.update(id, dto);
	}
	
	@Transactional(readOnly = true)
	public List<PetShopProposedAnimalDto> getDraft(
	        Long applicationId) {

	    return petShopProposedAnimalRepository
	            .findByApplication_IdOrderByDisplayOrderAsc(
	                    applicationId)
	            .stream()
	            .map(PetShopProposedAnimal::toDTO)
	            .toList();
	}
}