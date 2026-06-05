package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.RoleMasterSearchBean;
import com.keltron.utility.jpa.entity.RoleMaster;

import jakarta.persistence.criteria.Predicate;

public class RoleMasterPredicates {

    public static Specification<RoleMaster> createPredicate(
            RoleMasterSearchBean searchBean) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            if (ValidationUtils.isValid(searchBean.getRoleName())) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("roleName")),
                                "%" + searchBean.getRoleName().toLowerCase() + "%"));
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}