package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.PaymentStatusMasterSearchBean;
import com.keltron.utility.jpa.entity.PaymentStatusMaster;

import jakarta.persistence.criteria.Predicate;

public class PaymentStatusMasterPredicates {

    public static Specification<PaymentStatusMaster> createPredicate(
            PaymentStatusMasterSearchBean searchBean) {

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

            if (ValidationUtils.isValid(searchBean.getStatusCode())) {

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("statusCode")),
                        "%" + searchBean.getStatusCode().toLowerCase() + "%"
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getStatusName())) {

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("statusName")),
                        "%" + searchBean.getStatusName().toLowerCase() + "%"
                    )
                );
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}