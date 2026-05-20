package pl.edu.payroll.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import pl.edu.payroll.entity.Payslip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class PdfService {

    public byte[] generatePayslipPdf(Payslip p) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        // Nagłówek firmy
        doc.add(new Paragraph(p.getEmployee().getCompany().getName())
            .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        String companyInfo = "NIP: " + nvl(p.getEmployee().getCompany().getNip())
            + "   REGON: " + nvl(p.getEmployee().getCompany().getRegon());
        doc.add(new Paragraph(companyInfo)
            .setFont(regular).setFontSize(10).setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("\n"));

        // Tytuł
        String title = String.format("PASEK PŁACOWY – %02d/%d", p.getPeriodMonth(), p.getPeriodYear());
        doc.add(new Paragraph(title)
            .setFont(bold).setFontSize(13).setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("\n"));

        // Dane pracownika
        doc.add(new Paragraph("Pracownik: " + p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName())
            .setFont(regular).setFontSize(11));
        if (p.getEmployee().getPesel() != null) {
            doc.add(new Paragraph("PESEL: " + p.getEmployee().getPesel())
                .setFont(regular).setFontSize(11));
        }
        doc.add(new Paragraph("Data sporządzenia: " + (p.getDocumentDate() != null ? p.getDocumentDate().toString() : ""))
            .setFont(regular).setFontSize(10));

        doc.add(new Paragraph("\n"));

        // Tabela składników
        Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();

        addHeaderRow(table, "Składnik", "Kwota (PLN)", bold);
        addRow(table, "Wynagrodzenie zasadnicze / brutto bazowe", p.getGrossSalary(), regular);
        if (p.getBonus().compareTo(BigDecimal.ZERO) > 0)
            addRow(table, "Premia", p.getBonus(), regular);
        if (p.getAllowances().compareTo(BigDecimal.ZERO) > 0)
            addRow(table, "Dodatki", p.getAllowances(), regular);

        addSeparator(table, "Składki ZUS (pracownik)", bold);
        addRow(table, "Składka emerytalna (9,76%)", p.getPensionContribEmployee(), regular);
        addRow(table, "Składka rentowa (1,50%)", p.getDisabilityContribEmployee(), regular);
        addRow(table, "Składka chorobowa (2,45%)", p.getSicknessContrib(), regular);
        addRow(table, "Składka zdrowotna (9,00%)", p.getHealthContrib(), regular);

        addSeparator(table, "Podatek", bold);
        addRow(table, "Zaliczka na podatek PIT", p.getIncomeTaxAdvance(), regular);

        if (p.getGarnishment().compareTo(BigDecimal.ZERO) > 0)
            addRow(table, "Potrącenie komornicze", p.getGarnishment(), regular);
        if (p.getVoluntaryDeduction().compareTo(BigDecimal.ZERO) > 0)
            addRow(table, "Potrącenie dobrowolne", p.getVoluntaryDeduction(), regular);

        addSeparator(table, "Podsumowanie", bold);
        addRowBold(table, "WYNAGRODZENIE NETTO (do wypłaty)", p.getNetSalary(), bold);
        addRow(table, "Całkowity koszt pracodawcy", p.getEmployerTotalCost(), regular);

        doc.add(table);

        if (p.getSickLeaveDays() > 0) {
            doc.add(new Paragraph("\nDni L4: " + p.getSickLeaveDays())
                .setFont(regular).setFontSize(9));
        }
        if (p.getUnpaidLeaveDays() > 0) {
            doc.add(new Paragraph("Dni urlopu bezpłatnego: " + p.getUnpaidLeaveDays())
                .setFont(regular).setFontSize(9));
        }

        doc.close();
        return out.toByteArray();
    }

    private void addHeaderRow(Table table, String label, String value, PdfFont font) {
        table.addHeaderCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(10))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(10)
            .setTextAlignment(TextAlignment.RIGHT))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY));
    }

    private void addRow(Table table, String label, BigDecimal value, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(10)));
        table.addCell(new Cell().add(new Paragraph(fmt(value)).setFont(font).setFontSize(10)
            .setTextAlignment(TextAlignment.RIGHT)));
    }

    private void addRowBold(Table table, String label, BigDecimal value, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(11))
            .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(220, 237, 220)));
        table.addCell(new Cell().add(new Paragraph(fmt(value)).setFont(font).setFontSize(11)
            .setTextAlignment(TextAlignment.RIGHT))
            .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(220, 237, 220)));
    }

    private void addSeparator(Table table, String label, PdfFont font) {
        table.addCell(new Cell(1, 2).add(new Paragraph(label).setFont(font).setFontSize(10))
            .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(26, 82, 118))
            .setFontColor(ColorConstants.WHITE));
    }

    private String fmt(BigDecimal v) {
        return v != null ? String.format("%,.2f", v) : "0,00";
    }

    private String nvl(String s) {
        return s != null ? s : "-";
    }
}
