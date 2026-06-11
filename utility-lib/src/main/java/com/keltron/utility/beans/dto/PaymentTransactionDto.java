package com.keltron.utility.beans.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.PaymentStatusMaster;
import com.keltron.utility.jpa.entity.PaymentTransaction;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.jpa.entity.Users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String transactionRef;

    private Long applicationId;
    private String applicationNumber;

    private String paymentPurpose;

    private BigDecimal amount;

    private String currency;

    private Long statusId;
    private String statusName;

    private String gatewayName;

    private String gatewayOrderId;

    private String gatewayPaymentId;

    private JsonNode gatewayResponse;

    private String receiptNumber;

    private LocalDateTime paymentDate;

    private Long payerUserId;
    private String payerUserName;

    @SuppressWarnings("unchecked")
    @Override
    public PaymentTransaction toEntity() {

        PaymentTransaction entity = new PaymentTransaction();

        if (ValidationUtils.isValid(id)) {
            entity.setId(id);
        }

        if (ValidationUtils.isValid(transactionRef)) {
            entity.setTransactionRef(transactionRef);
        }

        if (ValidationUtils.isValid(applicationId)) {
            entity.setApplication(new RegistrationApplication(applicationId));
        }

        if (ValidationUtils.isValid(paymentPurpose)) {
            entity.setPaymentPurpose(paymentPurpose);
        }

        if (amount != null) {
            entity.setAmount(amount);
        }

        if (ValidationUtils.isValid(currency)) {
            entity.setCurrency(currency);
        }

        if (ValidationUtils.isValid(statusId)) {
            entity.setStatus(new PaymentStatusMaster(statusId));
        }

        if (ValidationUtils.isValid(gatewayName)) {
            entity.setGatewayName(gatewayName);
        }

        if (ValidationUtils.isValid(gatewayOrderId)) {
            entity.setGatewayOrderId(gatewayOrderId);
        }

        if (ValidationUtils.isValid(gatewayPaymentId)) {
            entity.setGatewayPaymentId(gatewayPaymentId);
        }

        if (gatewayResponse != null) {
            entity.setGatewayResponse(gatewayResponse);
        }

        if (ValidationUtils.isValid(receiptNumber)) {
            entity.setReceiptNumber(receiptNumber);
        }

        if (paymentDate != null) {
            entity.setPaymentDate(paymentDate);
        }

        if (ValidationUtils.isValid(payerUserId)) {
            entity.setPayerUser(new Users(payerUserId));
        }

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.POST)) {

            if (!ValidationUtils.isValid(transactionRef)) {
                addError("transactionRef", transactionRef);
            }

            if (!ValidationUtils.isValid(applicationId)) {
                addError("applicationId", applicationId);
            }

            if (!ValidationUtils.isValid(paymentPurpose)) {
                addError("paymentPurpose", paymentPurpose);
            }

            if (amount == null) {
                addError("amount", amount);
            }

            if (!ValidationUtils.isValid(currency)) {
                addError("currency", currency);
            }

            if (!ValidationUtils.isValid(statusId)) {
                addError("statusId", statusId);
            }
        }

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}