package com.keltron.utility.beans.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.FeeSchedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeScheduleDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String entityType;
    private String feeKind;
    private BigDecimal amount;
    private String currency;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean active;

    @Override
    public FeeSchedule toEntity() {

        FeeSchedule entity = new FeeSchedule();

        entity.setId(id);
        entity.setEntityType(entityType);
        entity.setFeeKind(feeKind);
        entity.setAmount(amount);
        entity.setCurrency(currency);
        entity.setEffectiveFrom(effectiveFrom);
        entity.setEffectiveTo(effectiveTo);
        entity.setActive(active);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}