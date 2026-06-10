package com.keltron.utility.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.PaymentTransactionDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "payment_transaction", schema = "awb")
@Entity
@ToString
@NoArgsConstructor
public class PaymentTransaction extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "transaction_ref", nullable = false, length = 150, unique = true)
    private String transactionRef;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private RegistrationApplication application;

    @Column(name = "payment_purpose", nullable = false, length = 30)
    private String paymentPurpose;
    
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    private PaymentStatusMaster status;

    @Column(name = "gateway_name", length = 100)
    private String gatewayName;

    @Column(name = "gateway_order_id", length = 200)
    private String gatewayOrderId;

    @Column(name = "gateway_payment_id", length = 200)
    private String gatewayPaymentId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gateway_response", columnDefinition = "jsonb")
    private JsonNode gatewayResponse;

    @Column(name = "receipt_number", length = 100, unique = true)
    private String receiptNumber;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_user_id")
    private Users payerUser;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        PaymentTransactionDto paymentDto = (PaymentTransactionDto) dto;

        if (ValidationUtils.isValid(paymentDto.getId())) {
            id = paymentDto.getId();
        }

        if (ValidationUtils.isValid(paymentDto.getTransactionRef())) {
            transactionRef = paymentDto.getTransactionRef();
        }

        if (ValidationUtils.isValid(paymentDto.getApplicationId())) {
            application = new RegistrationApplication(paymentDto.getApplicationId());
        }

        if (ValidationUtils.isValid(paymentDto.getPaymentPurpose())) {
            paymentPurpose = paymentDto.getPaymentPurpose();
        }
        
        if (paymentDto.getAmount() != null) {
            amount = paymentDto.getAmount();
        }

        if (ValidationUtils.isValid(paymentDto.getCurrency())) {
            currency = paymentDto.getCurrency();
        }
        
        if (ValidationUtils.isValid(paymentDto.getStatusId())) {
            status = new PaymentStatusMaster(paymentDto.getStatusId());
        }

        if (ValidationUtils.isValid(paymentDto.getGatewayName())) {
            gatewayName = paymentDto.getGatewayName();
        }

        if (ValidationUtils.isValid(paymentDto.getGatewayOrderId())) {
            gatewayOrderId = paymentDto.getGatewayOrderId();
        }

        if (ValidationUtils.isValid(paymentDto.getGatewayPaymentId())) {
            gatewayPaymentId = paymentDto.getGatewayPaymentId();
        }

        if (paymentDto.getGatewayResponse() != null) {
            gatewayResponse = paymentDto.getGatewayResponse();
        }

        if (ValidationUtils.isValid(paymentDto.getReceiptNumber())) {
            receiptNumber = paymentDto.getReceiptNumber();
        }

        if (paymentDto.getPaymentDate() != null) {
            paymentDate = paymentDto.getPaymentDate();
        }

        if (ValidationUtils.isValid(paymentDto.getPayerUserId())) {
            payerUser = new Users(paymentDto.getPayerUserId());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PaymentTransactionDto toDTO() {

        PaymentTransactionDto dto = new PaymentTransactionDto();

        dto.setId(id);
        dto.setTransactionRef(transactionRef);

        if (application != null) {
            dto.setApplicationId(application.getId());
            dto.setApplicationNumber(application.getApplicationNumber());
        }

        dto.setPaymentPurpose(paymentPurpose);
        dto.setAmount(amount);
        dto.setCurrency(currency);

        if (status != null) {
            dto.setStatusId(status.getId());
            dto.setStatusName(status.getStatusName());
        }

        dto.setGatewayName(gatewayName);
        dto.setGatewayOrderId(gatewayOrderId);
        dto.setGatewayPaymentId(gatewayPaymentId);
        dto.setGatewayResponse(gatewayResponse);
        dto.setReceiptNumber(receiptNumber);
        dto.setPaymentDate(paymentDate);

        if (payerUser != null) {
            dto.setPayerUserId(payerUser.getId());

            String name = "";

            if (payerUser.getFname() != null) {
                name += payerUser.getFname();
            }

            if (payerUser.getLname() != null) {
                name += " " + payerUser.getLname();
            }

            name = name.trim();

            if (name.isBlank() && payerUser.getUsername() != null) {
                name = payerUser.getUsername();
            }

            dto.setPayerUserName(name);
        }

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(transactionRef);

        return payload;
    }

    public PaymentTransaction(Long id) {
        this.id = id;
    }
}

