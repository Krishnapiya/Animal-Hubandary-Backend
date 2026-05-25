package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.PaymentModeDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "payment_mode", schema = "master")
@ToString
@NoArgsConstructor
public class PaymentMode extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {
        PaymentModeDto d = (PaymentModeDto) dto;
        if (ValidationUtils.isValid(d.getId())) {
            id = d.getId();
        }
        if (ValidationUtils.isValid(d.getName())) {
            name = d.getName();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PaymentModeDto toDTO() {
        PaymentModeDto dto = new PaymentModeDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    @Override
    public DropdownPayload<Integer> toDropDownPayload() {
        DropdownPayload<Integer> payLoad = new DropdownPayload<>();
        payLoad.setId(id);
        payLoad.setName(name);
        return payLoad;
    }
}
