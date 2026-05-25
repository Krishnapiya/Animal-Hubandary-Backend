package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.OfficeSearchBean;
import com.keltron.utility.jpa.entity.Office;

import jakarta.persistence.criteria.Predicate;

public final class OfficePredicates {

    private OfficePredicates() {}

    public static Specification<Office> createPredicate(OfficeSearchBean searchBean) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchBean.getId()));
            }
            if (ValidationUtils.isValid(searchBean.getOfficeType())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("officeType")),
                    "%" + searchBean.getOfficeType().toLowerCase() + "%"));
            }
            if (ValidationUtils.isValid(searchBean.getName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + searchBean.getName().toLowerCase() + "%"));
            }
            if (ValidationUtils.isValid(searchBean.getParentId())) {
                predicates.add(criteriaBuilder.equal(root.get("parent").get("id"), searchBean.getParentId()));
            }
            if (ValidationUtils.isValid(searchBean.getSearch())) {
                String term = "%" + searchBean.getSearch().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), term),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("officeType")), term)));
            }
            return ValidationUtils.isValid(predicates)
                ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                : null;
        };
    }
}
