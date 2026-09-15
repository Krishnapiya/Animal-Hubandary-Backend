package com.keltron.dogbreeder.entity;

import java.sql.Timestamp;

import com.keltron.dogbreeder.dto.DogBreederApplicationStatusHistoryDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.constants.ApplicationStatus;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(
    name = "dog_breeder_application_status_history",
    schema = "awb"
)
@NoArgsConstructor
@ToString
public class DogBreederApplicationStatusHistory
        extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /*
     * Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * application_id
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "application_id",
        nullable = false
    )
    private RegistrationApplication application;

    /*
     * from_status
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "from_status",
        length = 50
    )
    private ApplicationStatus fromStatus;

    /*
     * to_status
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "to_status",
        nullable = false,
        length = 50
    )
    private ApplicationStatus toStatus;

    /*
     * changed_by
     */
    @Column(
        name = "changed_by",
        length = 100
    )
    private String changedBy;

    /*
     * changed_at
     */
    @Column(
        name = "changed_at"
    )
    private Timestamp changedAt;

    /*
     * remarks
     */
    @Column(
        name = "remarks",
        columnDefinition = "TEXT"
    )
    private String remarks;

    /*
     * action_type
     */
    @Column(
        name = "action_type",
        length = 50
    )
    private String actionType;

    /*
     * =====================================================
     * AUDIT FIELDS
     * =====================================================
     */

    /*
     * created_by
     */
    @Column(
        name = "created_by",
        length = 255
    )
    private String createdBy;

    /*
     * created_at
     */
    @Column(
        name = "created_at",
        nullable = false
    )
    private Timestamp createdAt;

    /*
     * last_modified_by
     */
    @Column(
        name = "last_modified_by",
        length = 255
    )
    private String lastModifiedBy;

    /*
     * last_modified_at
     */
    @Column(
        name = "last_modified_at"
    )
    private Timestamp lastModifiedAt;


    /*
     * =====================================================
     * COPY FROM DTO
     * =====================================================
     */

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DogBreederApplicationStatusHistoryDto historyDto =
                (DogBreederApplicationStatusHistoryDto) dto;

        if (ValidationUtils.isValid(historyDto.getId())) {
            this.id = historyDto.getId();
        }

        if (ValidationUtils.isValid(
                historyDto.getApplication())) {

            this.application =
                    new RegistrationApplication(
                            historyDto
                                    .getApplication()
                                    .getId());
        }

        if (historyDto.getFromStatus() != null) {
            this.fromStatus =
                    historyDto.getFromStatus();
        }

        if (historyDto.getToStatus() != null) {
            this.toStatus =
                    historyDto.getToStatus();
        }

        if (ValidationUtils.isValid(
                historyDto.getChangedBy())) {

            this.changedBy =
                    historyDto.getChangedBy();
        }

        if (historyDto.getChangedAt() != null) {
            this.changedAt =
                    new Timestamp(
                            historyDto.getChangedAt());
        }

        if (ValidationUtils.isValid(
                historyDto.getRemarks())) {

            this.remarks =
                    historyDto.getRemarks();
        }

        if (ValidationUtils.isValid(
                historyDto.getActionType())) {

            this.actionType =
                    historyDto.getActionType();
        }

        /*
         * Audit fields
         */

        if (ValidationUtils.isValid(
                historyDto.getCreatedBy())) {

            this.createdBy =
                    historyDto.getCreatedBy();
        }

        if (historyDto.getCreatedAt() != null) {
            this.createdAt =
                    new Timestamp(
                            historyDto.getCreatedAt());
        }

        if (ValidationUtils.isValid(
                historyDto.getLastModifiedBy())) {

            this.lastModifiedBy =
                    historyDto.getLastModifiedBy();
        }

        if (historyDto.getLastModifiedAt() != null) {
            this.lastModifiedAt =
                    new Timestamp(
                            historyDto.getLastModifiedAt());
        }
    }


    /*
     * =====================================================
     * TO DTO
     * =====================================================
     */

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederApplicationStatusHistoryDto toDTO() {

        DogBreederApplicationStatusHistoryDto dto =
                new DogBreederApplicationStatusHistoryDto();

        dto.setId(id);

        dto.setFromStatus(fromStatus);

        dto.setToStatus(toStatus);

        dto.setChangedBy(changedBy);

        dto.setRemarks(remarks);

        dto.setActionType(actionType);

        /*
         * Changed At
         */
        if (changedAt != null) {
            dto.setChangedAt(
                    changedAt.getTime());
        }

        /*
         * Application
         */
        if (application != null) {

            DropdownPayload<Long> payload =
                    new DropdownPayload<>();

            payload.setId(
                    application.getId());

            payload.setName(
                    application.getApplicationNumber());

            dto.setApplication(payload);
        }

        /*
         * Audit fields
         */

        dto.setCreatedBy(createdBy);

        if (createdAt != null) {
            dto.setCreatedAt(
                    createdAt.getTime());
        }

        dto.setLastModifiedBy(
                lastModifiedBy);

        if (lastModifiedAt != null) {
            dto.setLastModifiedAt(
                    lastModifiedAt.getTime());
        }

        return dto;
    }


    /*
     * =====================================================
     * DROPDOWN
     * =====================================================
     */

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);

        payload.setName(actionType);

        return payload;
    }


    /*
     * Constructor with ID
     */
    public DogBreederApplicationStatusHistory(Long id) {
        this.id = id;
    }
}