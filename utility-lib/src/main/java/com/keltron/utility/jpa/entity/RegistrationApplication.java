package com.keltron.utility.jpa.entity;

import java.time.LocalDateTime;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.RegistrationApplicationDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "registration_application", schema = "awb")
@NoArgsConstructor
@ToString
public class RegistrationApplication extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_number")
    private String applicationNumber;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "application_kind", nullable = false)
    private String applicationKind;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private ApplicationStatusMaster status;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @Column(name = "applicant_user_id")
    private Long applicantUserId;

    @Column(name = "assigned_office_id")
    private Long assignedOfficeId;

    @Column(name = "cv_office_id")
    private Long cvOfficeId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "forwarded_to_cvo_at")
    private LocalDateTime forwardedToCvoAt;

    @Column(name = "forwarded_to_cvo_by")
    private Long forwardedToCvoBy;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        RegistrationApplicationDto appDto =
                (RegistrationApplicationDto) dto;

        if (ValidationUtils.isValid(appDto.getId()))
            this.id = appDto.getId();

        if (ValidationUtils.isValid(appDto.getApplicationNumber()))
            this.applicationNumber = appDto.getApplicationNumber();

        if (ValidationUtils.isValid(appDto.getEntityType()))
            this.entityType = appDto.getEntityType();

        if (ValidationUtils.isValid(appDto.getApplicationKind()))
            this.applicationKind = appDto.getApplicationKind();

        if (ValidationUtils.isValid(appDto.getStatusId()))
            this.status = new ApplicationStatusMaster(appDto.getStatusId());

        if (ValidationUtils.isValid(appDto.getDistrictId()))
            this.district = new District(appDto.getDistrictId());
        if (appDto.getApplicantUserId() != null) {
            this.applicantUserId = appDto.getApplicantUserId();
        }

        if (appDto.getAssignedOfficeId() != null) {
            this.assignedOfficeId = appDto.getAssignedOfficeId();
        }

        if (appDto.getCvOfficeId() != null) {
            this.cvOfficeId = appDto.getCvOfficeId();
        }

//        this.applicantUserId = appDto.getApplicantUserId();
//        this.assignedOfficeId = appDto.getAssignedOfficeId();
//        this.cvOfficeId = appDto.getCvOfficeId();
        this.paymentId = appDto.getPaymentId();
        this.submittedAt = appDto.getSubmittedAt();
        this.forwardedToCvoAt = appDto.getForwardedToCvoAt();
        this.forwardedToCvoBy = appDto.getForwardedToCvoBy();
    }

    @Override
    public RegistrationApplicationDto toDTO() {

        RegistrationApplicationDto dto =
                new RegistrationApplicationDto();

        dto.setId(id);
        dto.setApplicationNumber(applicationNumber);
        dto.setEntityType(entityType);
        dto.setApplicationKind(applicationKind);

        if (status != null) {
            dto.setStatusId(status.getId());
            dto.setStatus(status.toDropDownPayload());
        }

        if (district != null) {
            dto.setDistrictId(district.getId());
            dto.setDistrict(district.toDropDownPayload());
        }

        dto.setApplicantUserId(applicantUserId);
        dto.setAssignedOfficeId(assignedOfficeId);
        dto.setCvOfficeId(cvOfficeId);
        dto.setPaymentId(paymentId);
        dto.setSubmittedAt(submittedAt);
        dto.setForwardedToCvoAt(forwardedToCvoAt);
        dto.setForwardedToCvoBy(forwardedToCvoBy);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(applicationNumber);

        return payload;
    }

    public RegistrationApplication(Long id) {
        this.id = id;
    }
}