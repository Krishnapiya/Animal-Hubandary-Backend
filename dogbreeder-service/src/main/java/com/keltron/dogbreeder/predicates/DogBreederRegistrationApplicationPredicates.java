package com.keltron.dogbreeder.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.searchbean.DogBreederRegistrationApplicationSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class DogBreederRegistrationApplicationPredicates {

    public static Specification<DogBreederRegistrationApplication> createPredicate(
            DogBreederRegistrationApplicationSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(
                        cb.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            if (ValidationUtils.isValid(searchBean.getApplicationNumber())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("applicationNumber")),
                                "%"
                                        + searchBean.getApplicationNumber()
                                                .toLowerCase()
                                        + "%"));
            }

            if (ValidationUtils.isValid(searchBean.getEntityType())) {
                predicates.add(
                        cb.equal(
                                root.get("entityType"),
                                searchBean.getEntityType()));
            }

            if (ValidationUtils.isValid(searchBean.getStatusId())) {
                predicates.add(
                        cb.equal(
                                root.get("status").get("id"),
                                searchBean.getStatusId()));
            }

            if (ValidationUtils.isValid(searchBean.getDistrictId())) {
                predicates.add(
                        cb.equal(
                                root.get("district").get("id"),
                                searchBean.getDistrictId()));
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}