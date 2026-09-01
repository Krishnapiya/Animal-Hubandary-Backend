package com.keltron.petshop.entity;

import java.sql.Timestamp;

import com.keltron.petshop.dto.RegistrationApplicationStatusHistoryDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "registration_application_status_history", schema = "awb")
@NoArgsConstructor
@ToString
public class RegistrationApplicationStatusHistory extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private PetShopRegistrationApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 50)
    private ApplicationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 50)
    private ApplicationStatus toStatus;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private Timestamp changedAt;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        RegistrationApplicationStatusHistoryDto historyDto =
                (RegistrationApplicationStatusHistoryDto) dto;

        if (ValidationUtils.isValid(historyDto.getId()))
            this.id = historyDto.getId();

        if (ValidationUtils.isValid(historyDto.getApplication()))
            this.application =
                    new PetShopRegistrationApplication(
                            historyDto.getApplication().getId());

        if (historyDto.getFromStatus() != null)
            this.fromStatus = historyDto.getFromStatus();

        if (historyDto.getToStatus() != null)
            this.toStatus = historyDto.getToStatus();

        if (ValidationUtils.isValid(historyDto.getChangedBy()))
            this.changedBy = historyDto.getChangedBy();

        if (historyDto.getChangedAt() != null)
            this.changedAt =
                    new java.sql.Timestamp(historyDto.getChangedAt());

        if (ValidationUtils.isValid(historyDto.getRemarks()))
            this.remarks = historyDto.getRemarks();

        if (ValidationUtils.isValid(historyDto.getActionType()))
            this.actionType = historyDto.getActionType();
    }

    @Override
    public RegistrationApplicationStatusHistoryDto toDTO() {

        RegistrationApplicationStatusHistoryDto dto =
                new RegistrationApplicationStatusHistoryDto();

        dto.setId(id);
        dto.setFromStatus(fromStatus);
        dto.setToStatus(toStatus);
        dto.setChangedBy(changedBy);
        dto.setRemarks(remarks);
        dto.setActionType(actionType);

        if (changedAt != null) {
            dto.setChangedAt(changedAt.getTime());
        }

        if (application != null) {

            DropdownPayload<Long> payload =
                    new DropdownPayload<>();

            payload.setId(application.getId());
            payload.setName(application.getApplicationNumber());

            dto.setApplication(payload);
        }

        return dto;
    }
    

    @Override
    public DropdownPayload<Long> toDropDownPayload() {
        DropdownPayload<Long> payload = new DropdownPayload<>();
        payload.setId(id);
        payload.setName(actionType);
        return payload;
    }

    public RegistrationApplicationStatusHistory(Long id) {
        this.id = id;
    }
}