package com.keltron.petshop.searchbean;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopProposedAnimalSearchBean extends AbstractSearchBean {

	private static final long serialVersionUID = 1L;

	private Long id;

	private List<Long> applicationId;

	private String recordKind;

	private String species;

	private String breed;

	private Integer quantity;

	private Integer displayOrder;

	private String search;

	private String sortBy = "id"; // Default

	private String sortOrder = "asc"; // Default

	// Allow only actual fields from PetShopProposedAnimal entity
	private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
			"id",
			"recordKind",
			"species",
			"breed",
			"quantity",
			"priceOffered",
			"ageDescription",
			"displayOrder",
			"createdAt",
			"lastModifiedAt"
	);

	public PetShopProposedAnimalSearchBean() {
		dataSort = Sort.by(Sort.Order.asc(sortBy));
	}

	public Sort getSort() {
		return "desc".equalsIgnoreCase(sortOrder)
				? Sort.by(Sort.Order.desc(sortBy))
				: Sort.by(Sort.Order.asc(sortBy));
	}

	public void setSortBy(String sortBy) {
		if (ALLOWED_SORT_FIELDS.contains(sortBy)) {
			this.sortBy = sortBy;
		} else {
			this.sortBy = "id";
		}
		super.setDataSort(getSort());
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
		super.setDataSort(getSort());
	}
}