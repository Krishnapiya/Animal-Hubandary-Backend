package com.keltron.dogbreeder.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.ApplicationCorrection;
import com.keltron.dogbreeder.searchbean.ApplicationCorrectionSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class ApplicationCorrectionPredicates {

    public static Specification<ApplicationCorrection> createPredicate(
            ApplicationCorrectionSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {

                predicates.add(
                        cb.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            if (ValidationUtils.isValid(searchBean.getApplicationId())) {

                predicates.add(
                        cb.equal(
                                root.get("applicationId"),
                                searchBean.getApplicationId()));
            }

            if (ValidationUtils.isValid(searchBean.getSubmittedBy())) {

                predicates.add(
                        cb.equal(
                                root.get("submittedBy"),
                                searchBean.getSubmittedBy()));
            }

            if (ValidationUtils.isValid(searchBean.getCorrectionSummary())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("correctionSummary")),
                                "%" + searchBean.getCorrectionSummary().toLowerCase() + "%"));
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}