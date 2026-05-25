package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.SampleDepartmentSearchBean;
import com.keltron.utility.jpa.entity.SampleDepartment;

import jakarta.persistence.criteria.Predicate;

public final class SampleDepartmentPredicates {

    private SampleDepartmentPredicates() {}

    public static Specification<SampleDepartment> createPredicate(SampleDepartmentSearchBean searchBean) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchBean.getId()));
            }
            if (ValidationUtils.isValid(searchBean.getName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + searchBean.getName().toLowerCase() + "%"));
            }
            if (ValidationUtils.isValid(searchBean.getSearch())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + searchBean.getSearch().toLowerCase() + "%"));
            }
            return ValidationUtils.isValid(predicates)
                ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                : null;
        };
    }
}

