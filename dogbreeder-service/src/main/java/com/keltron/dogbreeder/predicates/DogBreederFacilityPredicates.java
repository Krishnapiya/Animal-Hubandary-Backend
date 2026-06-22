package com.keltron.dogbreeder.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.DogBreederFacility;
import com.keltron.dogbreeder.searchbean.DogBreederFacilitySearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class DogBreederFacilityPredicates {

    public static Specification<DogBreederFacility> createPredicate(
            DogBreederFacilitySearchBean searchBean) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("id"),
                                searchBean.getId()
                        )
                );
            }

            if (ValidationUtils.isValid(searchBean.getDogBreederDetailId())) {

                predicates.add(
                        root.get("dogBreederDetailId")
                                .in(searchBean.getDogBreederDetailId())
                );
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(
                            predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}