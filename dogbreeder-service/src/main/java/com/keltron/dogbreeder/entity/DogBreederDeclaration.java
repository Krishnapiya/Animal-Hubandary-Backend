package com.keltron.dogbreeder.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.keltron.dogbreeder.dto.DogBreederDeclarationDto;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "dog_breeder_declaration", schema = "awb")
@NoArgsConstructor
@ToString
public class DogBreederDeclaration extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "dog_breeder_detail_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true
    )
    private DogBreederDetail dogBreederDetail;

    @Column(name = "qualification_experience", columnDefinition = "TEXT")
    private String qualificationExperience;

    @Column(name = "declaration_accepted")
    private Boolean declarationAccepted;

    @Column(name = "declaration_place", length = 150)
    private String declarationPlace;

    @Column(name = "declaration_date")
    private LocalDate declarationDate;

    @Column(name = "applicant_name", length = 200)
    private String applicantName;

    @Column(name = "signature_name", length = 200)
    private String signatureName;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DogBreederDeclarationDto declarationDto =
                (DogBreederDeclarationDto) dto;

        if (ValidationUtils.isValid(declarationDto.getId())) {
            this.id = declarationDto.getId();
        }

        if (declarationDto.getDogBreederDetail() != null
                && declarationDto.getDogBreederDetail().getId() != null) {
            this.dogBreederDetail =
                    new DogBreederDetail(
                            declarationDto.getDogBreederDetail().getId()
                    );
        }

        this.qualificationExperience =
                declarationDto.getQualificationExperience();

        this.declarationAccepted =
                declarationDto.getDeclarationAccepted();

        this.declarationPlace =
                declarationDto.getDeclarationPlace();

        this.declarationDate =
                declarationDto.getDeclarationDate();

        this.applicantName =
                declarationDto.getApplicantName();

        this.signatureName =
                declarationDto.getSignatureName();

        this.signedAt =
                declarationDto.getSignedAt();
    }

    @SuppressWarnings("unchecked")
	@Override
    public DogBreederDeclarationDto toDTO() {

        DogBreederDeclarationDto dto =
                new DogBreederDeclarationDto();

        dto.setId(id);

        if (dogBreederDetail != null) {
            DropdownPayload<Long> detailPayload =
                    new DropdownPayload<>();

            detailPayload.setId(dogBreederDetail.getId());
            detailPayload.setName(dogBreederDetail.getBreederName());

            dto.setDogBreederDetail(detailPayload);
        }

        dto.setQualificationExperience(qualificationExperience);
        dto.setDeclarationAccepted(declarationAccepted);
        dto.setDeclarationPlace(declarationPlace);
        dto.setDeclarationDate(declarationDate);
        dto.setApplicantName(applicantName);
        dto.setSignatureName(signatureName);
        dto.setSignedAt(signedAt);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);

        if (dogBreederDetail != null) {
            payload.setName(
                    "Declaration - " + dogBreederDetail.getBreederName()
            );
        } else {
            payload.setName("Declaration - " + id);
        }

        return payload;
    }

    public DogBreederDeclaration(Long id) {
        this.id = id;
    }
}