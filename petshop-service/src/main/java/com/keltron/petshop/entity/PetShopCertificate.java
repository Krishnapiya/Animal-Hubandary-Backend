package com.keltron.petshop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.keltron.utility.jpa.entity.District;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "pet_shop_certificate", schema = "awb")
@NoArgsConstructor
@ToString
public class PetShopCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private PetShopRegistrationApplication application;

    @Column(name = "certificate_number", nullable = false, unique = true)
    private String certificateNumber;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "issue_year", nullable = false)
    private Integer issueYear;

    @Column(name = "serial_number", nullable = false)
    private Integer serialNumber;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "generated_by")
    private Long generatedBy;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;
}
