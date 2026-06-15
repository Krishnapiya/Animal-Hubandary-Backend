package com.keltron.dogbreeder.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.dogbreeder.entity.DogBreederBreed;
import com.keltron.dogbreeder.searchbean.DogBreederBreedSearchBean;
import com.keltron.utility.ValidationUtils;

import jakarta.persistence.criteria.Predicate;

public class DogBreederBreedPredicates {

    public static Specification<DogBreederBreed> createPredicate(DogBreederBreedSearchBean searchBean) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (searchBean == null) {
                return null;
            }

            if (ValidationUtils.isValid(searchBean.getId())) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchBean.getId()));
            }

            if (ValidationUtils.isValid(searchBean.getDogBreederDetailId())) {
                predicates.add(
                    root.get("dogBreederDetail").get("id").in(searchBean.getDogBreederDetailId())
                );
            }

            if (ValidationUtils.isValid(searchBean.getBreedName())) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("breedName")),
                        "%" + searchBean.getBreedName().toLowerCase() + "%"
                    )
                );
            }

            if (ValidationUtils.isValid(searchBean.getDogCount())) {
                predicates.add(
                    criteriaBuilder.equal(root.get("dogCount"), searchBean.getDogCount())
                );
            }

            if (ValidationUtils.isValid(searchBean.getSearch())) {

                String keyword = "%" + searchBean.getSearch().toLowerCase() + "%";

                Predicate byBreedName = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("breedName")),
                    keyword
                );

                Predicate byDogBreederDetailId = criteriaBuilder.like(
                    root.get("dogBreederDetail").get("id").as(String.class),
                    keyword
                );

                Predicate byDogCount = criteriaBuilder.like(
                    root.get("dogCount").as(String.class),
                    keyword
                );

                predicates.add(
                    criteriaBuilder.or(
                        byBreedName,
                        byDogBreederDetailId,
                        byDogCount
                    )
                );
            }

            return ValidationUtils.isValid(predicates)
                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}