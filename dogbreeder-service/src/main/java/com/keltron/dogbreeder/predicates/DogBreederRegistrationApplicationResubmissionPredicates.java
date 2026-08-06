package com.keltron.dogbreeder.predicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplicationResubmission;
import com.keltron.dogbreeder.searchbean.DogBreederRegistrationApplicationResubmissionSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class DogBreederRegistrationApplicationResubmissionPredicates {

    public static Specification<DogBreederRegistrationApplicationResubmission> createPredicate(
            DogBreederRegistrationApplicationResubmissionSearchBean searchBean) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<Object, Object> applicationJoin =
                    root.join("application", JoinType.LEFT);

            Join<Object, Object> resubmittedByJoin =
                    root.join("resubmittedBy", JoinType.LEFT);

            // Filter by ID
            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("id"),
                                searchBean.getId()));
            }

            // Filter by Application ID(s)
            if (ValidationUtils.isValid(searchBean.getApplicationIds())
                    && !searchBean.getApplicationIds().isEmpty()) {

                predicates.add(
                        applicationJoin.get("id")
                                .in(searchBean.getApplicationIds()));

            } else if (ValidationUtils.isValid(searchBean.getApplicationId())) {

                predicates.add(
                        criteriaBuilder.equal(
                                applicationJoin.get("id"),
                                searchBean.getApplicationId()));
            }

            // Filter by Resubmitted By User ID(s)
            if (ValidationUtils.isValid(searchBean.getResubmittedByIds())
                    && !searchBean.getResubmittedByIds().isEmpty()) {

                predicates.add(
                        resubmittedByJoin.get("id")
                                .in(searchBean.getResubmittedByIds()));

            } else if (ValidationUtils.isValid(searchBean.getResubmittedBy())) {

                predicates.add(
                        criteriaBuilder.equal(
                                resubmittedByJoin.get("id"),
                                searchBean.getResubmittedBy()));
            }

            // Filter by Remarks
            if (ValidationUtils.isValid(searchBean.getRemarks())) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("remarks")),
                                "%" + searchBean.getRemarks().toLowerCase() + "%"));
            }

            // Filter by Resubmitted Date From
            if (searchBean.getResubmittedAtFrom() != null) {
                LocalDateTime fromDate =
                        searchBean.getResubmittedAtFrom().atStartOfDay();

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("resubmittedAt"),
                                fromDate));
            }

            // Filter by Resubmitted Date To
            if (searchBean.getResubmittedAtTo() != null) {
                LocalDateTime toDate =
                        searchBean.getResubmittedAtTo()
                                .plusDays(1)
                                .atStartOfDay();

                predicates.add(
                        criteriaBuilder.lessThan(
                                root.get("resubmittedAt"),
                                toDate));
            }

            // Global Search
            if (ValidationUtils.isValid(searchBean.getSearch())) {

                String keyword =
                        "%" + searchBean.getSearch().toLowerCase() + "%";

                Predicate byRemarks =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("remarks")),
                                keyword);

                Predicate byApplicationNumber =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        applicationJoin.get("applicationNumber")),
                                keyword);

                Predicate byResubmittedUsername =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        resubmittedByJoin.get("username")),
                                keyword);

                Predicate byResubmittedFname =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        resubmittedByJoin.get("fname")),
                                keyword);

                Predicate byResubmittedLname =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        resubmittedByJoin.get("lname")),
                                keyword);

                predicates.add(
                        criteriaBuilder.or(
                                byRemarks,
                                byApplicationNumber,
                                byResubmittedUsername,
                                byResubmittedFname,
                                byResubmittedLname));
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}