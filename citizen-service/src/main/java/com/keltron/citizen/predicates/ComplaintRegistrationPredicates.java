package com.keltron.citizen.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.citizen.searchbean.ComplaintRegistrationSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class ComplaintRegistrationPredicates {

    private ComplaintRegistrationPredicates() {
    }

    public static Specification<ComplaintRegistration> createPredicate(
            ComplaintRegistrationSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (ValidationUtils.isValid(searchBean.getId())) {

                predicates.add(
                        cb.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            if (ValidationUtils.isValid(
                    searchBean.getComplaintNumber())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("complaintNumber")),
                                "%"
                                        + searchBean
                                                .getComplaintNumber()
                                                .toLowerCase()
                                        + "%"));
            }

            if (ValidationUtils.isValid(
                    searchBean.getCitizenUserId())) {

                predicates.add(
                        cb.equal(
                                root.get("citizenUserId"),
                                searchBean.getCitizenUserId()));
            }

            if (ValidationUtils.isValid(
                    searchBean.getStatus())) {

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                searchBean.getStatus()));
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}