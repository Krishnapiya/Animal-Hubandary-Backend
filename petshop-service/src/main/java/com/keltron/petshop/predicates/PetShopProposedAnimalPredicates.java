package com.keltron.petshop.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.petshop.entity.PetShopProposedAnimal;
import com.keltron.petshop.searchbean.PetShopProposedAnimalSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class PetShopProposedAnimalPredicates {

	public static Specification<PetShopProposedAnimal> createPredicate(PetShopProposedAnimalSearchBean searchBean) {

		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (ValidationUtils.isValid(searchBean.getId())) {
				predicates.add(criteriaBuilder.equal(root.get("id"), searchBean.getId()));
			}

			if (ValidationUtils.isValid(searchBean.getApplicationId())) {
				predicates.add(root.get("application").get("id").in(searchBean.getApplicationId()));
			}

			if (ValidationUtils.isValid(searchBean.getRecordKind())) {
				predicates.add(criteriaBuilder.equal(root.get("recordKind"), searchBean.getRecordKind()));
			}

			if (ValidationUtils.isValid(searchBean.getSpecies())) {
			    predicates.add(criteriaBuilder.like(
			            criteriaBuilder.lower(
			                    root.get("species").get("speciesName")),
			            "%" + searchBean.getSpecies().toLowerCase() + "%"
			    ));
			}

			if (ValidationUtils.isValid(searchBean.getBreed())) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("breed")),
						"%" + searchBean.getBreed().toLowerCase() + "%"
				));
			}

			if (ValidationUtils.isValid(searchBean.getQuantity())) {
				predicates.add(criteriaBuilder.equal(root.get("quantity"), searchBean.getQuantity()));
			}

			if (ValidationUtils.isValid(searchBean.getDisplayOrder())) {
				predicates.add(criteriaBuilder.equal(root.get("displayOrder"), searchBean.getDisplayOrder()));
			}

			if (ValidationUtils.isValid(searchBean.getSearch())) {

				String keyword = "%" + searchBean.getSearch().toLowerCase() + "%";

				Predicate bySpecies = criteriaBuilder.like(
				        criteriaBuilder.lower(
				                root.get("species").get("speciesName")),
				        keyword
				);

				Predicate byBreed = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("breed")),
						keyword
				);

				Predicate byRecordKind = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("recordKind")),
						keyword
				);

				Predicate byDescription = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("description")),
						keyword
				);

				Predicate byAgeDescription = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("ageDescription")),
						keyword
				);

				predicates.add(criteriaBuilder.or(
						bySpecies,
						byBreed,
						byRecordKind,
						byDescription,
						byAgeDescription
				));
			}

			return ValidationUtils.isValid(predicates)
					? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
					: null;
		};
	}
}