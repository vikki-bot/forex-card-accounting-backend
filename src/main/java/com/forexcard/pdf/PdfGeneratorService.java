package com.forexcard.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.forexcard.dto.TransactionDTO;
import com.forexcard.model.User;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;
import java.awt.Color;

public class PdfGeneratorService {

    public static byte[] generateTransactionReport(List<TransactionDTO> transactions, User user) {
        try {
            // Initialize Document
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Page numbering
            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        PdfContentByte cb = writer.getDirectContent();
                        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
                        Phrase footer = new Phrase("Page " + document.getPageNumber(), font);
                        float x = (document.right() - document.left()) / 2 + document.leftMargin();
                        float y = document.bottom() - 20;
                        cb.beginText();
                        cb.setFontAndSize(font.getBaseFont(), 10);
                        cb.setTextMatrix(x, y);
                        cb.showText(footer.getContent());
                        cb.endText();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // 1. Logo from URL
            try {
                URL logoUrl = new URL("https://images-platform.99static.com/KtadMm4dc0KOZSK7kU4tVTvOIsE=/99designs-contests-attachments/73/73396/attachment_73396902");
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 120);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception ex) {
                System.err.println("Logo failed to load: " + ex.getMessage());
            }

            // 2. Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(0, 102, 204));
            Paragraph title = new Paragraph("ForexCard Transaction Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 3. User Info Section
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            PdfPTable userInfo = new PdfPTable(2);
            userInfo.setWidthPercentage(60);
            userInfo.setSpacingAfter(20);
            userInfo.setHorizontalAlignment(Element.ALIGN_LEFT);

            userInfo.addCell(new Phrase("Customer Name:", labelFont));
            userInfo.addCell(new Phrase(user.getName(), valueFont));
            userInfo.addCell(new Phrase("Card Number:", labelFont));
            userInfo.addCell(new Phrase(user.getForexCard().getCardNumber(), valueFont));
            userInfo.addCell(new Phrase("PAN Number:", labelFont));
            userInfo.addCell(new Phrase(user.getPan(), valueFont));

            document.add(userInfo);

            // 4. Transaction Table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);
            table.setWidths(new float[]{2f, 3f, 2f, 1.5f, 2f, 2f, 2f});

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Color headerBgColor = new Color(0, 102, 204);
            String[] headers = {"Date", "Merchant", "Amount", "Currency", "Debit", "Status", "Balance"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(headerBgColor);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Data rows
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            boolean alternate = false;
            Color altColor = new Color(224, 235, 255);

            for (TransactionDTO txn : transactions) {
                Color rowColor = alternate ? altColor : Color.WHITE;
                table.addCell(makeCell(txn.getDate().toString(), cellFont, rowColor));
                table.addCell(makeCell(String.valueOf(txn.getMerchant()), cellFont, rowColor));
                table.addCell(makeCell(String.valueOf(txn.getAmount()), cellFont, rowColor));
                table.addCell(makeCell(String.valueOf(txn.getCurrencyName()), cellFont, rowColor));
                table.addCell(makeCell(String.valueOf(txn.getDeductAmount()), cellFont, rowColor));
                table.addCell(makeCell(String.valueOf(txn.getStatus()), cellFont, rowColor));
                table.addCell(makeCell(String.valueOf(txn.getCurrentBalance()), cellFont, rowColor));
                alternate = !alternate;
            }

            document.add(table);

            // 5. Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);
            Paragraph footer = new Paragraph("This is a system-generated report by ForexCard. Please do not reply to this message.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility method for table cells
    private static PdfPCell makeCell(String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setBorder(1);
        return cell;
    }
}
