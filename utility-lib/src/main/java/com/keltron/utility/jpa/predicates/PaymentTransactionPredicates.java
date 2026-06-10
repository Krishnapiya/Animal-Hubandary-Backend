package com.keltron.utility.jpa.predicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.searchbean.PaymentTransactionSearchBean;
import com.keltron.utility.jpa.entity.PaymentTransaction;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;


public class PaymentTransactionPredicates {
	public static Specification<PaymentTransaction> createPredicate(
			PaymentTransactionSearchBean searchBean) {
		 return (root, query, criteriaBuilder) -> {

	            List<Predicate> predicates = new ArrayList<>();

	            Join<Object, Object> applicationJoin =
	                    root.join("application", JoinType.LEFT);

	            Join<Object, Object> statusJoin =
	                    root.join("status", JoinType.LEFT);

	            Join<Object, Object> payerUserJoin =
	                    root.join("payerUser", JoinType.LEFT);

	            // ID
	            if (ValidationUtils.isValid(searchBean.getId())) {
	                predicates.add(
	                        criteriaBuilder.equal(
	                                root.get("id"),
	                                searchBean.getId()));
	            }

	            // Transaction Ref
	            if (ValidationUtils.isValid(searchBean.getTransactionRef())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("transactionRef")),
	                                "%" + searchBean.getTransactionRef().toLowerCase() + "%"));
	            }

	            // Application ID
	            if (ValidationUtils.isValid(searchBean.getApplicationId())) {
	                predicates.add(
	                        criteriaBuilder.equal(
	                                applicationJoin.get("id"),
	                                searchBean.getApplicationId()));
	            }

	            // Payment Purpose
	            if (ValidationUtils.isValid(searchBean.getPaymentPurpose())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("paymentPurpose")),
	                                "%" + searchBean.getPaymentPurpose().toLowerCase() + "%"));
	            }

	            // Amount From
	            if (searchBean.getAmountFrom() != null) {
	                predicates.add(
	                        criteriaBuilder.greaterThanOrEqualTo(
	                                root.get("amount"),
	                                searchBean.getAmountFrom()));
	            }

	            // Amount To
	            if (searchBean.getAmountTo() != null) {
	                predicates.add(
	                        criteriaBuilder.lessThanOrEqualTo(
	                                root.get("amount"),
	                                searchBean.getAmountTo()));
	            }

	            // Currency
	            if (ValidationUtils.isValid(searchBean.getCurrency())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("currency")),
	                                "%" + searchBean.getCurrency().toLowerCase() + "%"));
	            }

	            // Status ID
	            if (ValidationUtils.isValid(searchBean.getStatusId())) {
	                predicates.add(
	                        criteriaBuilder.equal(
	                                statusJoin.get("id"),
	                                searchBean.getStatusId()));
	            }

	            // Gateway Name
	            if (ValidationUtils.isValid(searchBean.getGatewayName())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("gatewayName")),
	                                "%" + searchBean.getGatewayName().toLowerCase() + "%"));
	            }

	            // Gateway Order ID
	            if (ValidationUtils.isValid(searchBean.getGatewayOrderId())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("gatewayOrderId")),
	                                "%" + searchBean.getGatewayOrderId().toLowerCase() + "%"));
	            }

	            // Gateway Payment ID
	            if (ValidationUtils.isValid(searchBean.getGatewayPaymentId())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("gatewayPaymentId")),
	                                "%" + searchBean.getGatewayPaymentId().toLowerCase() + "%"));
	            }

	            // Receipt Number
	            if (ValidationUtils.isValid(searchBean.getReceiptNumber())) {
	                predicates.add(
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("receiptNumber")),
	                                "%" + searchBean.getReceiptNumber().toLowerCase() + "%"));
	            }

	            // Payment Date From
	            if (searchBean.getPaymentDateFrom() != null) {
	                LocalDateTime fromDate =
	                        searchBean.getPaymentDateFrom().atStartOfDay();

	                predicates.add(
	                        criteriaBuilder.greaterThanOrEqualTo(
	                                root.get("paymentDate"),
	                                fromDate));
	            }

	            // Payment Date To
	            if (searchBean.getPaymentDateTo() != null) {
	                LocalDateTime toDate =
	                        searchBean.getPaymentDateTo()
	                                .plusDays(1)
	                                .atStartOfDay();

	                predicates.add(
	                        criteriaBuilder.lessThan(
	                                root.get("paymentDate"),
	                                toDate));
	            }

	            // Payer User ID
	            if (ValidationUtils.isValid(searchBean.getPayerUserId())) {
	                predicates.add(
	                        criteriaBuilder.equal(
	                                payerUserJoin.get("id"),
	                                searchBean.getPayerUserId()));
	            }

	            // Global Search
	            if (ValidationUtils.isValid(searchBean.getSearch())) {

	                String keyword =
	                        "%" + searchBean.getSearch().toLowerCase() + "%";

	                Predicate byTransactionRef =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("transactionRef")),
	                                keyword);

	                Predicate byPaymentPurpose =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("paymentPurpose")),
	                                keyword);

	                Predicate byCurrency =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("currency")),
	                                keyword);

	                Predicate byGatewayName =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("gatewayName")),
	                                keyword);

	                Predicate byGatewayOrderId =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("gatewayOrderId")),
	                                keyword);

	                Predicate byGatewayPaymentId =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("gatewayPaymentId")),
	                                keyword);

	                Predicate byReceiptNumber =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(root.get("receiptNumber")),
	                                keyword);

	                Predicate byApplicationNumber =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(
	                                        applicationJoin.get("applicationNumber")),
	                                keyword);

	                Predicate byStatusName =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(
	                                        statusJoin.get("statusName")),
	                                keyword);

	                Predicate byPayerUsername =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(
	                                        payerUserJoin.get("username")),
	                                keyword);

	                Predicate byPayerFname =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(
	                                        payerUserJoin.get("fname")),
	                                keyword);

	                Predicate byPayerLname =
	                        criteriaBuilder.like(
	                                criteriaBuilder.lower(
	                                        payerUserJoin.get("lname")),
	                                keyword);

	                predicates.add(
	                        criteriaBuilder.or(
	                                byTransactionRef,
	                                byPaymentPurpose,
	                                byCurrency,
	                                byGatewayName,
	                                byGatewayOrderId,
	                                byGatewayPaymentId,
	                                byReceiptNumber,
	                                byApplicationNumber,
	                                byStatusName,
	                                byPayerUsername,
	                                byPayerFname,
	                                byPayerLname));
	            }

	            return ValidationUtils.isValid(predicates)
	                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
	                    : null;
	        };
	    }
}
