package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.FeeScheduleSearchBean;
import com.keltron.utility.jpa.entity.FeeSchedule;

import jakarta.persistence.criteria.Predicate;

public class FeeSchedulePredicates {

    public static Specification<FeeSchedule> createPredicate(
            FeeScheduleSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            
            if (ValidationUtils.isValid(searchBean.getId())) {

                predicates.add(
                    cb.equal(
                        root.get("id"),
                        searchBean.getId()
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getEntityType())) {

                predicates.add(
                    cb.like(
                        cb.lower(root.get("entityType")),
                        "%" + searchBean.getEntityType().toLowerCase() + "%"
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getFeeKind())) {

                predicates.add(
                    cb.like(
                        cb.lower(root.get("feeKind")),
                        "%" + searchBean.getFeeKind().toLowerCase() + "%"
                    )
                );
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}