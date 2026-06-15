package com.keltron.petshop.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.petshop.entity.ApplicationDeclaration;
import com.keltron.petshop.searchbean.ApplicationDeclarationSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class ApplicationDeclarationPredicates {

    public static Specification<ApplicationDeclaration> createPredicate(
            ApplicationDeclarationSearchBean searchBean) {

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
                    searchBean.getApplicationId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("applicationId"),
                        searchBean.getApplicationId()
                    )
                );
            }

            if (ValidationUtils.isValid(
                    searchBean.getDeclarationPlace())) {

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("declarationPlace")),
                        "%" + searchBean
                                .getDeclarationPlace()
                                .toLowerCase() + "%"
                    )
                );
            }

            if (ValidationUtils.isValid(
                    searchBean.getAffidavitDeponentName())) {

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("affidavitDeponentName")),
                        "%" + searchBean
                                .getAffidavitDeponentName()
                                .toLowerCase() + "%"
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