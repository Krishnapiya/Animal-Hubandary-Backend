package com.keltron.dogbreeder.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.DogBreederRegistrationInspection;
import com.keltron.dogbreeder.searchbean.DogBreederRegistrationInspectionSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class DogBreederRegistrationInspectionPredicates {

    public static Specification<DogBreederRegistrationInspection> createPredicate(
            DogBreederRegistrationInspectionSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {

                predicates.add(
                        cb.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            if (ValidationUtils.isValid(
                    searchBean.getApplicationId())) {

                predicates.add(
                        cb.equal(
                                root.get("application")
                                        .get("id"),
                                searchBean.getApplicationId()));
            }

            if (ValidationUtils.isValid(
                    searchBean.getStatus())) {

                predicates.add(
                        cb.equal(
                                cb.lower(root.get("status")),
                                searchBean.getStatus().toLowerCase()));
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(
                            predicates.toArray(
                                    new Predicate[0]))
                    : null;
        };
    }
}