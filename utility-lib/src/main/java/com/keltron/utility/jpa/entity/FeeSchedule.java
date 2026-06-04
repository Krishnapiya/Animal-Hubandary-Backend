package com.keltron.utility.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.FeeScheduleDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "fee_schedule", schema = "awb")
@NoArgsConstructor
@ToString
public class FeeSchedule extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "fee_kind", nullable = false)
    private String feeKind;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(nullable = false)
    private Boolean active = true;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        FeeScheduleDto feeDto = (FeeScheduleDto) dto;

        if (ValidationUtils.isValid(feeDto.getId()))
            this.id = feeDto.getId();

        if (ValidationUtils.isValid(feeDto.getEntityType()))
            this.entityType = feeDto.getEntityType();

        if (ValidationUtils.isValid(feeDto.getFeeKind()))
            this.feeKind = feeDto.getFeeKind();

        if (feeDto.getAmount() != null)
            this.amount = feeDto.getAmount();

        if (ValidationUtils.isValid(feeDto.getCurrency()))
            this.currency = feeDto.getCurrency();

        if (feeDto.getEffectiveFrom() != null)
            this.effectiveFrom = feeDto.getEffectiveFrom();

        this.effectiveTo = feeDto.getEffectiveTo();

        if (feeDto.getActive() != null)
            this.active = feeDto.getActive();
    }

    @Override
    public FeeScheduleDto toDTO() {

        FeeScheduleDto dto = new FeeScheduleDto();

        dto.setId(id);
        dto.setEntityType(entityType);
        dto.setFeeKind(feeKind);
        dto.setAmount(amount);
        dto.setCurrency(currency);
        dto.setEffectiveFrom(effectiveFrom);
        dto.setEffectiveTo(effectiveTo);
        dto.setActive(active);

        return dto;
    }

    @Override
    public DropdownPayload<Integer> toDropDownPayload() {

        DropdownPayload<Integer> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(entityType + " - " + feeKind);

        return payload;
    }
}