package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.jpa.entity.AnimalSpecies;
import com.keltron.utility.beans.searchbean.*;

import jakarta.persistence.criteria.Predicate;

public class AnimalSpeciesPredicates {

    public static Specification<AnimalSpecies> createPredicate(
            AnimalSpeciesSearchBean searchBean) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (searchBean.getId() != null) {
                predicates.add(
                        cb.equal(root.get("id"),
                                searchBean.getId()));
            }

            if (searchBean.getSpeciesName() != null) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("speciesName")),
                                "%" + searchBean.getSpeciesName().toLowerCase() + "%"
                        ));
            }

            if (searchBean.getIsActive() != null) {
                predicates.add(
                        cb.equal(root.get("isActive"),
                                searchBean.getIsActive()));
            }

            return cb.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}