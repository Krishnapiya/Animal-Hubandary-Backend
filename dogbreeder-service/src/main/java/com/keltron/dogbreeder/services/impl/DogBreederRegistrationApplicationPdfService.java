package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keltron.dogbreeder.entity.DogBreederApplicationDocument;
import com.keltron.dogbreeder.repository.DogBreederApplicationDocumentRepository;
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
public class DogBreederRegistrationApplicationPdfService {

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

    @Value("${dogbreeder.document.upload-root:}")
    private String configuredUploadRoot;

    @Value("${application.document.upload-dir:}")
    private String uploadDir;

    @Autowired
    private DogBreederApplicationDocumentRepository documentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Generates the dog breeder registration PDF as raw bytes.
     */
    @Transactional(readOnly = true)
    public byte[] generateApplicationPdf(Long applicationId, Map<String, Object> applicationData) {
        try {
            return buildApplicationPdf(applicationData, applicationId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate dog breeder application PDF", e);
        }
    }

    /**
     * Builds a ZIP containing the application PDF and all uploaded attachments.
     */
    @Transactional(readOnly = true)
    public byte[] generateApplicationZip(Long applicationId, Map<String, Object> applicationData) {
        try {
            byte[] pdfBytes = buildApplicationPdf(applicationData, applicationId);

            String appNo = value(applicationData.get("applicationNumber"));
            String pdfFileName = !appNo.equals("-")
                    ? appNo + ".pdf"
                    : "DogBreederApplication-" + applicationId + ".pdf";

            ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(zipOut)) {
                // Add Application PDF
                ZipEntry pdfEntry = new ZipEntry(pdfFileName);
                zip.putNextEntry(pdfEntry);
                zip.write(pdfBytes);
                zip.closeEntry();

                // Fetch attached documents from DB
                List<DogBreederApplicationDocument> documents =
                        documentRepository.findByApplication_Id(applicationId);

                Set<String> usedNames = new HashSet<>();
                usedNames.add(pdfFileName.toLowerCase(Locale.ROOT));

                int index = 1;
                int attachedCount = 0;
                for (DogBreederApplicationDocument document : documents) {
                    if (document.getFilePath() == null || document.getFilePath().isBlank()) {
                        continue;
                    }

                    Path filePath = resolveExistingDocumentPath(document.getFilePath());
                    if (filePath == null) {
                        System.out.println("ZIP skip missing attachment: " + document.getFilePath());
                        continue;
                    }

                    String entryName = buildAttachmentEntryName(document, index++, usedNames);

                    ZipEntry attachmentEntry = new ZipEntry("attachments/" + entryName);
                    zip.putNextEntry(attachmentEntry);
                    zip.write(Files.readAllBytes(filePath));
                    zip.closeEntry();
                    attachedCount++;
                }

                System.out.println("ZIP attachments included = " + attachedCount + " for applicationId = " + applicationId);
            }

            return zipOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate dog breeder application ZIP", e);
        }
    }

    /**
     * Builds download response with ZIP content containing PDF + attachments.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadApplication(Long applicationId, Map<String, Object> applicationData) throws Exception {
        byte[] zipBytes = generateApplicationZip(applicationId, applicationData);

        String appNo = value(applicationData.get("applicationNumber"));
        String fileName = !appNo.equals("-")
                ? appNo + ".zip"
                : "DogBreederApplication-" + applicationId + ".zip";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fileName)
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipBytes);
    }

    private byte[] buildApplicationPdf(Map<String, Object> data, Long applicationId) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        PdfWriter writer = PdfWriter.getInstance(document, out);
        // Optional: Attach watermark event handler here if available
        // writer.setPageEvent(new WatermarkPageEvent());

        document.open();

        // Header Section
        addCenter(document, "KERALA STATE ANIMAL WELFARE BOARD", TITLE_FONT);
        addCenter(document, "THE FIRST SCHEDULE", SUBTITLE_FONT);
        addCenter(document, "FORM - I", SUBTITLE_FONT);
        addCenter(document, "(See rules 4(2) and 5)", NORMAL_FONT);
        addCenter(document, "APPLICATION FORM FOR DOG BREEDER REGISTRATION / RENEWAL", BOLD_FONT);
        addBlank(document);

        // Address Section
        addText(document, "To,", NORMAL_FONT);
        addText(document,
                "Member Secretary / Convenor\n"
                        + "Kerala State Animal Welfare Board\n"
                        + "O/o Director, Animal Husbandry Department\n"
                        + "Vikas Bhavan, 6th Floor\n"
                        + "Thiruvananthapuram - 695033\n"
                        + "Email : sawbkerala@gmail.com",
                NORMAL_FONT);
        addBlank(document);

        addText(document, "Subject : Application for Grant / Renewal of Dog Breeder Licence", BOLD_FONT);
        addBlank(document);

        String ownerName = value(data.get("breederName"));
        String address = formatAddress(data);

        String introduction = "Sir,\n\n"
                + "I/We " + ownerName + " r/o " + address
                + " with office address " + address
                + " do hereby apply for registration to operate / continue operating a Dog Breeding Establishment, the particulars of which are set out below :-";

        addText(document, introduction, NORMAL_FONT);
        addBlank(document);

        // Questionnaires
        addQuestion(document, "1. Name and address of the Dog Breeding Establishment :", value(data.get("establishmentName")) + "\n" + address);
        addQuestion(document, "2. Name and address of the Breeder / Owner :", ownerName + "\n" + address);
        addQuestion(document, "3. Telephone Number (Mobile / Landline) :", value(data.get("contactMobile")));
        addQuestion(document, "4. Infrastructure and Accommodation Details :", value(data.get("infrastructureDetails")));
        addQuestion(document, "5. Veterinary Support Arrangement :", value(data.get("veterinarySupportDetails")));
        addQuestion(document, "6. Sanitation, Waste & Excreta Disposal Arrangements :", value(data.get("sanitationWasteDetails")));

        // Dog Details Table
        Paragraph title = new Paragraph("7. Details of Dogs proposed to be bred / housed in the Establishment", BOLD_FONT);
        title.setSpacingBefore(8);
        title.setSpacingAfter(8);
        document.add(title);

        List<Map<String, Object>> dogsList = extractList(data.get("dogs"));
        addDogTable(document, dogsList);
        addBlank(document);

        // Payment Info
        addQuestion(document, "8. Details of Payment :", "Online Payment");

        Paragraph bankTitle = new Paragraph("Bank Details", BOLD_FONT);
        bankTitle.setSpacingBefore(10);
        document.add(bankTitle);

        addText(document,
                "Name of the Bank : Kerala Gramin Bank\n"
                        + "Account Number : 40341111002087\n"
                        + "IFSC Code : KLGB0040341\n"
                        + "Branch : Main Branch, Trivandrum GPO",
                NORMAL_FONT);
        addBlank(document);

        // Declaration
        Paragraph declarationTitle = new Paragraph("DECLARATION", BOLD_FONT);
        declarationTitle.setSpacingBefore(10);
        document.add(declarationTitle);

        addText(document, "I/We do hereby declare that the information provided herein is accurate and true.", NORMAL_FONT);
        addBlank(document);

        addText(document, "Place : " + value(data.get("declarationPlace")), NORMAL_FONT);
        addText(document, "Date : " + value(data.get("declarationDate")), NORMAL_FONT);
        addBlank(document);

        addSignatureBlock(document, data, applicationId);

        // Page 2: Supporting Documents
        document.newPage();

        Paragraph documentTitle = new Paragraph("SUPPORTING DOCUMENTS", TITLE_FONT);
        documentTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(documentTitle);
        addBlank(document);

        List<Map<String, Object>> docsList = extractList(data.get("supportingDocuments"));
        addSupportingDocumentsTable(document, docsList);

        // Page 3: Affidavit
        document.newPage();

        addCenter(document, "AFFIDAVIT", TITLE_FONT);
        addBlank(document);

        Paragraph affidavit = new Paragraph(
                "I " + value(data.get("affidavitDeponentName"))
                        + " residing at " + address
                        + " do hereby solemnly affirm and state as follows :-",
                NORMAL_FONT);
        affidavit.setSpacingAfter(15);
        document.add(affidavit);

        addText(document, "1. I do hereby follow the Prevention of Cruelty to Animals (Dog Breeding and Marketing) Rules, 2017.", NORMAL_FONT);
        addText(document, "2. I do hereby abide by all the rules laid down from time to time by Animal Welfare Board of India.", NORMAL_FONT);
        addText(document, "3. I do hereby undertake to fulfil all conditions mentioned in the registration rules.", NORMAL_FONT);
        addText(document, "4. I do hereby accept cancellation of my registration certificate in case of misconduct / irregularities.", NORMAL_FONT);
        addText(document, "5. I do hereby declare that this affidavit is true and correct to the best of my knowledge and belief.", NORMAL_FONT);

        addBlank(document);
        addBlank(document);
        addText(document, "Solemnly affirmed and signed", NORMAL_FONT);
        addBlank(document);

        Paragraph deponent = new Paragraph("Deponent", BOLD_FONT);
        deponent.setAlignment(Element.ALIGN_RIGHT);
        document.add(deponent);
        addBlank(document);

        addText(document,
                "This day " + value(data.get("declarationDate"))
                        + " at " + value(data.get("declarationPlace"))
                        + " before me.",
                NORMAL_FONT);

        document.close();
        return out.toByteArray();
    }

    private void addDogTable(Document document, List<Map<String, Object>> dogs) throws Exception {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {2.5f, 2.5f, 1.5f, 2.0f, 3.5f});

        table.addCell(headerCell("Breed"));
        table.addCell(headerCell("Gender"));
        table.addCell(headerCell("Microchip No"));
        table.addCell(headerCell("Age"));
        table.addCell(headerCell("Remarks"));

        if (dogs != null && !dogs.isEmpty()) {
            for (Map<String, Object> dog : dogs) {
                table.addCell(normalCell(value(dog.get("breed"))));
                table.addCell(normalCell(value(dog.get("gender"))));
                table.addCell(normalCell(value(dog.get("microchipNumber"))));
                table.addCell(normalCell(value(dog.get("age"))));
                table.addCell(normalCell(value(dog.get("remarks"))));
            }
        } else {
            PdfPCell cell = new PdfPCell(new Paragraph("No dogs registered", NORMAL_FONT));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        document.add(table);
    }

    private void addSupportingDocumentsTable(Document document, List<Map<String, Object>> docs) throws Exception {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {1, 4, 5});

        table.addCell(headerCell("Sl No"));
        table.addCell(headerCell("Document Type"));
        table.addCell(headerCell("File Name"));

        if (docs != null && !docs.isEmpty()) {
            int index = 1;
            for (Map<String, Object> doc : docs) {
                table.addCell(normalCell(String.valueOf(index++)));
                table.addCell(normalCell(value(doc.get("documentTypeName"))));
                table.addCell(normalCell(value(doc.get("fileName"))));
            }
        } else {
            PdfPCell cell = new PdfPCell(new Paragraph("No Supporting Documents Uploaded", NORMAL_FONT));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        document.add(table);
    }

    private void addSignatureBlock(Document document, Map<String, Object> data, Long applicationId) throws Exception {
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

        Optional<Path> signaturePath = findSignatureFilePath(data, applicationId);
        if (signaturePath.isPresent() && Files.exists(signaturePath.get())) {
            Image image = Image.getInstance(signaturePath.get().toAbsolutePath().toString());
            image.scaleToFit(140, 60);
            rightCell.addElement(image);
        } else {
            rightCell.addElement(new Paragraph(value(data.get("breederName")), NORMAL_FONT));
        }

        signTable.addCell(rightCell);
        document.add(signTable);
    }

    private Optional<Path> findSignatureFilePath(Map<String, Object> data, Long applicationId) {
        List<Map<String, Object>> docs = extractList(data.get("supportingDocuments"));
        if (docs != null) {
            for (Map<String, Object> doc : docs) {
                String typeName = value(doc.get("documentTypeName")).toLowerCase(Locale.ROOT);
                String mimeType = value(doc.get("mimeType")).toLowerCase(Locale.ROOT);

                if (typeName.contains("signature") || mimeType.startsWith("image/")) {
                    String filePath = value(doc.get("filePath"));
                    if (!filePath.equals("-")) {
                        return Optional.ofNullable(resolveExistingDocumentPath(filePath));
                    }
                }
            }
        }

        for (String code : SIGNATURE_TYPE_CODES) {
            Optional<DogBreederApplicationDocument> document =
                    documentRepository.findFirstByApplication_IdAndDocumentType_Code(applicationId, code);

            if (document.isPresent() && document.get().getFilePath() != null && !document.get().getFilePath().isBlank()) {
                Path resolved = resolveExistingDocumentPath(document.get().getFilePath());
                if (resolved != null && Files.exists(resolved)) {
                    return Optional.of(resolved);
                }
            }
        }

        return Optional.empty();
    }

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

    private List<Path> getCandidateUploadRoots() {
        List<Path> roots = new ArrayList<>();

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

    private String buildAttachmentEntryName(DogBreederApplicationDocument document, int index, Set<String> usedNames) {
        String typePrefix = document.getDocumentType() != null && document.getDocumentType().getName() != null
                ? sanitizeFileName(document.getDocumentType().getName())
                : "document";

        String originalName = document.getFileName() != null && !document.getFileName().isBlank()
                ? document.getFileName()
                : Paths.get(document.getFilePath()).getFileName().toString();

        String entryName = typePrefix + "_" + index + "_" + sanitizeFileName(originalName);
        String key = entryName.toLowerCase(Locale.ROOT);
        int duplicate = 1;

        while (usedNames.contains(key)) {
            entryName = typePrefix + "_" + index + "_" + duplicate + "_" + sanitizeFileName(originalName);
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

    private String formatAddress(Map<String, Object> data) {
        return value(data.get("addressLine1")) + ", "
                + value(data.get("addressLine2")) + ", "
                + value(data.get("city")) + " - "
                + value(data.get("pincode"));
    }

    private List<Map<String, Object>> extractList(Object obj) {
        if (obj == null) return new ArrayList<>();
        try {
            return objectMapper.convertValue(obj, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
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

    private void addQuestion(Document document, String question, String answer) throws Exception {
        Paragraph questionParagraph = new Paragraph(question, BOLD_FONT);
        questionParagraph.setSpacingBefore(4);
        questionParagraph.setSpacingAfter(2);
        document.add(questionParagraph);

        Paragraph answerParagraph = new Paragraph(value(answer), NORMAL_FONT);
        answerParagraph.setSpacingAfter(8);
        document.add(answerParagraph);
    }

    private String value(Object obj) {
        if (obj == null) return "-";
        String str = obj.toString().trim();
        return str.isEmpty() ? "-" : str;
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
}