package com.keltron.petshop.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.petshop.searchbean.PetShopDetailSearchBean;
import com.keltron.petshop.entity.PetShopDetail;

import jakarta.persistence.criteria.Predicate;

public class PetShopDetailPredicates {

    public static Specification<PetShopDetail> createPredicate(
            PetShopDetailSearchBean searchBean) {

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

            if (ValidationUtils.isValid(searchBean.getShopName())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("shopName")),
                                "%" + searchBean.getShopName().toLowerCase() + "%"));
            }

            if (ValidationUtils.isValid(searchBean.getOwnerName())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("ownerName")),
                                "%" + searchBean.getOwnerName().toLowerCase() + "%"));
            }

            if (ValidationUtils.isValid(searchBean.getCity())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("city")),
                                "%" + searchBean.getCity().toLowerCase() + "%"));
            }

            if (ValidationUtils.isValid(searchBean.getContactMobile())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("contactMobile")),
                                "%" + searchBean.getContactMobile().toLowerCase() + "%"));
            }

            if (ValidationUtils.isValid(searchBean.getContactEmail())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("contactEmail")),
                                "%" + searchBean.getContactEmail().toLowerCase() + "%"));
            }

            return ValidationUtils.isValid(predicates)
                    ? cb.and(predicates.toArray(new Predicate[0]))
                    : null;
        };
    }
}