package com.keltron.dogbreeder.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;

import com.keltron.dogbreeder.entity.DogBreederDeclaration;
import com.keltron.dogbreeder.entity.DogBreederDetail;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederDeclarationDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private DropdownPayload<Long> dogBreederDetail;

    private String qualificationExperience;

    private Boolean declarationAccepted;

    private String declarationPlace;

    private LocalDate declarationDate;

    private String applicantName;

    private String signatureName;

    private LocalDateTime signedAt;

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederDeclaration toEntity() {

        DogBreederDeclaration entity =
                new DogBreederDeclaration();

        entity.setId(id);

        if (dogBreederDetail != null
                && dogBreederDetail.getId() != null) {

            entity.setDogBreederDetail(
                    new DogBreederDetail(
                            dogBreederDetail.getId()
                    )
            );
        }

        entity.setQualificationExperience(
                qualificationExperience);

        entity.setDeclarationAccepted(
                declarationAccepted);

        entity.setDeclarationPlace(
                declarationPlace);

        entity.setDeclarationDate(
                declarationDate);

        entity.setApplicantName(
                applicantName);

        entity.setSignatureName(
                signatureName);

        entity.setSignedAt(
                signedAt);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        if (dogBreederDetail == null
                || !ValidationUtils.isValid(
                        dogBreederDetail.getId())) {

            addError(
                    "dog_breeder_detail_id",
                    dogBreederDetail
            );
        }

        if (!ValidationUtils.isValid(
                qualificationExperience)) {

            addError(
                    "qualificationExperience",
                    qualificationExperience
            );
        }

        if (declarationAccepted == null
                || !declarationAccepted) {

            addError(
                    "declarationAccepted",
                    declarationAccepted
            );
        }

        if (!ValidationUtils.isValid(
                declarationPlace)) {

            addError(
                    "declarationPlace",
                    declarationPlace
            );
        }

        if (declarationDate == null) {

            addError(
                    "declarationDate",
                    declarationDate
            );
        }

        return getErrors() == null
                || getErrors().isEmpty();
    }
}