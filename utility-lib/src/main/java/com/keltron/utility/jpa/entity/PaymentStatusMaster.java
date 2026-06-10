package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.PaymentStatusMasterDto;
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
@Table(name = "payment_status_master", schema = "awb")
@Entity
@ToString
@NoArgsConstructor
public class PaymentStatusMaster extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "active")
    private Boolean active;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {
    	PaymentStatusMasterDto statusDto = (PaymentStatusMasterDto) dto;

        if (ValidationUtils.isValid(statusDto.getId()))
            id = statusDto.getId();

        if (ValidationUtils.isValid(statusDto.getStatusCode()))
            statusCode = statusDto.getStatusCode();

        if (ValidationUtils.isValid(statusDto.getStatusName()))
            statusName = statusDto.getStatusName();

        if (ValidationUtils.isValid(statusDto.getDisplayOrder()))
            displayOrder = statusDto.getDisplayOrder();

        if (statusDto.getActive() != null)
            active = statusDto.getActive();
    }

    @Override
    public PaymentStatusMasterDto toDTO() {
    	PaymentStatusMasterDto dto = new PaymentStatusMasterDto();

        dto.setId(id);
        dto.setStatusCode(statusCode);
        dto.setStatusName(statusName);
        dto.setDisplayOrder(displayOrder);
        dto.setActive(active);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {
        DropdownPayload<Long> payload = new DropdownPayload<>();
        payload.setId(id);
        payload.setName(statusName);
        return payload;
    }

    public PaymentStatusMaster(Long id) {
        this.id = id;
    }
}