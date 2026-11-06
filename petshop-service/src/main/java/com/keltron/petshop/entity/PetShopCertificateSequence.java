package com.keltron.petshop.entity;

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
@Table(name = "pet_shop_certificate_sequence", schema = "awb")
@NoArgsConstructor
@ToString
public class PetShopCertificateSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "issue_year", nullable = false)
    private Integer issueYear;

    @Column(name = "last_serial_number", nullable = false)
    private Integer lastSerialNumber = 0;
}
