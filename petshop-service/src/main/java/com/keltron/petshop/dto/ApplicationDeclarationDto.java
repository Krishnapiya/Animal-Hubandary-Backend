package com.keltron.petshop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.petshop.entity.ApplicationDeclaration;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDeclarationDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long applicationId;

    private String declarationPlace;

    private LocalDate declarationDate;

    private Boolean informationAccurate;

    private Boolean affidavitRule2018Ack;

    private Boolean affidavitAwbiRulesAck;

    private Boolean affidavitConditionsAck;

    private Boolean affidavitCancellationAck;

    private Boolean affidavitTruthAck;

    private String affidavitDeponentName;

    private LocalDateTime signedAt;

    @Override
    public ApplicationDeclaration toEntity() {

        ApplicationDeclaration entity =
                new ApplicationDeclaration();

        entity.setId(id);
        entity.setApplicationId(applicationId);
        entity.setDeclarationPlace(declarationPlace);
        entity.setDeclarationDate(declarationDate);
        entity.setInformationAccurate(informationAccurate);
        entity.setAffidavitRule2018Ack(affidavitRule2018Ack);
        entity.setAffidavitAwbiRulesAck(affidavitAwbiRulesAck);
        entity.setAffidavitConditionsAck(affidavitConditionsAck);
        entity.setAffidavitCancellationAck(
                affidavitCancellationAck);
        entity.setAffidavitTruthAck(
                affidavitTruthAck);
        entity.setAffidavitDeponentName(
                affidavitDeponentName);
        entity.setSignedAt(signedAt);

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

        if (!ValidationUtils.isValid(applicationId)) {

            addError(
                    "applicationId",
                    applicationId);
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}