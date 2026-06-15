package com.keltron.petshop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.keltron.petshop.dto.ApplicationDeclarationDto;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
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
@Table(name = "application_declaration", schema = "awb")
@NoArgsConstructor
@ToString
public class ApplicationDeclaration extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, unique = true)
    private Long applicationId;

    @Column(name = "declaration_place")
    private String declarationPlace;

    @Column(name = "declaration_date")
    private LocalDate declarationDate;

    @Column(name = "information_accurate")
    private Boolean informationAccurate;

    @Column(name = "affidavit_rule_2018_ack")
    private Boolean affidavitRule2018Ack;

    @Column(name = "affidavit_awbi_rules_ack")
    private Boolean affidavitAwbiRulesAck;

    @Column(name = "affidavit_conditions_ack")
    private Boolean affidavitConditionsAck;

    @Column(name = "affidavit_cancellation_ack")
    private Boolean affidavitCancellationAck;

    @Column(name = "affidavit_truth_ack")
    private Boolean affidavitTruthAck;

    @Column(name = "affidavit_deponent_name")
    private String affidavitDeponentName;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        ApplicationDeclarationDto declarationDto =
                (ApplicationDeclarationDto) dto;

        if (ValidationUtils.isValid(declarationDto.getId()))
            this.id = declarationDto.getId();

        if (ValidationUtils.isValid(
                declarationDto.getApplicationId()))
            this.applicationId =
                    declarationDto.getApplicationId();

        this.declarationPlace =
                declarationDto.getDeclarationPlace();

        this.declarationDate =
                declarationDto.getDeclarationDate();

        this.informationAccurate =
                declarationDto.getInformationAccurate();

        this.affidavitRule2018Ack =
                declarationDto.getAffidavitRule2018Ack();

        this.affidavitAwbiRulesAck =
                declarationDto.getAffidavitAwbiRulesAck();

        this.affidavitConditionsAck =
                declarationDto.getAffidavitConditionsAck();

        this.affidavitCancellationAck =
                declarationDto.getAffidavitCancellationAck();

        this.affidavitTruthAck =
                declarationDto.getAffidavitTruthAck();

        this.affidavitDeponentName =
                declarationDto.getAffidavitDeponentName();

        this.signedAt =
                declarationDto.getSignedAt();
    }

    @Override
    public ApplicationDeclarationDto toDTO() {

        ApplicationDeclarationDto dto =
                new ApplicationDeclarationDto();

        dto.setId(id);
        dto.setApplicationId(applicationId);
        dto.setDeclarationPlace(declarationPlace);
        dto.setDeclarationDate(declarationDate);
        dto.setInformationAccurate(informationAccurate);
        dto.setAffidavitRule2018Ack(
                affidavitRule2018Ack);
        dto.setAffidavitAwbiRulesAck(
                affidavitAwbiRulesAck);
        dto.setAffidavitConditionsAck(
                affidavitConditionsAck);
        dto.setAffidavitCancellationAck(
                affidavitCancellationAck);
        dto.setAffidavitTruthAck(
                affidavitTruthAck);
        dto.setAffidavitDeponentName(
                affidavitDeponentName);
        dto.setSignedAt(signedAt);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(
                "Declaration - " + applicationId);

        return payload;
    }

    public ApplicationDeclaration(Long id) {
        this.id = id;
    }
}