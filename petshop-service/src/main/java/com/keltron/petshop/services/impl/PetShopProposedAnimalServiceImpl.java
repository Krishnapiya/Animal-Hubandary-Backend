package com.keltron.petshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.petshop.dto.PetShopProposedAnimalDto;
import com.keltron.petshop.entity.PetShopProposedAnimal;
import com.keltron.petshop.repository.PetShopProposedAnimalRepository;
import com.keltron.utility.annotations.WriteTransactional;
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
		return super.save(dto);
	}

	@Override
	@WriteTransactional
	public PetShopProposedAnimal update(Long id, PetShopProposedAnimalDto dto) {
		return super.update(id, dto);
	}
}