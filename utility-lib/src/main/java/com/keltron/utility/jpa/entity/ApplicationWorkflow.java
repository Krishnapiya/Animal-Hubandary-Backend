package com.keltron.utility.jpa.entity;

import java.time.LocalDateTime;

import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.ApplicationWorkflowDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "application_workflow", schema = "awb")
@NoArgsConstructor
public class ApplicationWorkflow extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "from_status_id")
    private ApplicationStatusMaster fromStatus;

    @ManyToOne
    @JoinColumn(name = "to_status_id")
    private ApplicationStatusMaster toStatus;

    @Column(name = "action_by")
    private Long actionBy;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        ApplicationWorkflowDto workflowDto =
                (ApplicationWorkflowDto) dto;

        this.id = workflowDto.getId();
        this.moduleName = workflowDto.getModuleName();
        this.applicationId = workflowDto.getApplicationId();

        if (workflowDto.getFromStatusId() != null) {
            this.fromStatus =
                    new ApplicationStatusMaster(
                            workflowDto.getFromStatusId());
        }

        if (workflowDto.getToStatusId() != null) {
            this.toStatus =
                    new ApplicationStatusMaster(
                            workflowDto.getToStatusId());
        }

        this.actionBy = workflowDto.getActionBy();
        this.remarks = workflowDto.getRemarks();
        this.actionDate = workflowDto.getActionDate();
    }

    @Override
    public ApplicationWorkflowDto toDTO() {

        ApplicationWorkflowDto dto =
                new ApplicationWorkflowDto();

        dto.setId(id);
        dto.setModuleName(moduleName);
        dto.setApplicationId(applicationId);

        if (fromStatus != null)
            dto.setFromStatusId(fromStatus.getId());

        if (toStatus != null)
            dto.setToStatusId(toStatus.getId());

        dto.setActionBy(actionBy);
        dto.setRemarks(remarks);
        dto.setActionDate(actionDate);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(moduleName);

        return payload;
    }
}