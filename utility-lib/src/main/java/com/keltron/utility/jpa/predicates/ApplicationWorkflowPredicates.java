package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.ApplicationWorkflowSearchBean;
import com.keltron.utility.jpa.entity.ApplicationWorkflow;

import jakarta.persistence.criteria.Predicate;

public class ApplicationWorkflowPredicates {

    public static Specification<ApplicationWorkflow> createPredicate(
            ApplicationWorkflowSearchBean searchBean) {

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

            if (ValidationUtils.isValid(searchBean.getApplicationId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("applicationId"),
                        searchBean.getApplicationId()
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getApplicationId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("applicationId"),
                        searchBean.getApplicationId()
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getFromStatusId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("fromStatus").get("id"),
                        searchBean.getFromStatusId()
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getToStatusId())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("toStatus").get("id"),
                        searchBean.getToStatusId()
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getActionBy())) {

                predicates.add(
                    criteriaBuilder.equal(
                        root.get("actionBy"),
                        searchBean.getActionBy()
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getModuleName())) {

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("moduleName")),
                        "%" + searchBean.getModuleName().toLowerCase() + "%"
                    )
                );
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(
                            predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}