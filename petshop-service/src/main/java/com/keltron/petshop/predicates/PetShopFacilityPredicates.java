package com.keltron.petshop.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.petshop.entity.PetShopFacility;
import com.keltron.petshop.searchbean.PetShopFacilitySearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class PetShopFacilityPredicates {

    public static Specification<PetShopFacility> createPredicate(
            PetShopFacilitySearchBean searchBean) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (ValidationUtils.isValid(
                    searchBean.getId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("id"),
                        searchBean.getId()
                    )
                );
            }

            if (ValidationUtils.isValid(
                    searchBean.getPetShopDetailId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("petShopDetailId"),
                        searchBean.getPetShopDetailId()
                    )
                );
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(
                            predicates.toArray(
                                    new Predicate[0]))
                    : null;
        };
    }
}