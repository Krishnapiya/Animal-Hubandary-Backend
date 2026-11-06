package com.keltron.petshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import com.lowagie.text.Phrase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.dto.PetShopProposedAnimalDto;
import com.keltron.petshop.dto.PetShopRegistrationViewDto;
import com.keltron.petshop.entity.PetShopApplicationDocument;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import com.keltron.utility.responses.payload.DropdownPayload;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PetShopRegistrationPdfService {

    private static final Font CERT_NO_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font FORM_II_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font RULES_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font CERT_TITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
    private static final Font DECLARATION_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10.5f);
    private static final Font DECLARATION_BOLD_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 10.5f);
    private static final Font CONDITION_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 10f);
    private static final Font FOOTER_BOLD_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f);
    private static final Font FOOTER_NORMAL_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 9.5f);
    private static final Font SEAL_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f);

    private static final Font TITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

    private static final Font SUBTITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

    private static final Font BOLD_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    private static final Font NORMAL_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 10);

    private static final String[] SIGNATURE_TYPE_CODES = {
            "APPLICANT_SIGNATURE",
            "SIGNATURE",
            "ID_PROOF",
            "FACILITY_PHOTO"
    };

    @Value("${petshop.document.upload-root:}")
    private String configuredUploadRoot;

    /**
     * Generates a blank 3-page Pet Shop Inspection Report format PDF.
     * Reproduces the official layout of pages 1-3 of the reference inspection report.
     * Page 1: General Information & Infrastructure Assessment.
     * Page 2: Animal Housing Conditions, Animal Health and Welfare, Documentation Verification.
     * Page 3: Observations, Deficiencies Noted, Remarks, Signature block.
     *
     * @return PDF bytes of the blank report format
     */
    /**
     * Generates the official Pet Shop Certificate of Registration PDF (matching Form II / Page 4).
     */
    public byte[] generateCertificatePdf(
            String certNumber,
            PetShopRegistrationViewDto dto,
            LocalDate issueDate) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 45, 45, 45, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new WatermarkPageEvent());

            document.open();

            String applicantName = value(dto.getOwnerName());
            String applicantAddress = formatAddress(dto);
            String shopLocation = formatShopLocation(dto);
            String permittedAnimals = extractPermittedAnimals(dto.getAnimals());
            String formattedDate = issueDate != null
                    ? issueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    : LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // 1. Certificate Number
            Paragraph certNo = new Paragraph("No - " + certNumber, CERT_NO_FONT);
            certNo.setAlignment(Element.ALIGN_LEFT);
            certNo.setSpacingAfter(18);
            document.add(certNo);

            // 2. Heading
            Paragraph formII = new Paragraph("Form II", FORM_II_FONT);
            formII.setAlignment(Element.ALIGN_CENTER);
            formII.setSpacingAfter(4);
            document.add(formII);

            Paragraph rules = new Paragraph("[See Rules 3(7) and 4(5)]", RULES_FONT);
            rules.setAlignment(Element.ALIGN_CENTER);
            rules.setSpacingAfter(4);
            document.add(rules);

            Paragraph title = new Paragraph("Certificate of Registration of Pet Shop", CERT_TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 3. Declaration Paragraph
            Paragraph declaration = new Paragraph();
            declaration.setAlignment(Element.ALIGN_JUSTIFIED);
            declaration.setLeading(16f);
            declaration.setSpacingAfter(16);

            declaration.add(new Phrase("A Certificate of Registration is provisionally granted to ", DECLARATION_FONT));
            declaration.add(new Phrase(applicantName + ", " + applicantAddress, DECLARATION_BOLD_FONT));
            declaration.add(new Phrase(", to establish a pet shop as provided in the Prevention of Cruelty to Animals (Pet Shop) Rules, 2018.", DECLARATION_FONT));
            document.add(declaration);

            // 4. Conditions 1 - 7
            String[] conditions = {
                "1. The location of the pet shop is at " + shopLocation + ".",
                "2. The owner of the pet shop is permitted to deal with the following pet animals: " + permittedAnimals + ".",
                "3. The owner of the pet shop shall abide by the provisions of the Prevention of Cruelty to Animals Act, 1960, and the rules and notifications made and issued thereunder.",
                "4. The owner of the pet shop shall submit an annual report in compliance with clause (a) of sub-rule (1) of rule 12 of the aforesaid rules for the year ending 31 December to reach the undersigned before the 31 January of the succeeding year.",
                "5. The certificate shall be displayed prominently in the pet shop.",
                "6. The certificate is non-transferable.",
                "7. The Certificate shall be valid up to five years from the date of issue of this Certificate, and a renewal application should be submitted thirty days prior to the expiry date."
            };

            for (String cond : conditions) {
                Paragraph p = new Paragraph(cond, CONDITION_FONT);
                p.setAlignment(Element.ALIGN_JUSTIFIED);
                p.setLeading(14f);
                p.setSpacingAfter(8);
                document.add(p);
            }

            // 5. Signature Section
            Paragraph space = new Paragraph(" ");
            space.setSpacingBefore(30);
            document.add(space);

            PdfPTable sigTable = new PdfPTable(2);
            sigTable.setWidthPercentage(100);
            sigTable.setWidths(new float[]{45f, 55f});

            // Left Cell: Date
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(PdfPCell.NO_BORDER);
            leftCell.setVerticalAlignment(Element.ALIGN_TOP);
            Paragraph datePara = new Paragraph("Date: " + formattedDate, FOOTER_BOLD_FONT);
            leftCell.addElement(datePara);
            sigTable.addCell(leftCell);

            // Right Cell: Legitimate Issuing Authority
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(PdfPCell.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            Paragraph authority = new Paragraph("Signature and Seal of State Animal Welfare Board", FOOTER_NORMAL_FONT);
            Paragraph subAuth = new Paragraph("(Member Secretary-Kerala State)", FOOTER_NORMAL_FONT);

            rightCell.addElement(authority);
            rightCell.addElement(subAuth);

            sigTable.addCell(rightCell);
            document.add(sigTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Pet Shop Certificate PDF", e);
        }
    }

    public String formatShopLocation(PetShopRegistrationViewDto dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getAddressLine1() != null && !dto.getAddressLine1().isBlank() && !"-".equals(dto.getAddressLine1())) {
            sb.append(dto.getAddressLine1().trim());
        }
        if (dto.getAddressLine2() != null && !dto.getAddressLine2().isBlank() && !"-".equals(dto.getAddressLine2())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(dto.getAddressLine2().trim());
        }
        if (dto.getCity() != null && !dto.getCity().isBlank() && !"-".equals(dto.getCity())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(dto.getCity().trim());
        }
        return sb.length() > 0 ? sb.toString() : formatAddress(dto);
    }

    public String extractPermittedAnimals(List<PetShopProposedAnimalDto> animals) {
        if (animals == null || animals.isEmpty()) {
            return "cats, birds, and exotic pets";
        }
        List<String> speciesList = animals.stream()
                .map(a -> {
                    if (a.getSpecies() != null && a.getSpecies().getName() != null && !a.getSpecies().getName().isBlank()) {
                        return a.getSpecies().getName().trim().toLowerCase();
                    }
                    if (a.getCustomSpeciesName() != null && !a.getCustomSpeciesName().isBlank()) {
                        return a.getCustomSpeciesName().trim().toLowerCase();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (speciesList.isEmpty()) {
            return "cats, birds, and exotic pets";
        }
        if (speciesList.size() == 1) {
            return speciesList.get(0);
        }
        if (speciesList.size() == 2) {
            return speciesList.get(0) + " and " + speciesList.get(1);
        }
        return String.join(", ", speciesList.subList(0, speciesList.size() - 1))
                + ", and " + speciesList.get(speciesList.size() - 1);
    }

    public byte[] generateInspectionReportFormatPdf() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 30, 30);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new WatermarkPageEvent());

            document.open();

            // ==========================================
            // PAGE 1: General Information & Infrastructure Assessment
            // ==========================================
            Font reportTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Font.UNDERLINE);
            Paragraph title = new Paragraph("PET SHOP VERIFICATION REPORT", reportTitleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(14);
            document.add(title);

            Paragraph genInfoTitle = new Paragraph("1. General Information", BOLD_FONT);
            genInfoTitle.setSpacingAfter(8);
            document.add(genInfoTitle);

            String[] genInfoFields = {
                "Name of Pet Shop:",
                "Address:",
                "Name of Owner/Licensee:",
                "Contact Number:",
                "Date of Inspection:",
                "Time of Inspection:",
                "Name & Designation of Inspecting Veterinary Surgeon:"
            };

            for (String field : genInfoFields) {
                Paragraph p = new Paragraph("•   " + field, NORMAL_FONT);
                p.setSpacingAfter(6);
                document.add(p);
            }

            Paragraph infraTitle = new Paragraph("2. Infrastructure Assessment", BOLD_FONT);
            infraTitle.setSpacingBefore(14);
            infraTitle.setSpacingAfter(8);
            document.add(infraTitle);

            PdfPTable infraTable = new PdfPTable(5);
            infraTable.setWidthPercentage(100);
            infraTable.setWidths(new float[]{36f, 13f, 17f, 17f, 17f});

            infraTable.addCell(formatHeaderCell("Parameter", Element.ALIGN_CENTER));
            infraTable.addCell(formatHeaderCell("Good", Element.ALIGN_CENTER));
            infraTable.addCell(formatHeaderCell("Satisfactory", Element.ALIGN_CENTER));
            infraTable.addCell(formatHeaderCell("Not Satisfactory", Element.ALIGN_CENTER));
            infraTable.addCell(formatHeaderCell("Remarks", Element.ALIGN_CENTER));

            String[] infraParams = {
                "Adequate floor space",
                "Proper ventilation",
                "Adequate lighting",
                "Cleanliness and sanitation",
                "Waste disposal system",
                "Fire safety arrangements"
            };

            for (String param : infraParams) {
                PdfPCell paramCell = new PdfPCell(new Paragraph(param, NORMAL_FONT));
                paramCell.setPadding(6);
                paramCell.setMinimumHeight(28);
                paramCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                infraTable.addCell(paramCell);

                for (int c = 0; c < 4; c++) {
                    PdfPCell blankCell = new PdfPCell(new Paragraph("", NORMAL_FONT));
                    blankCell.setMinimumHeight(28);
                    infraTable.addCell(blankCell);
                }
            }
            document.add(infraTable);

            // ==========================================
            // PAGE 2: Animal Housing Conditions, Animal Health and Welfare, Documentation Verification
            // ==========================================
            document.newPage();

            Paragraph housingTitle = new Paragraph("3. Animal Housing Conditions", BOLD_FONT);
            housingTitle.setSpacingAfter(8);
            document.add(housingTitle);

            PdfPTable housingTable = new PdfPTable(5);
            housingTable.setWidthPercentage(100);
            housingTable.setWidths(new float[]{36f, 13f, 17f, 17f, 17f});

            housingTable.addCell(formatHeaderCell("Parameter", Element.ALIGN_CENTER));
            housingTable.addCell(formatHeaderCell("Good", Element.ALIGN_CENTER));
            housingTable.addCell(formatHeaderCell("Satisfactory", Element.ALIGN_CENTER));
            housingTable.addCell(formatHeaderCell("Not Satisfactory", Element.ALIGN_CENTER));
            housingTable.addCell(formatHeaderCell("Remarks", Element.ALIGN_CENTER));

            String[] housingParams = {
                "Species-wise segregation",
                "Adequate cage/enclosure size",
                "Clean drinking water available",
                "Adequate feeding arrangements",
                "Protection from extreme weather",
                "Overstocking absent"
            };

            for (String param : housingParams) {
                PdfPCell paramCell = new PdfPCell(new Paragraph(param, NORMAL_FONT));
                paramCell.setPadding(6);
                paramCell.setMinimumHeight(28);
                paramCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                housingTable.addCell(paramCell);

                for (int c = 0; c < 4; c++) {
                    PdfPCell blankCell = new PdfPCell(new Paragraph("", NORMAL_FONT));
                    blankCell.setMinimumHeight(28);
                    housingTable.addCell(blankCell);
                }
            }
            document.add(housingTable);

            Paragraph healthTitle = new Paragraph("4. Animal Health and Welfare", BOLD_FONT);
            healthTitle.setSpacingBefore(14);
            healthTitle.setSpacingAfter(8);
            document.add(healthTitle);

            String[] healthFields = {
                "Total number of animals present:",
                "Species maintained:",
                "General health status of animals:",
                "Evidence of disease/injury: Yes / No",
                "Isolation facility for sick animals: Yes / No",
                "Vaccination records available: Yes / No",
                "Deworming records available: Yes / No",
                "Veterinary treatment records maintained: Yes / No"
            };

            for (String field : healthFields) {
                Paragraph p = new Paragraph("•   " + field, NORMAL_FONT);
                p.setSpacingAfter(6);
                document.add(p);
            }

            Paragraph docTitle = new Paragraph("5. Documentation Verification", BOLD_FONT);
            docTitle.setSpacingBefore(14);
            docTitle.setSpacingAfter(8);
            document.add(docTitle);

            PdfPTable docTable = new PdfPTable(3);
            docTable.setWidthPercentage(100);
            docTable.setWidths(new float[]{50f, 25f, 25f});

            docTable.addCell(formatHeaderCell("Document", Element.ALIGN_LEFT));
            docTable.addCell(formatHeaderCell("Available", Element.ALIGN_CENTER));
            docTable.addCell(formatHeaderCell("Not Available", Element.ALIGN_CENTER));

            String[] docParams = {
                "Animal purchase/source records",
                "Health records",
                "Mortality records",
                "Veterinary care records"
            };

            for (String docParam : docParams) {
                PdfPCell paramCell = new PdfPCell(new Paragraph(docParam, NORMAL_FONT));
                paramCell.setPadding(6);
                paramCell.setMinimumHeight(28);
                paramCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                docTable.addCell(paramCell);

                for (int c = 0; c < 2; c++) {
                    PdfPCell blankCell = new PdfPCell(new Paragraph("", NORMAL_FONT));
                    blankCell.setMinimumHeight(28);
                    docTable.addCell(blankCell);
                }
            }
            document.add(docTable);

            // ==========================================
            // PAGE 3: Observations, Deficiencies, Remarks, Signatures
            // ==========================================
            document.newPage();

            Paragraph obsTitle = new Paragraph("6. Observations", BOLD_FONT);
            obsTitle.setSpacingAfter(6);
            document.add(obsTitle);
            document.add(formatWritingLines(4, 22f));

            Paragraph defTitle = new Paragraph("7. Deficiencies Noted (if any)", BOLD_FONT);
            defTitle.setSpacingBefore(14);
            defTitle.setSpacingAfter(6);
            document.add(defTitle);
            document.add(formatWritingLines(3, 22f));

            Paragraph remTitle = new Paragraph("8. Remarks", BOLD_FONT);
            remTitle.setSpacingBefore(14);
            remTitle.setSpacingAfter(6);
            document.add(remTitle);
            document.add(formatWritingLines(3, 22f));

            Paragraph sigSpace = new Paragraph(" ");
            sigSpace.setSpacingBefore(40);
            document.add(sigSpace);

            PdfPTable sigTable = new PdfPTable(2);
            sigTable.setWidthPercentage(100);
            sigTable.setWidths(new float[]{60f, 40f});

            PdfPCell leftSig = new PdfPCell();
            leftSig.setBorder(PdfPCell.NO_BORDER);
            leftSig.addElement(new Paragraph("Signature of Inspecting Veterinary Surgeon", BOLD_FONT));
            Paragraph datePara = new Paragraph("Date:", BOLD_FONT);
            datePara.setSpacingBefore(45);
            leftSig.addElement(datePara);
            sigTable.addCell(leftSig);

            PdfPCell rightSig = new PdfPCell();
            rightSig.setBorder(PdfPCell.NO_BORDER);
            sigTable.addCell(rightSig);

            document.add(sigTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate pet shop inspection report format PDF", e);
        }
    }

    private PdfPCell formatHeaderCell(String text, int align) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, BOLD_FONT));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }

    private PdfPTable formatWritingLines(int count, float lineHeight) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        for (int i = 0; i < count; i++) {
            PdfPCell cell = new PdfPCell(new Paragraph(" ", NORMAL_FONT));
            cell.setMinimumHeight(lineHeight);
            cell.setBorder(PdfPCell.BOTTOM);
            cell.setBorderColor(new java.awt.Color(200, 200, 200));
            cell.setBorderWidth(0.5f);
            table.addCell(cell);
        }
        return table;
    }

    @Autowired
    private PetShopRegistrationApplicationServiceImpl applicationService;

    @Autowired
    private PetShopApplicationDocumentRepository documentRepository;

    @Value("${application.document.upload-dir:}")
    private String uploadDir;

    /**
     * Generates the pet shop registration PDF as raw bytes.
     *
     * @param applicationId registration application id
     * @return PDF content
     */
    @Transactional(readOnly = true)
    public byte[] generateApplicationPdf(Long applicationId) {
        try {
            PetShopRegistrationViewDto dto =
                    applicationService.getApplication(applicationId);
            return buildApplicationPdf(dto, applicationId);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate pet shop application PDF",
                    e);
        }
    }

    /**
     * Builds a ZIP containing the application PDF and all uploaded attachments.
     *
     * @param applicationId registration application id
     * @return ZIP content
     */
    @Transactional(readOnly = true)
    public byte[] generateApplicationZip(Long applicationId) {
        try {
            PetShopRegistrationViewDto dto =
                    applicationService.getApplication(applicationId);
            byte[] pdfBytes = buildApplicationPdf(dto, applicationId);

            String pdfFileName = dto.getApplicationNumber() != null
                    && !dto.getApplicationNumber().isBlank()
                    ? dto.getApplicationNumber() + ".pdf"
                    : "PetShopApplication-" + applicationId + ".pdf";

            ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(zipOut)) {
                ZipEntry pdfEntry = new ZipEntry(pdfFileName);
                zip.putNextEntry(pdfEntry);
                zip.write(pdfBytes);
                zip.closeEntry();

                List<PetShopApplicationDocument> documents =
                        documentRepository.findByApplication_Id(applicationId);

                Set<String> usedNames = new HashSet<>();
                usedNames.add(pdfFileName.toLowerCase(Locale.ROOT));

                int index = 1;
                int attachedCount = 0;
                for (PetShopApplicationDocument document : documents) {
                    if (document.getFilePath() == null
                            || document.getFilePath().isBlank()) {
                        continue;
                    }

                    Path filePath = resolveExistingDocumentPath(
                            document.getFilePath());
                    if (filePath == null) {
                        System.out.println(
                                "ZIP skip missing attachment: "
                                        + document.getFilePath());
                        continue;
                    }

                    String entryName = buildAttachmentEntryName(
                            document,
                            index++,
                            usedNames);

                    ZipEntry attachmentEntry =
                            new ZipEntry("attachments/" + entryName);
                    zip.putNextEntry(attachmentEntry);
                    zip.write(Files.readAllBytes(filePath));
                    zip.closeEntry();
                    attachedCount++;
                }

                System.out.println(
                        "ZIP attachments included = " + attachedCount
                                + " for applicationId = " + applicationId);
            }

            return zipOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate pet shop application ZIP",
                    e);
        }
    }

    /**
     * Builds download response with PDF content and headers.
     *
     * @param id registration application id
     * @return PDF download response
     * @throws Exception if PDF generation fails
     */
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadApplication(Long id) throws Exception {
        PetShopRegistrationViewDto dto = applicationService.getApplication(id);
        byte[] zipBytes = generateApplicationZip(id);

        String fileName = dto.getApplicationNumber() != null
                && !dto.getApplicationNumber().isBlank()
                ? dto.getApplicationNumber() + ".zip"
                : "PetShopApplication-" + id + ".zip";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(fileName)
                        .build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(zipBytes);
    }

    private byte[] buildApplicationPdf(
            PetShopRegistrationViewDto dto,
            Long applicationId) throws Exception {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	Document document = new Document(PageSize.A4, 40, 40, 40, 40);

    	PdfWriter writer = PdfWriter.getInstance(document, out);
    	writer.setPageEvent(new WatermarkPageEvent());

    	document.open();

        addCenter(document, "KERALA STATE ANIMAL WELFARE BOARD", TITLE_FONT);
        addCenter(document, "THE FIRST SCHEDULE", SUBTITLE_FONT);
        addCenter(document, "FORM - I", SUBTITLE_FONT);
        addCenter(document, "(See rules 4(2) and 5)", NORMAL_FONT);
        addCenter(document, "APPLICATION FORM FOR REGISTRATION / RENEWAL", BOLD_FONT);
        addBlank(document);

        addText(document, "To,", NORMAL_FONT);
        addText(
                document,
                "Member Secretary / Convenor\n"
                        + "Kerala State Animal Welfare Board\n"
                        + "O/o Director, Animal Husbandry Department\n"
                        + "Vikas Bhavan, 6th Floor\n"
                        + "Thiruvananthapuram - 695033\n"
                        + "Email : sawbkerala@gmail.com",
                NORMAL_FONT);
        addBlank(document);

        addText(
                document,
                "Subject : Application for grant of licence for Pet Shop",
                BOLD_FONT);
        addBlank(document);

        String ownerAddress = formatAddress(dto);

        String introduction =
                "Sir,\n\n"
                        + "I/We "
                        + value(dto.getOwnerName())
                        + " r/o "
                        + ownerAddress
                        + " with office address "
                        + ownerAddress
                        + " do hereby apply for registration to operate / continue "
                        + "operating a Pet Shop, the particulars of which are set out below :-";

        addText(document, introduction, NORMAL_FONT);
        addBlank(document);

        addQuestion(
                document,
                "1. Name and address of the Pet Shop :",
                value(dto.getShopName()) + "\n" + ownerAddress);

        addQuestion(
                document,
                "2. Name and address of the Pet Shop Owner :",
                value(dto.getOwnerName()) + "\n" + ownerAddress);

        addQuestion(
                document,
                "3. Telephone Number (Landline / Mobile) :",
                value(dto.getContactMobile()));

        addQuestion(
                document,
                "4. Details of accommodation and infrastructure available at proposed Pet Shop :",
                value(dto.getAccommodationInfrastructure()));

        addQuestion(
                document,
                "5. Working hours and Rest Day :",
                "Working Hours : "
                        + value(dto.getWorkingHours())
                        + "\n"
                        + "Rest Day : "
                        + value(dto.getRestDay()));

        addQuestion(
                document,
                "6. Ventilation Arrangement :",
                value(dto.getVentilationArrangement()));

        addQuestion(
                document,
                "7. Lighting Arrangement :",
                value(dto.getLightingArrangement()));

        addQuestion(
                document,
                "8. Smoke Detection and Fire Fighting Arrangement :",
                value(dto.getFireSafetyArrangement()));

        addQuestion(
                document,
                "9. Heating / Cooling Arrangement and maintenance of comfortable temperature :",
                value(dto.getHeatingCoolingArrangement()));

        addQuestion(
                document,
                "10. Power back-up arrangement :",
                value(dto.getPowerBackupArrangement()));

        addQuestion(
                document,
                "11. Arrangements for food storage :",
                value(dto.getFoodStorageArrangement()));

        addQuestion(
                document,
                "12. Cleanliness, how proposed to be maintained, and arrangements for removal of animal excreta and waste :",
                value(dto.getCleanlinessWasteArrangement()));

        addQuestion(
                document,
                "13. Arrangement for disposal of animals that die :",
                value(dto.getDeadAnimalDisposalArrangement()));

        addQuestion(
                document,
                "14. Arrangement for medical and veterinary support :",
                value(dto.getVeterinarySupportArrangement()));

        Paragraph title = new Paragraph(
                "15. Details of Pet Animals proposed to be displayed or housed in the Pet Shop for sale",
                BOLD_FONT);
        title.setSpacingBefore(8);
        title.setSpacingAfter(8);
        document.add(title);

        addAnimalTable(document, dto.getAnimals());
        addBlank(document);

        addQuestion(
                document,
                "16. Details of cheque / Demand Draft / Online payment :",
                "Online Payment");

        Paragraph bankTitle = new Paragraph("Bank Details", BOLD_FONT);
        bankTitle.setSpacingBefore(10);
        document.add(bankTitle);

        addText(
                document,
                "Name of the Bank : Kerala Gramin Bank\n"
                        + "Account Number : 40341111002087\n"
                        + "IFSC Code : KLGB0040341\n"
                        + "Branch : Main Branch, Trivandrum GPO",
                NORMAL_FONT);

        addBlank(document);

        Paragraph declarationTitle = new Paragraph("DECLARATION", BOLD_FONT);
        declarationTitle.setSpacingBefore(10);
        document.add(declarationTitle);

        addText(
                document,
                "I/We do hereby declare that the information provided herein is accurate and true.",
                NORMAL_FONT);
        addBlank(document);

        addText(document, "Place : " + value(dto.getDeclarationPlace()), NORMAL_FONT);
        addText(
                document,
                "Date : "
                        + value(
                                dto.getDeclarationDate() == null
                                        ? "-"
                                        : dto.getDeclarationDate().toString()),
                NORMAL_FONT);
        addBlank(document);

        addSignatureBlock(document, dto, applicationId);

        document.newPage();

        Paragraph documentTitle = new Paragraph("SUPPORTING DOCUMENTS", TITLE_FONT);
        documentTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(documentTitle);
        addBlank(document);

        addSupportingDocumentsTable(document, dto.getSupportingDocuments());

        document.newPage();

        addCenter(document, "AFFIDAVIT", TITLE_FONT);
        addBlank(document);

        Paragraph affidavit = new Paragraph(
                "I "
                        + value(dto.getAffidavitDeponentName())
                        + " residing at "
                        + ownerAddress
                        + " do hereby solemnly affirm and state as follows :-",
                NORMAL_FONT);
        affidavit.setSpacingAfter(15);
        document.add(affidavit);

        addText(
                document,
                "1. I do hereby follow the Prevention of Cruelty to Animals (Pet Shop) Rules, 2018.",
                NORMAL_FONT);
        addText(
                document,
                "2. I do hereby abide by all the rules laid down from time to time by Animal Welfare Board of India.",
                NORMAL_FONT);
        addText(
                document,
                "3. I do hereby undertake to fulfil all the conditions in the Pet Shop Registration Rules.",
                NORMAL_FONT);
        addText(
                document,
                "4. I do hereby accept cancellation of my registration certificate in case of misconduct / irregularities.",
                NORMAL_FONT);
        addText(
                document,
                "5. I do hereby declare that this affidavit is true and correct to the best of my knowledge and belief.",
                NORMAL_FONT);

        addBlank(document);
        addBlank(document);
        addText(document, "Solemnly affirmed and signed", NORMAL_FONT);
        addBlank(document);

        Paragraph deponent = new Paragraph("Deponent", BOLD_FONT);
        deponent.setAlignment(Element.ALIGN_RIGHT);
        document.add(deponent);
        addBlank(document);

        String declarationDate =
                dto.getDeclarationDate() == null
                        ? "__________"
                        : dto.getDeclarationDate().toString();

        addText(
                document,
                "This day "
                        + declarationDate
                        + " at "
                        + value(dto.getDeclarationPlace())
                        + " before me.",
                NORMAL_FONT);

        document.close();
        return out.toByteArray();
    }

    private void addAnimalTable(
            Document document,
            List<PetShopProposedAnimalDto> animals) throws Exception {

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {2.5f, 2.5f, 1.2f, 2.0f, 2.0f, 3.5f});

        table.addCell(headerCell("Species"));
        table.addCell(headerCell("Breed"));
        table.addCell(headerCell("Qty"));
        table.addCell(headerCell("Age"));
        table.addCell(headerCell("Price"));
        table.addCell(headerCell("Description"));

        if (animals != null && !animals.isEmpty()) {
            for (PetShopProposedAnimalDto animal : animals) {
                table.addCell(normalCell(speciesName(animal)));
                String breedDisplay = (animal.getCustomBreedName() != null && !animal.getCustomBreedName().isBlank())
                        ? ("Other".equalsIgnoreCase(animal.getBreed()) ? animal.getBreed() + " - " + animal.getCustomBreedName().trim() : animal.getCustomBreedName().trim())
                        : value(animal.getBreed());
                table.addCell(normalCell(breedDisplay));
                table.addCell(normalCell(String.valueOf(animal.getQuantity())));
                table.addCell(normalCell(value(animal.getAgeDescription())));
                table.addCell(normalCell(value(animal.getPriceOffered())));
                table.addCell(normalCell(value(animal.getDescription())));
            }
        } else {
            PdfPCell cell = new PdfPCell(
                    new Paragraph("No animals added", NORMAL_FONT));
            cell.setColspan(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        document.add(table);
    }

    private void addSupportingDocumentsTable(
            Document document,
            List<PetShopApplicationDocumentDto> documents) throws Exception {

        PdfPTable documentTable = new PdfPTable(3);
        documentTable.setWidthPercentage(100);
        documentTable.setWidths(new float[] {1, 4, 5});

        documentTable.addCell(headerCell("Sl No"));
        documentTable.addCell(headerCell("Document Type"));
        documentTable.addCell(headerCell("File Name"));

        if (documents != null && !documents.isEmpty()) {
            int index = 1;
            for (PetShopApplicationDocumentDto doc : documents) {
                documentTable.addCell(normalCell(String.valueOf(index++)));
                documentTable.addCell(normalCell(doc.getDocumentTypeName()));
                documentTable.addCell(normalCell(doc.getFileName()));
            }
        } else {
            PdfPCell cell = new PdfPCell(
                    new Paragraph("No Supporting Documents Uploaded", NORMAL_FONT));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            documentTable.addCell(cell);
        }

        document.add(documentTable);
    }

    private void addSignatureBlock(
            Document document,
            PetShopRegistrationViewDto dto,
            Long applicationId) throws Exception {

        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);
        signTable.setWidths(new float[] {3, 2});
        signTable.setKeepTogether(true);

        PdfPCell leftCell = new PdfPCell(new Paragraph("", BOLD_FONT));
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        leftCell.setPaddingTop(20);
        signTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Optional<Path> signaturePath = findSignatureFilePath(dto, applicationId);
        if (signaturePath.isPresent() && Files.exists(signaturePath.get())) {
            Image image = Image.getInstance(signaturePath.get().toAbsolutePath().toString());
            image.scaleToFit(140, 60);
            rightCell.addElement(image);
        } else {
            rightCell.addElement(
                    new Paragraph(value(dto.getOwnerName()), NORMAL_FONT));
        }

        signTable.addCell(rightCell);
        document.add(signTable);
    }

    private Optional<Path> findSignatureFilePath(
            PetShopRegistrationViewDto dto,
            Long applicationId) {

        Optional<PetShopApplicationDocumentDto> fromDto =
                findSignatureDocument(dto.getSupportingDocuments());
        if (fromDto.isPresent()) {
            return Optional.of(resolveDocumentPath(fromDto.get().getFilePath()));
        }

        for (String code : SIGNATURE_TYPE_CODES) {
            Optional<PetShopApplicationDocument> document =
                    documentRepository.findFirstByApplication_IdAndDocumentType_Code(
                            applicationId,
                            code);
            if (document.isPresent()
                    && document.get().getFilePath() != null
                    && !document.get().getFilePath().isBlank()) {
                Path resolved = resolveDocumentPath(document.get().getFilePath());
                if (Files.exists(resolved)) {
                    return Optional.of(resolved);
                }
            }
        }

        return Optional.empty();
    }

    private Optional<PetShopApplicationDocumentDto> findSignatureDocument(
            List<PetShopApplicationDocumentDto> documents) {

        if (documents == null || documents.isEmpty()) {
            return Optional.empty();
        }

        for (PetShopApplicationDocumentDto document : documents) {
            String typeName = value(document.getDocumentTypeName()).toLowerCase(Locale.ROOT);
            String mimeType = value(document.getMimeType()).toLowerCase(Locale.ROOT);
            if (typeName.contains("signature")
                    || mimeType.startsWith("image/")) {
                return Optional.of(document);
            }
        }

        return Optional.empty();
    }

    private Path resolveDocumentPath(String relativeFilePath) {
        Path existing = resolveExistingDocumentPath(relativeFilePath);
        if (existing != null) {
            return existing;
        }

        String normalizedPath = relativeFilePath.replace("\\", "/");
        if (normalizedPath.startsWith("/")) {
            return Paths.get(normalizedPath).normalize();
        }

        return getUploadRootPath().resolve(normalizedPath).normalize();
    }

    /**
     * Resolves a stored relative/absolute document path against known upload roots.
     *
     * @param relativeFilePath path saved in DB
     * @return readable path if found, otherwise null
     */
    private Path resolveExistingDocumentPath(String relativeFilePath) {
        if (relativeFilePath == null || relativeFilePath.isBlank()) {
            return null;
        }

        String normalizedPath = relativeFilePath.replace("\\", "/");

        if (normalizedPath.startsWith("/")) {
            Path absolute = Paths.get(normalizedPath).normalize();
            if (Files.exists(absolute) && Files.isReadable(absolute)) {
                return absolute;
            }
        }

        for (Path root : getCandidateUploadRoots()) {
            Path candidate = root.resolve(normalizedPath).normalize();
            if (Files.exists(candidate) && Files.isReadable(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    private Path getUploadRootPath() {
        List<Path> roots = getCandidateUploadRoots();
        return roots.isEmpty()
                ? Paths.get(
                        System.getProperty("user.home"),
                        "Documents",
                        "uploads",
                        "documents")
                : roots.get(0);
    }

    private List<Path> getCandidateUploadRoots() {
        List<Path> roots = new java.util.ArrayList<>();

        // Same location used by PetShopApplicationDocumentServiceImpl.uploadDocument
        roots.add(Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "uploads",
                "documents"));

        if (configuredUploadRoot != null && !configuredUploadRoot.isBlank()) {
            roots.add(Paths.get(configuredUploadRoot).normalize());
        }

        if (uploadDir != null && !uploadDir.isBlank()) {
            roots.add(Paths.get(uploadDir).toAbsolutePath().normalize());
        }

        return roots.stream().distinct().toList();
    }

    private String buildAttachmentEntryName(
            PetShopApplicationDocument document,
            int index,
            Set<String> usedNames) {

        String typePrefix = document.getDocumentType() != null
                && document.getDocumentType().getName() != null
                ? sanitizeFileName(document.getDocumentType().getName())
                : "document";

        String originalName = document.getFileName() != null
                && !document.getFileName().isBlank()
                ? document.getFileName()
                : Paths.get(document.getFilePath()).getFileName().toString();

        String entryName = typePrefix + "_" + index + "_" + sanitizeFileName(originalName);
        String key = entryName.toLowerCase(Locale.ROOT);
        int duplicate = 1;

        while (usedNames.contains(key)) {
            entryName = typePrefix + "_" + index + "_" + duplicate + "_"
                    + sanitizeFileName(originalName);
            key = entryName.toLowerCase(Locale.ROOT);
            duplicate++;
        }

        usedNames.add(key);
        return entryName;
    }

    private String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "file";
        }
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    public String formatAddress(PetShopRegistrationViewDto dto) {
        return value(dto.getAddressLine1())
                + ", "
                + value(dto.getAddressLine2())
                + ", "
                + value(dto.getCity())
                + " - "
                + value(dto.getPincode());
    }

    private String speciesName(PetShopProposedAnimalDto animal) {
        if (animal == null) {
            return "-";
        }

        DropdownPayload<Long> species = animal.getSpecies();
        String baseName = species != null ? value(species.getName()) : "-";

        if (animal.getCustomSpeciesName() != null && !animal.getCustomSpeciesName().isBlank()) {
            return "Other".equalsIgnoreCase(baseName)
                    ? baseName + " - " + animal.getCustomSpeciesName().trim()
                    : animal.getCustomSpeciesName().trim();
        }

        return baseName;
    }

    private void addCenter(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(5);
        document.add(paragraph);
    }

    private void addText(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingAfter(6);
        document.add(paragraph);
    }

    private void addBlank(Document document) throws Exception {
        document.add(new Paragraph(" "));
    }

    private void addQuestion(
            Document document,
            String question,
            String answer) throws Exception {

        Paragraph questionParagraph = new Paragraph(question, BOLD_FONT);
        questionParagraph.setSpacingBefore(4);
        questionParagraph.setSpacingAfter(2);
        document.add(questionParagraph);

        Paragraph answerParagraph = new Paragraph(value(answer), NORMAL_FONT);
        answerParagraph.setSpacingAfter(8);
        document.add(answerParagraph);
    }

    private String value(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private String value(BigDecimal value) {
        return value == null ? "-" : value.toPlainString();
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, BOLD_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell normalCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(value(text), NORMAL_FONT));
        cell.setPadding(5);
        return cell;
    }
    private Path resolveFilePath(String filePath) {

        if (filePath == null || filePath.trim().isEmpty() || "-".equals(filePath)) {
            return null;
        }

        String normalizedPath = filePath.replace("\\", "/");

        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        Path directPath = Paths.get(normalizedPath);

        if (Files.exists(directPath)) {
            return directPath;
        }

        Path uploadPath = Paths.get(uploadDir);

        String uploadFolderName = uploadPath.getFileName() != null
                ? uploadPath.getFileName().toString()
                : "";

        if (!uploadFolderName.isEmpty()
                && normalizedPath.startsWith(uploadFolderName + "/")) {

            String strippedPath =
                    normalizedPath.substring(uploadFolderName.length() + 1);

            return uploadPath.resolve(strippedPath).normalize();
        }

        return uploadPath.resolve(normalizedPath).normalize();
    }
}
