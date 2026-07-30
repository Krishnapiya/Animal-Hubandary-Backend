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

    @Autowired
    private DogBreederRegistrationApplicationServiceImpl applicationService;

    @Autowired
    private DogBreederApplicationDocumentRepository documentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${application.document.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Generates the dog breeder registration PDF as raw bytes.
     *
     * @param applicationId registration application id
     * @return PDF content
     */
    @Transactional(readOnly = true)
    public byte[] generateApplicationPdf(Long applicationId) {
        try {
            Map<String, Object> preview = applicationService.getPreview(applicationId);
            return buildApplicationPdf(preview, applicationId);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate dog breeder application PDF",
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
            Map<String, Object> preview = applicationService.getPreview(applicationId);
            byte[] pdfBytes = buildApplicationPdf(preview, applicationId);

            String appNo = value(preview.get("applicationNumber"));
            String pdfFileName = !appNo.equals("-")
                    ? appNo + ".pdf"
                    : "DogBreederApplication-" + applicationId + ".pdf";

            ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(zipOut)) {
                ZipEntry pdfEntry = new ZipEntry(pdfFileName);
                zip.putNextEntry(pdfEntry);
                zip.write(pdfBytes);
                zip.closeEntry();

                List<DogBreederApplicationDocument> documents =
                        documentRepository.findByApplication_Id(applicationId);

                Set<String> usedNames = new HashSet<>();
                usedNames.add(pdfFileName.toLowerCase(Locale.ROOT));

                int index = 1;
                int attachedCount = 0;
                for (DogBreederApplicationDocument document : documents) {
                    if (document.getFilePath() == null
                            || document.getFilePath().isBlank()) {
                        continue;
                    }

                    Path filePath = resolveExistingDocumentPath(document.getFilePath());
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
                    "Failed to generate dog breeder application ZIP",
                    e);
        }
    }

    /**
     * Builds download response with ZIP content and headers.
     *
     * @param id registration application id
     * @return ZIP download response
     * @throws Exception if ZIP generation fails
     */
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadApplication(Long id) throws Exception {
        Map<String, Object> preview = applicationService.getPreview(id);
        byte[] zipBytes = generateApplicationZip(id);

        String appNo = value(preview.get("applicationNumber"));
        String fileName = !appNo.equals("-")
                ? appNo + ".zip"
                : "DogBreederApplication-" + id + ".zip";

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
            Map<String, Object> preview,
            Long applicationId) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(document, out);
        document.open();

        addCenter(document, "KERALA STATE ANIMAL WELFARE BOARD", TITLE_FONT);
        addCenter(document, "APPLICATION FOR REGISTRATION OF DOG BREEDER", SUBTITLE_FONT);
        addCenter(document, "(Under Prevention of Cruelty to Animals Rules)", NORMAL_FONT);
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
                "Subject : Application for grant of licence for Dog Breeding Establishment",
                BOLD_FONT);
        addBlank(document);

        String ownerName = value(preview.get("breederName"));
        String establishmentAddress = formatAddress(preview);

        String introduction =
                "Sir,\n\n"
                        + "I/We "
                        + ownerName
                        + " do hereby apply for registration to operate / continue operating a Dog Breeding Establishment, "
                        + "the particulars of which are set out below :-";

        addText(document, introduction, NORMAL_FONT);
        addBlank(document);

        addQuestion(
                document,
                "1. Name and address of the Breeder / Establishment :",
                ownerName + "\n" + establishmentAddress);

        addQuestion(
                document,
                "2. Contact Mobile Number :",
                value(preview.get("contactMobile")));

        addQuestion(
                document,
                "3. Email Address :",
                value(preview.get("email")));

        addQuestion(
                document,
                "4. Details of Accommodation and Infrastructure :",
                value(preview.get("infrastructureDetails")));

        addQuestion(
                document,
                "5. Veterinary Support Arrangement :",
                value(preview.get("veterinarySupportDetails")));

        addQuestion(
                document,
                "6. Waste Disposal & Sanitation Arrangement :",
                value(preview.get("wasteDisposalDetails")));

        Paragraph title = new Paragraph(
                "7. Details of Breeding Dogs Proposed to be Housed / Bred",
                BOLD_FONT);
        title.setSpacingBefore(8);
        title.setSpacingAfter(8);
        document.add(title);

        List<Map<String, Object>> dogs = asListMap(preview.get("dogs"));
        addDogsTable(document, dogs);
        addBlank(document);

        Paragraph declarationTitle = new Paragraph("DECLARATION", BOLD_FONT);
        declarationTitle.setSpacingBefore(10);
        document.add(declarationTitle);

        addText(
                document,
                "I/We do hereby declare that the information provided herein is accurate and true to the best of my knowledge and belief.",
                NORMAL_FONT);
        addBlank(document);

        addText(document, "Place : " + value(preview.get("declarationPlace")), NORMAL_FONT);
        addText(document, "Date : " + value(preview.get("declarationDate")), NORMAL_FONT);
        addBlank(document);

        addSignatureBlock(document, preview, applicationId);

        document.newPage();

        Paragraph documentTitle = new Paragraph("SUPPORTING DOCUMENTS", TITLE_FONT);
        documentTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(documentTitle);
        addBlank(document);

        List<Map<String, Object>> documents = asListMap(preview.get("supportingDocuments"));
        addSupportingDocumentsTable(document, documents);

        document.close();
        return out.toByteArray();
    }

    private void addDogsTable(
            Document document,
            List<Map<String, Object>> dogs) throws Exception {

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {2.5f, 2.5f, 1.2f, 2.0f, 3.5f});

        table.addCell(headerCell("Breed"));
        table.addCell(headerCell("Microchip No"));
        table.addCell(headerCell("Age"));
        table.addCell(headerCell("Gender"));
        table.addCell(headerCell("Remarks / Health Status"));

        if (dogs != null && !dogs.isEmpty()) {
            for (Map<String, Object> dog : dogs) {
                table.addCell(normalCell(value(dog.get("breed"))));
                table.addCell(normalCell(value(dog.get("microchipNumber"))));
                table.addCell(normalCell(value(dog.get("age"))));
                table.addCell(normalCell(value(dog.get("gender"))));
                table.addCell(normalCell(value(dog.get("remarks"))));
            }
        } else {
            PdfPCell cell = new PdfPCell(
                    new Paragraph("No dogs registered", NORMAL_FONT));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        document.add(table);
    }

    private void addSupportingDocumentsTable(
            Document document,
            List<Map<String, Object>> documents) throws Exception {

        PdfPTable documentTable = new PdfPTable(3);
        documentTable.setWidthPercentage(100);
        documentTable.setWidths(new float[] {1, 4, 5});

        documentTable.addCell(headerCell("Sl No"));
        documentTable.addCell(headerCell("Document Type"));
        documentTable.addCell(headerCell("File Name"));

        if (documents != null && !documents.isEmpty()) {
            int index = 1;
            for (Map<String, Object> doc : documents) {
                documentTable.addCell(normalCell(String.valueOf(index++)));
                documentTable.addCell(normalCell(value(doc.get("documentTypeName"))));
                documentTable.addCell(normalCell(value(doc.get("fileName"))));
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
            Map<String, Object> preview,
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

        Optional<Path> signaturePath = findSignatureFilePath(preview, applicationId);
        if (signaturePath.isPresent() && Files.exists(signaturePath.get())) {
            Image image = Image.getInstance(signaturePath.get().toAbsolutePath().toString());
            image.scaleToFit(140, 60);
            rightCell.addElement(image);
        } else {
            rightCell.addElement(
                    new Paragraph(value(preview.get("breederName")), NORMAL_FONT));
        }

        signTable.addCell(rightCell);
        document.add(signTable);
    }

    private Optional<Path> findSignatureFilePath(
            Map<String, Object> preview,
            Long applicationId) {

        List<Map<String, Object>> docs = asListMap(preview.get("supportingDocuments"));
        if (docs != null) {
            for (Map<String, Object> doc : docs) {
                String typeName = value(doc.get("documentTypeName")).toLowerCase(Locale.ROOT);
                String mimeType = value(doc.get("mimeType")).toLowerCase(Locale.ROOT);
                if (typeName.contains("signature") || mimeType.startsWith("image/")) {
                    String relativePath = value(doc.get("filePath"));
                    if (!relativePath.equals("-")) {
                        Path path = resolveDocumentPath(relativePath);
                        if (Files.exists(path)) {
                            return Optional.of(path);
                        }
                    }
                }
            }
        }

        for (String code : SIGNATURE_TYPE_CODES) {
            Optional<DogBreederApplicationDocument> document =
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
                ? Paths.get(System.getProperty("user.home"), "Documents", "uploads").normalize()
                : roots.get(0);
    }

    private List<Path> getCandidateUploadRoots() {
        List<Path> roots = new ArrayList<>();

        // 1. Primary Base Path: /home/user/Documents/uploads
        roots.add(Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "uploads").normalize());

        // 2. Sub-folder fallback if uploads/documents is used
        roots.add(Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "uploads",
                "documents").normalize());

        // 3. Dynamic spring application upload-dir property fallback
        if (uploadDir != null && !uploadDir.isBlank()) {
            roots.add(Paths.get(uploadDir).toAbsolutePath().normalize());
        }

        // 4. Custom application root variable fallback
        if (configuredUploadRoot != null && !configuredUploadRoot.isBlank()) {
            roots.add(Paths.get(configuredUploadRoot).normalize());
        }

        return roots.stream().distinct().toList();
    }

    private String buildAttachmentEntryName(
            DogBreederApplicationDocument document,
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

    private String formatAddress(Map<String, Object> preview) {
        return value(preview.get("addressLine1"))
                + ", "
                + value(preview.get("addressLine2"))
                + ", "
                + value(preview.get("city"))
                + " - "
                + value(preview.get("pincode"));
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

    private String value(Object obj) {
        if (obj == null) {
            return "-";
        }
        String val = String.valueOf(obj).trim();
        return val.isEmpty() ? "-" : val;
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

    private List<Map<String, Object>> asListMap(Object obj) {
        if (obj == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.convertValue(
                    obj,
                    new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}