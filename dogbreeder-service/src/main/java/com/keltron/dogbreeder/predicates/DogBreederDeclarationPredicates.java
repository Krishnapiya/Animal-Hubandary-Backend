package com.keltron.dogbreeder.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.DogBreederDeclaration;
import com.keltron.dogbreeder.searchbean.DogBreederDeclarationSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class DogBreederDeclarationPredicates {

    public static Specification<DogBreederDeclaration> createPredicate(
            DogBreederDeclarationSearchBean searchBean) {

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

            if (ValidationUtils.isValid(
                    searchBean.getDogBreederDetailId())) {

                predicates.add(
                        root.get("dogBreederDetail")
                                .get("id")
                                .in(searchBean.getDogBreederDetailId())
                );
            }

            if (ValidationUtils.isValid(
                    searchBean.getQualificationExperience())) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("qualificationExperience")),
                                "%" + searchBean
                                        .getQualificationExperience()
                                        .toLowerCase() + "%"
                        )
                );
            }

            if (searchBean.getDeclarationAccepted() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("declarationAccepted"),
                                searchBean.getDeclarationAccepted()
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

            if (searchBean.getDeclarationDate() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("declarationDate"),
                                searchBean.getDeclarationDate()
                        )
                );
            }

            if (ValidationUtils.isValid(
                    searchBean.getApplicantName())) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("applicantName")),
                                "%" + searchBean
                                        .getApplicantName()
                                        .toLowerCase() + "%"
                        )
                );
            }

            if (ValidationUtils.isValid(
                    searchBean.getSignatureName())) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("signatureName")),
                                "%" + searchBean
                                        .getSignatureName()
                                        .toLowerCase() + "%"
                        )
                );
            }

            if (searchBean.getSignedAt() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("signedAt"),
                                searchBean.getSignedAt()
                        )
                );
            }

            if (ValidationUtils.isValid(searchBean.getSearch())) {

                String searchValue =
                        "%" + searchBean.getSearch().toLowerCase() + "%";

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("qualificationExperience")),
                                        searchValue
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("declarationPlace")),
                                        searchValue
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("applicantName")),
                                        searchValue
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("signatureName")),
                                        searchValue
                                )
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