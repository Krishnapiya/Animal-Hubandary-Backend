package com.keltron.utility.jpa.predicates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;


import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.UsersSearchBean;
import com.keltron.utility.jpa.entity.Users;

import jakarta.persistence.criteria.Predicate;

public class UsersPredicates {
	public static Specification<Users> createPredicate(UsersSearchBean searchBean) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (ValidationUtils.isValid(searchBean.getRoleId())) {
			    predicates.add(root.get("role").get("id").in(searchBean.getRoleId()));
			}
			if (ValidationUtils.isValid(searchBean.getGenderId())) {
			    predicates.add(root.get("genderId").get("id").in(searchBean.getGenderId()));
			}
			if (ValidationUtils.isValid(searchBean.getFname())) {
			    predicates.add(criteriaBuilder.like(root.get("fname"), "%" + searchBean.getFname() + "%"));
			}

			if (ValidationUtils.isValid(searchBean.getMobileNo())) {
			    predicates.add(criteriaBuilder.like(root.get("mobileNo"), "%" + searchBean.getMobileNo() + "%"));
			}
			if (ValidationUtils.isValid(searchBean.getEmail())) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
						"%" + searchBean.getEmail().toLowerCase() + "%"));
			}
			if (ValidationUtils.isValid(searchBean.getEmployeeCode())) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeCode")),
						"%" + searchBean.getEmployeeCode().toLowerCase() + "%"));
			}
			if(ValidationUtils.isValid(searchBean.getId())) {
				predicates.add(criteriaBuilder.equal(root.get("id"), searchBean.getId()));
			}
			
			if (ValidationUtils.isValid(searchBean.getSearch())) {
			    String keyword = "%" + searchBean.getSearch().toLowerCase() + "%";

			    Predicate byFname = criteriaBuilder.like(criteriaBuilder.lower(root.get("fname")), keyword);
			    Predicate byMobileNo = criteriaBuilder.like(criteriaBuilder.lower(root.get("mobileNo")), keyword);
			    Predicate byEmployeeCode = criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeCode")), keyword);
			    Predicate byUsername = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), keyword);
			    Predicate byGenderName = criteriaBuilder.like(criteriaBuilder.lower(root.get("genderId").get("name")), keyword);
			    Predicate byMail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), keyword);
			    predicates.add(criteriaBuilder.or(byFname, byMobileNo, byEmployeeCode, byUsername, byGenderName,byMail));
			}



			return ValidationUtils.isValid(predicates) ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
					: null;
		};
	}

}

