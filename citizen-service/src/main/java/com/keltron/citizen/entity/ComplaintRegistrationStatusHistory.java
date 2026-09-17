package com.keltron.citizen.entity;

import java.sql.Timestamp;

import com.keltron.citizen.dto.ComplaintRegistrationStatusHistoryDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
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
@Entity
@Table(name = "complaint_registration_status_history", schema = "awb")
@NoArgsConstructor
@ToString
public class ComplaintRegistrationStatusHistory extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "complaint_id", nullable = false)
    private ComplaintRegistration complaint;

    @Column(name = "from_status", length = 50)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 50)
    private String toStatus;

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

        ComplaintRegistrationStatusHistoryDto historyDto =
                (ComplaintRegistrationStatusHistoryDto) dto;

        if (ValidationUtils.isValid(historyDto.getId()))
            this.id = historyDto.getId();

        if (ValidationUtils.isValid(historyDto.getComplaint()))
            this.complaint =
                    new ComplaintRegistration(
                            historyDto.getComplaint().getId());

        if (ValidationUtils.isValid(historyDto.getFromStatus()))
            this.fromStatus = historyDto.getFromStatus();

        if (ValidationUtils.isValid(historyDto.getToStatus()))
            this.toStatus = historyDto.getToStatus();

        if (ValidationUtils.isValid(historyDto.getChangedBy()))
            this.changedBy = historyDto.getChangedBy();

        if (historyDto.getChangedAt() != null)
            this.changedAt =
                    new Timestamp(historyDto.getChangedAt());

        if (ValidationUtils.isValid(historyDto.getRemarks()))
            this.remarks = historyDto.getRemarks();

        if (ValidationUtils.isValid(historyDto.getActionType()))
            this.actionType = historyDto.getActionType();
    }

    @Override
    public ComplaintRegistrationStatusHistoryDto toDTO() {

        ComplaintRegistrationStatusHistoryDto dto =
                new ComplaintRegistrationStatusHistoryDto();

        dto.setId(id);
        dto.setFromStatus(fromStatus);
        dto.setToStatus(toStatus);
        dto.setChangedBy(changedBy);
        dto.setRemarks(remarks);
        dto.setActionType(actionType);

        if (changedAt != null)
            dto.setChangedAt(changedAt.getTime());

        if (complaint != null) {

            DropdownPayload<Long> payload =
                    new DropdownPayload<>();

            payload.setId(complaint.getId());
            payload.setName(complaint.getComplaintNumber());

            dto.setComplaint(payload);
        }

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(actionType);

        return payload;
    }

    public ComplaintRegistrationStatusHistory(Long id) {
        this.id = id;
    }
}