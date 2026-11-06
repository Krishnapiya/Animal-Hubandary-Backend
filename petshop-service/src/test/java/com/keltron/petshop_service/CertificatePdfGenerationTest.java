package com.keltron.petshop_service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.keltron.petshop.dto.PetShopRegistrationViewDto;
import com.keltron.petshop.services.impl.PetShopRegistrationPdfService;

public class CertificatePdfGenerationTest {

    @Test
    public void testGenerateCertificatePdf() throws Exception {
        PetShopRegistrationPdfService service = new PetShopRegistrationPdfService();

        PetShopRegistrationViewDto dto = new PetShopRegistrationViewDto();
        dto.setOwnerName("ATHUL M.S");
        dto.setAddressLine1("40/477 Govt Quarters");
        dto.setAddressLine2("Melarannoor, Quarters");
        dto.setCity("TRIVANDRUM");
        dto.setAnimals(new ArrayList<>());

        byte[] pdfBytes = service.generateCertificatePdf("KL-02-2026-PSL-001", dto, LocalDate.now());

        Assertions.assertNotNull(pdfBytes);
        Assertions.assertTrue(pdfBytes.length > 500);

        String pdfMagic = new String(pdfBytes, 0, 4);
        Assertions.assertEquals("%PDF", pdfMagic);

        try (FileOutputStream fos = new FileOutputStream("/tmp/test_certificate_no_footnote.pdf")) {
            fos.write(pdfBytes);
        }
        System.out.println("Generated Certificate PDF written to /tmp/test_certificate_no_footnote.pdf, size: " + pdfBytes.length);
    }
}

