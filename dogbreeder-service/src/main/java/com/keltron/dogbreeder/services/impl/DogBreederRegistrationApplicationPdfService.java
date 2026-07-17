package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.lowagie.text.pdf.PdfWriter;

@Service
public class DogBreederRegistrationApplicationPdfService {

    private static final Font TITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);

    private static final Font BOLD_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    private static final Font NORMAL_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 10);

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
    public byte[] generateApplicationPdf(Long applicationId) {

        try {
            Map<String, Object> preview = applicationService.getPreview(applicationId);

            Map<String, Object> breeder = asMap(preview.get("breederDetails"));
            Map<String, Object> facility = asMap(preview.get("facilityDetails"));
            Map<String, Object> declaration = asMap(preview.get("declarationDetails"));
            List<Map<String, Object>> breeds = asMapList(preview.get("breedDetails"));
            List<Map<String, Object>> documents = asMapList(preview.get("documentDetails"));

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 40, 40, 35, 35);
            PdfWriter.getInstance(document, out);

            document.open();

            addCenter(document, "KERALA STATE ANIMAL WELFARE BOARD", TITLE_FONT);
            addCenter(document, "THE FIRST SCHEDULE", BOLD_FONT);
            addCenter(document, "[rules 4(2) and 5(1)] FORM - I", NORMAL_FONT);
            addCenter(document,
                    "APPLICATION FOR REGISTRATION OF BREEDER IN RESPECT OF AN ESTABLISHMENT",
                    BOLD_FONT);

            addBlank(document);

            addText(document, "To,", NORMAL_FONT);
            addText(document,
                    "Member Secretary/Convenor Kerala State Animal Welfare Board,\n"
                            + "Directorate of Animal Husbandry, 6th Floor, Vikas Bhavan,\n"
                            + "Thiruvananthapuram-33\n"
                            + "email: sawbkerala@gmail.com",
                    NORMAL_FONT);

            addBlank(document);

            addText(document,
                    "Subject: Application for registration of breeder in respect of an establishment",
                    BOLD_FONT);

            addBlank(document);

            addText(document,
                    "Sir, I/We " + value(breeder, "breederName")
                            + " r/o " + address(breeder)
                            + " with office address " + address(breeder)
                            + " do hereby apply for a registration as breeder in respect of the establishment "
                            + "in accordance with the particulars set out below :-",
                    NORMAL_FONT);

            addBlank(document);

            addQuestion(document, "1) Name and address of the applicant (breeder):",
                    value(breeder, "breederName") + "\n" + address(breeder));

            addQuestion(document, "2) Name and address of the establishment:",
                    value(breeder, "facilityDetails") + "\n" + address(breeder));

            addQuestion(document, "3) Telephone number:",
                    value(breeder, "contactMobile"));

            addQuestion(document, "4) Details of accommodation and infrastructure available at proposed establishment:",
                    value(facility, "accommodationInfrastructure"));

            addQuestion(document, "5) Working hours and rest day, i.e. day on which establishment shall remain closed:",
                    "Working Hours: " + value(facility, "workingHours")
                            + "\nRest Day: " + value(facility, "restDay"));

            addQuestion(document, "6) Ventilation arrangement:",
                    value(facility, "ventilationArrangement"));

            addQuestion(document, "7) Lighting arrangement:",
                    value(facility, "lightingArrangement"));

            addQuestion(document, "8) Heating or cooling arrangement, and manner in which comfortable temperature will be maintained for all pet animals:",
                    value(facility, "heatingCoolingArrangement"));

            addQuestion(document, "9) Arrangements for food storage:",
                    value(facility, "foodStorageArrangement"));

            addQuestion(document, "10) Cleanliness, how proposed to be maintained, and arrangements for removal of animal excreta and waste:",
                    value(facility, "cleanlinessWasteArrangement"));

            addQuestion(document, "11) Arrangement for disposal of animal that die:",
                    value(facility, "deadAnimalDisposalArrangement"));

            addQuestion(document, "12) Arrangement for medical and veterinary support:",
                    value(facility, "veterinarySupportArrangement"));

            addQuestion(document, "13) Details of dogs proposed to be bred in the establishment:",
                    "Total Dogs Count: " + value(breeder, "totalDogsCount"));

            addQuestion(document, "a) Breeds and number of dogs of each breed:",
                    breedNames(breeds));

            addQuestion(document, "b) Age of each dog:",
                    breedAges(breeds));

            addQuestion(document, "c) Accommodation and number and size of cages and enclosures:",
                    value(facility, "cageEnclosureDetails"));

            addQuestion(document, "14) Qualification and experience of the applicant (breeder) in respect of breeding:",
                    value(declaration, "qualificationExperience"));

            addQuestion(document, "15) Details of cheque or demand draft number for payment of fee:",
                    "-");

            addBlank(document);

            addText(document,
                    "I/We do hereby declare that the information provided by us is accurate and true.",
                    NORMAL_FONT);

            addBlank(document);

            addText(document,
                    "Place: " + value(declaration, "declarationPlace")
                            + "\nDate: " + value(declaration, "declarationDate"),
                    NORMAL_FONT);

            addSignature(document, declaration, documents);

            addBlank(document);

            addText(document, "Bank Details:", BOLD_FONT);
            addText(document,
                    "Name of the Bank: Kerala Gramin Bank,\n"
                            + "Account Number: 40341111002087\n"
                            + "IFSC Code: KLGB0040341\n"
                            + "Name of Branch: Main Branch, Trivandrum GPO (PO)\n"
                            + "Thiruvananthapuram Dist, 695001",
                    NORMAL_FONT);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate dog breeder PDF", e);
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
            byte[] pdfBytes = generateApplicationPdf(applicationId);

            String pdfFileName =
                    "dog-breeder-application-" + applicationId + ".pdf";

            ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(zipOut)) {
                ZipEntry pdfEntry = new ZipEntry(pdfFileName);
                zip.putNextEntry(pdfEntry);
                zip.write(pdfBytes);
                zip.closeEntry();

                List<DogBreederApplicationDocument> documents =
                        documentRepository.findByApplication_IdOrderByIdAsc(
                                applicationId);

                Set<String> usedNames = new HashSet<>();
                usedNames.add(pdfFileName.toLowerCase(Locale.ROOT));

                int index = 1;
                for (DogBreederApplicationDocument document : documents) {
                    if (document.getFilePath() == null
                            || document.getFilePath().isBlank()) {
                        continue;
                    }

                    Path filePath = resolveStoredFilePath(document.getFilePath());
                    if (filePath == null
                            || !Files.exists(filePath)
                            || !Files.isReadable(filePath)) {
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
                }
            }

            return zipOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate dog breeder application ZIP",
                    e);
        }
    }

    private Path resolveStoredFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty() || "-".equals(filePath)) {
            return null;
        }

        String normalizedPath = filePath.replace("\\", "/");
        Path savedPath = Paths.get(normalizedPath).normalize();

        if (savedPath.isAbsolute()) {
            return savedPath;
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
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

        String entryName = typePrefix + "_" + index + "_"
                + sanitizeFileName(originalName);
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

    private void addSignature(
            Document document,
            Map<String, Object> declaration,
            List<Map<String, Object>> documents
    ) throws Exception {

        Paragraph label = new Paragraph("Signature of Applicant:", BOLD_FONT);
        label.setSpacingBefore(8);
        label.setSpacingAfter(4);
        document.add(label);

        Map<String, Object> signatureDocument = findSignatureDocument(documents);

        if (signatureDocument == null) {
            addText(document, value(declaration, "signatureName"), NORMAL_FONT);
            return;
        }

        String filePath = value(signatureDocument, "filePath");
        String mimeType = value(signatureDocument, "mimeType");

        if (!mimeType.toLowerCase().contains("image")) {
            addText(document, value(declaration, "signatureName"), NORMAL_FONT);
            return;
        }

        Path path = resolveFilePath(filePath);

        if (path == null || !Files.exists(path)) {
            addText(document, value(declaration, "signatureName"), NORMAL_FONT);
            return;
        }

        Image signatureImage = Image.getInstance(path.toAbsolutePath().toString());
        signatureImage.scaleToFit(140, 60);
        signatureImage.setAlignment(Element.ALIGN_LEFT);

        document.add(signatureImage);
    }

    private Map<String, Object> findSignatureDocument(List<Map<String, Object>> documents) {
        if (documents == null || documents.isEmpty()) {
            return null;
        }

        for (Map<String, Object> document : documents) {
            String documentTypeCode = value(document, "documentTypeCode");
            String documentTypeName = value(document, "documentTypeName");
            String documentName = value(document, "documentName");
            String name = value(document, "name");
            String code = value(document, "code");

            String searchText = (
                    documentTypeCode + " " +
                    documentTypeName + " " +
                    documentName + " " +
                    name + " " +
                    code
            ).toLowerCase();

            if (searchText.contains("applicant_signature")
                    || searchText.contains("signature")) {
                return document;
            }
        }

        return null;
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
            String strippedPath = normalizedPath.substring(uploadFolderName.length() + 1);
            return uploadPath.resolve(strippedPath).normalize();
        }

        return uploadPath.resolve(normalizedPath).normalize();
    }

    private void addCenter(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(4);
        document.add(paragraph);
    }

    private void addText(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingAfter(6);
        document.add(paragraph);
    }

    private void addQuestion(Document document, String question, String answer) throws Exception {
        Paragraph q = new Paragraph(question, BOLD_FONT);
        q.setSpacingBefore(5);
        q.setSpacingAfter(2);
        document.add(q);

        Paragraph a = new Paragraph(clean(answer), NORMAL_FONT);
        a.setSpacingAfter(6);
        document.add(a);
    }

    private void addBlank(Document document) throws Exception {
        document.add(new Paragraph(" "));
    }

    private Map<String, Object> asMap(Object value) {
        if (value == null) {
            return new HashMap<>();
        }

        return objectMapper.convertValue(
                value,
                new TypeReference<Map<String, Object>>() {}
        );
    }

    private List<Map<String, Object>> asMapList(Object value) {
        if (value == null) {
            return List.of();
        }

        return objectMapper.convertValue(
                value,
                new TypeReference<List<Map<String, Object>>>() {}
        );
    }

    private String value(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return "-";
        }

        return String.valueOf(map.get(key));
    }

    private String address(Map<String, Object> breeder) {
        String address = "";

        address += line(value(breeder, "addressLine1"));
        address += line(value(breeder, "addressLine2"));
        address += line(value(breeder, "city"));
        address += line(value(breeder, "pincode"));

        return address.trim().isEmpty() ? "-" : address.trim();
    }

    private String line(String value) {
        if (value == null || value.equals("-") || value.trim().isEmpty()) {
            return "";
        }

        return value + "\n";
    }

    private String breedNames(List<Map<String, Object>> breeds) {
        if (breeds == null || breeds.isEmpty()) {
            return "-";
        }

        StringBuilder builder = new StringBuilder();

        for (Map<String, Object> breed : breeds) {
            builder.append("Breed: ")
                    .append(value(breed, "breedName"))
                    .append(", Count: ")
                    .append(value(breed, "dogCount"))
                    .append("\n");
        }

        return builder.toString();
    }

    private String breedAges(List<Map<String, Object>> breeds) {
        if (breeds == null || breeds.isEmpty()) {
            return "-";
        }

        StringBuilder builder = new StringBuilder();

        for (Map<String, Object> breed : breeds) {
            builder.append("Breed: ")
                    .append(value(breed, "breedName"))
                    .append(", Age: ")
                    .append(value(breed, "ageDescription"))
                    .append("\n");
        }

        return builder.toString();
    }

    private String clean(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return "-";
        }

        return value;
    }
}