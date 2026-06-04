package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.DistrictSearchBean;
import com.keltron.utility.jpa.entity.District;

import jakarta.persistence.criteria.Predicate;

public class DistrictPredicates {

    public static Specification<District> createPredicate(
            DistrictSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(
                        cb.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            if (ValidationUtils.isValid(searchBean.getCode())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("code")),
                                "%" + searchBean.getCode().toLowerCase() + "%"));
            }

            if (ValidationUtils.isValid(searchBean.getName())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + searchBean.getName().toLowerCase() + "%"));
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}