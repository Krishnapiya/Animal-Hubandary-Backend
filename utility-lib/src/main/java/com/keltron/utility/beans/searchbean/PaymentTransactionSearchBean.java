package com.keltron.utility.beans.searchbean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionSearchBean extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String transactionRef;

    private List<Long> applicationId;
    private List<Long> statusId;
    private List<Long> payerUserId;

    private String paymentPurpose;

    private BigDecimal amountFrom;
    private BigDecimal amountTo;

    private String currency;

    private String gatewayName;
    private String gatewayOrderId;
    private String gatewayPaymentId;

    private String receiptNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate paymentDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate paymentDateTo;

    private String search;

    private String sortBy = "id";
    private String sortOrder = "asc";

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "transactionRef",
            "paymentPurpose",
            "amount",
            "currency",
            "gatewayName",
            "gatewayOrderId",
            "gatewayPaymentId",
            "receiptNumber",
            "paymentDate"
    );

    public PaymentTransactionSearchBean() {
        dataSort = Sort.by(Sort.Order.asc(sortBy));
    }

    public Sort getSort() {
        return "desc".equalsIgnoreCase(sortOrder)
                ? Sort.by(Sort.Order.desc(sortBy))
                : Sort.by(Sort.Order.asc(sortBy));
    }

    public void setSortBy(String sortBy) {
        if (ALLOWED_SORT_FIELDS.contains(sortBy)) {
            this.sortBy = sortBy;
        } else {
            this.sortBy = "id";
        }

        super.setDataSort(getSort());
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        super.setDataSort(getSort());
    }
}