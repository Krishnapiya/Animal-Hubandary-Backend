package com.keltron.petshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.io.File;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import com.lowagie.text.Image;

import com.keltron.petshop.entity.PetShopApplicationDocument;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.dto.PetShopProposedAnimalDto;
import com.keltron.petshop.dto.PetShopRegistrationViewDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
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

    @Autowired
    private PetShopRegistrationApplicationServiceImpl applicationService;
    @Autowired
    private PetShopApplicationDocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadApplication(Long id)
            throws Exception {

        PetShopRegistrationViewDto dto =
                applicationService.getApplication(id);

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        Document document =
                new Document(PageSize.A4, 40, 40, 40, 40);

        PdfWriter.getInstance(document, out);

        document.open();

        /* ==========================================================
         * PAGE 1
         * ==========================================================
         */

        addCenter(document,
                "KERALA STATE ANIMAL WELFARE BOARD",
                TITLE_FONT);

        addCenter(document,
                "THE FIRST SCHEDULE",
                SUBTITLE_FONT);

        addCenter(document,
                "FORM - I",
                SUBTITLE_FONT);

        addCenter(document,
                "(See rules 4(2) and 5)",
                NORMAL_FONT);

        addCenter(document,
                "APPLICATION FORM FOR REGISTRATION / RENEWAL",
                BOLD_FONT);

        addBlank(document);

        /* ---------------------------------------------------------- */

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

        /* ---------------------------------------------------------- */

        String ownerAddress =
                value(dto.getAddressLine1())
                        + ", "
                        + value(dto.getAddressLine2())
                        + ", "
                        + value(dto.getCity())
                        + " - "
                        + value(dto.getPincode());

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

        addText(document,
                introduction,
                NORMAL_FONT);

        addBlank(document);

        /* ==========================================================
         * QUESTION 1
         * ==========================================================
         */

        addQuestion(
                document,
                "1. Name and address of the Pet Shop :",
                value(dto.getShopName())
                        + "\n"
                        + ownerAddress);

        /* ==========================================================
         * QUESTION 2
         * ==========================================================
         */

        addQuestion(
                document,
                "2. Name and address of the Pet Shop Owner :",
                value(dto.getOwnerName())
                        + "\n"
                        + ownerAddress);

        /* ==========================================================
         * QUESTION 3
         * ==========================================================
         */

        addQuestion(
                document,
                "3. Telephone Number (Landline / Mobile) :",
                value(dto.getContactMobile()));

        /* ==========================================================
         * QUESTION 4
         * ==========================================================
         */

        addQuestion(
                document,
                "4. Details of accommodation and infrastructure available at proposed Pet Shop :",
                value(dto.getAccommodationInfrastructure()));

        /* ==========================================================
         * QUESTION 5
         * ==========================================================
         */

        String workingHours =
                "Working Hours : "
                        + value(dto.getWorkingHours())
                        + "\n"
                        + "Rest Day : "
                        + value(dto.getRestDay());

        addQuestion(
                document,
                "5. Working hours and Rest Day :",
                workingHours);

        /* ==========================================================
         * QUESTION 6
         * ==========================================================
         */

        addQuestion(
                document,
                "6. Ventilation Arrangement :",
                value(dto.getVentilationArrangement()));

        /* ==========================================================
         * QUESTION 7
         * ==========================================================
         */

        addQuestion(
                document,
                "7. Lighting Arrangement :",
                value(dto.getLightingArrangement()));

        /* ==========================================================
         * QUESTION 8
         * ==========================================================
         */

        addQuestion(
                document,
                "8. Smoke Detection and Fire Fighting Arrangement :",
                value(dto.getFireSafetyArrangement()));

        /* ==========================================================
         * QUESTION 9
         * ==========================================================
         */


        addQuestion(
                document,
                "9. Heating / Cooling Arrangement and maintenance of comfortable temperature :",
                value(dto.getHeatingCoolingArrangement()));
        /* ==========================================================
         * PAGE 2
         * ==========================================================
         */

        /* ==========================================================
         * QUESTION 10
         * ==========================================================
         */

        addQuestion(
                document,
                "10. Power back-up arrangement :",
                value(dto.getPowerBackupArrangement()));

        /* ==========================================================
         * QUESTION 11
         * ==========================================================
         */

        addQuestion(
                document,
                "11. Arrangements for food storage :",
                value(dto.getFoodStorageArrangement()));

        /* ==========================================================
         * QUESTION 12
         * ==========================================================
         */

        addQuestion(
                document,
                "12. Cleanliness, how proposed to be maintained, and arrangements for removal of animal excreta and waste :",
                value(dto.getCleanlinessWasteArrangement()));

        /* ==========================================================
         * QUESTION 13
         * ==========================================================
         */

        addQuestion(
                document,
                "13. Arrangement for disposal of animals that die :",
                value(dto.getDeadAnimalDisposalArrangement()));

        /* ==========================================================
         * QUESTION 14
         * ==========================================================
         */

        addQuestion(
                document,
                "14. Arrangement for medical and veterinary support :",
                value(dto.getVeterinarySupportArrangement()));

        /* ==========================================================
         * QUESTION 15
         * ==========================================================
         */

        Paragraph title =
                new Paragraph(
                        "15. Details of Pet Animals proposed to be displayed or housed in the Pet Shop for sale",
                        BOLD_FONT);

        title.setSpacingBefore(8);
        title.setSpacingAfter(8);

        document.add(title);

        /* ==========================================================
         * ANIMAL TABLE
         * ==========================================================
         */

        PdfPTable table = new PdfPTable(6);

        table.setWidthPercentage(100);

        table.setWidths(new float[] {
                2.5f,
                2.5f,
                1.2f,
                2.0f,
                2.0f,
                3.5f
        });

        /* ---------- HEADER ---------- */

        table.addCell(headerCell("Species"));
        table.addCell(headerCell("Breed"));
        table.addCell(headerCell("Qty"));
        table.addCell(headerCell("Age"));
        table.addCell(headerCell("Price"));
        table.addCell(headerCell("Description"));

        /* ---------- DATA ---------- */

        List<PetShopProposedAnimalDto> animals =
                dto.getAnimals();

        if (animals != null && !animals.isEmpty()) {

            for (PetShopProposedAnimalDto animal : animals) {

                table.addCell(normalCell(
                        animal.getSpecies() != null
                                ? animal.getSpecies().getName()
                                : "-"));

                table.addCell(normalCell(
                        value(animal.getBreed())));

                table.addCell(normalCell(
                        String.valueOf(animal.getQuantity())));

                table.addCell(normalCell(
                        value(animal.getAgeDescription())));

                table.addCell(normalCell(
                        value(animal.getPriceOffered())));

                table.addCell(normalCell(
                        value(animal.getDescription())));
            }

        } else {

            PdfPCell cell =
                    new PdfPCell(
                            new Paragraph(
                                    "No animals added",
                                    NORMAL_FONT));

            cell.setColspan(6);

            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);

            cell.setPadding(8);

            table.addCell(cell);
        }

        document.add(table);

        addBlank(document);

        /* ==========================================================
         * QUESTION 16
         * ==========================================================
         */

        addQuestion(
                document,
                "16. Details of cheque / Demand Draft / Online payment :",
                "Online Payment");

        /* ==========================================================
         * BANK DETAILS
         * ==========================================================
         */

        Paragraph bankTitle =
                new Paragraph(
                        "Bank Details",
                        BOLD_FONT);

        bankTitle.setSpacingBefore(10);

        document.add(bankTitle);

        addText(
                document,
                "Name of the Bank : Kerala Gramin Bank\n"
                        + "Account Number : 40341111002087\n"
                        + "IFSC Code : KLGB0040341\n"
                        + "Branch : Main Branch, Trivandrum GPO",
                NORMAL_FONT);
        /* ==========================================================
         * DECLARATION
         * ==========================================================
         */

        addBlank(document);

        Paragraph declarationTitle =
                new Paragraph(
                        "DECLARATION",
                        BOLD_FONT);

        declarationTitle.setSpacingBefore(10);

        document.add(declarationTitle);

        addText(
                document,
                "I/We do hereby declare that the information provided herein is accurate and true.",
                NORMAL_FONT);

        addBlank(document);

        addText(
                document,
                "Place : " + value(dto.getDeclarationPlace()),
                NORMAL_FONT);

        addText(
                document,
                "Date : " + value(
                        dto.getDeclarationDate() == null
                                ? "-"
                                : dto.getDeclarationDate().toString()),
                NORMAL_FONT);

        addBlank(document);

        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);
        signTable.setWidths(new float[] {3, 2});
        signTable.setKeepTogether(true);

        // Left side
        PdfPCell leftCell = new PdfPCell(
                new Paragraph("", BOLD_FONT));
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        leftCell.setPaddingTop(20);
        signTable.addCell(leftCell);

        // Right side
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Optional<PetShopApplicationDocument> signatureDoc =
                documentRepository.findFirstByApplication_IdAndDocumentType_Code(
                        id,
                        "APPLICANT_SIGNATURE");

        if (signatureDoc.isPresent()) {

            String uploadRoot =
                    System.getProperty("user.home")
                    + File.separator
                    + "Documents"
                    + File.separator
                    + "uploads"
                    + File.separator
                    + "documents";

            String fullPath =
                    uploadRoot
                    + File.separator
                    + signatureDoc.get().getFilePath();

            File imageFile = new File(fullPath);

            if (imageFile.exists()) {

                Image image = Image.getInstance(fullPath);

                image.scaleToFit(140, 60);

                rightCell.addElement(image);
            }
        }

        signTable.addCell(rightCell);

        document.add(signTable);
        /* ==========================================================
         * SUPPORTING DOCUMENTS
         * ==========================================================
         */

        document.newPage();

        Paragraph documentTitle =
                new Paragraph(
                        "SUPPORTING DOCUMENTS",
                        TITLE_FONT);

        documentTitle.setAlignment(Element.ALIGN_CENTER);

        document.add(documentTitle);

        addBlank(document);

        PdfPTable documentTable =
                new PdfPTable(3);

        documentTable.setWidthPercentage(100);

        documentTable.setWidths(new float[] {
                1,
                4,
                5
        });

        documentTable.addCell(headerCell("Sl No"));
        documentTable.addCell(headerCell("Document Type"));
        documentTable.addCell(headerCell("File Name"));

        List<PetShopApplicationDocumentDto> documents =
                dto.getSupportingDocuments();

        if (documents != null && !documents.isEmpty()) {

            int index = 1;

            for (PetShopApplicationDocumentDto doc : documents) {

                documentTable.addCell(
                        normalCell(String.valueOf(index++)));

                documentTable.addCell(
                        normalCell(doc.getDocumentTypeName()));

                documentTable.addCell(
                        normalCell(doc.getFileName()));
            }

        } else {

            PdfPCell cell =
                    new PdfPCell(
                            new Paragraph(
                                    "No Supporting Documents Uploaded",
                                    NORMAL_FONT));

            cell.setColspan(3);

            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);

            cell.setPadding(8);

            documentTable.addCell(cell);
        }

        document.add(documentTable);

        /* ==========================================================
         * AFFIDAVIT
         * ==========================================================
         */

        document.newPage();

        addCenter(
                document,
                "AFFIDAVIT",
                TITLE_FONT);

        addBlank(document);

        String ownerAddress1 =
                value(dto.getAddressLine1())
                        + ", "
                        + value(dto.getAddressLine2())
                        + ", "
                        + value(dto.getCity())
                        + " - "
                        + value(dto.getPincode());

        Paragraph affidavit =
                new Paragraph(
                        "I "
                                + value(dto.getAffidavitDeponentName())
                                + " residing at "
                                + ownerAddress1
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

        addText(
                document,
                "Solemnly affirmed and signed",
                NORMAL_FONT);

        addBlank(document);

        Paragraph deponent =
                new Paragraph(
                        "Deponent",
                        BOLD_FONT);

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
        document.newPage();

        document.close();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename("PetShopApplication.pdf")
                        .build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    /* ==========================================================
                        HELPER METHODS
       ========================================================== */

    private void addCenter(Document document,
            String text,
            Font font) throws Exception {

        Paragraph p =
                new Paragraph(text, font);

        p.setAlignment(Element.ALIGN_CENTER);

        p.setSpacingAfter(5);

        document.add(p);
    }

    private void addText(Document document,
            String text,
            Font font) throws Exception {

        Paragraph p =
                new Paragraph(text, font);

        p.setSpacingAfter(6);

        document.add(p);
    }

    private void addBlank(Document document)
            throws Exception {

        document.add(new Paragraph(" "));
    }

    private void addQuestion(Document document,
            String question,
            String answer)
            throws Exception {

        Paragraph q =
                new Paragraph(question, BOLD_FONT);

        q.setSpacingBefore(4);
        q.setSpacingAfter(2);

        document.add(q);

        Paragraph a =
                new Paragraph(value(answer),
                        NORMAL_FONT);

        a.setSpacingAfter(8);

        document.add(a);
    }

    private String value(String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }

    private String value(BigDecimal value) {

        return value == null
                ? "-"
                : value.toPlainString();
    }
    private PdfPCell headerCell(String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Paragraph(text, BOLD_FONT));

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);

        return cell;
    }

    private PdfPCell normalCell(String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Paragraph(value(text), NORMAL_FONT));

        cell.setPadding(5);

        return cell;
    }

}
