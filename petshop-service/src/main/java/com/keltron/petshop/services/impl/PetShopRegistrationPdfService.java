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
                table.addCell(normalCell(value(animal.getBreed())));
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

    private String formatAddress(PetShopRegistrationViewDto dto) {
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
        if (species == null) {
            return "-";
        }

        return value(species.getName());
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
