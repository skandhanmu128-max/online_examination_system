package com.examiq.onlineexam.service;

import com.examiq.onlineexam.dto.AttemptResultDto;
import com.examiq.onlineexam.dto.QuestionResultDto;
import com.examiq.onlineexam.dto.OptionDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfReportService {

    public byte[] generateAttemptReportPdf(AttemptResultDto result) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Colors
            Color primaryColor = new Color(30, 41, 59);   // Slate 800
            Color accentColor = new Color(79, 70, 229);   // Indigo 600
            Color passColor = new Color(16, 185, 129);    // Emerald 500
            Color failColor = new Color(239, 68, 68);     // Red 500

            // Header Title
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, accentColor);
            Paragraph header = new Paragraph("ExamiQ - Official Scorecard", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
            Paragraph subHeader = new Paragraph("Automated Verification & Evaluation Report", subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(20);
            document.add(subHeader);

            // Student & Exam Metadata Table
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setSpacingAfter(15);

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryColor);
            Font valFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            addMetaRow(metaTable, "Student Name:", result.getStudentName(), labelFont, valFont);
            addMetaRow(metaTable, "Email Address:", result.getStudentEmail(), labelFont, valFont);
            addMetaRow(metaTable, "Exam Title:", result.getExamTitle(), labelFont, valFont);
            addMetaRow(metaTable, "Subject:", result.getSubject(), labelFont, valFont);
            addMetaRow(metaTable, "Attempt ID:", "#EXAM-" + result.getAttemptId(), labelFont, valFont);
            addMetaRow(metaTable, "Submission Status:", result.getStatus().name(), labelFont, valFont);

            document.add(metaTable);

            // Score Summary Box
            PdfPTable scoreTable = new PdfPTable(4);
            scoreTable.setWidthPercentage(100);
            scoreTable.setSpacingAfter(20);

            Color scoreBg = result.isPassed() ? new Color(236, 253, 245) : new Color(254, 242, 242);
            Color statusTextColor = result.isPassed() ? passColor : failColor;

            addScoreBox(scoreTable, "Score Obtained", String.format("%.1f / %.1f", result.getScoreObtained(), result.getTotalMarks()), accentColor, scoreBg);
            addScoreBox(scoreTable, "Percentage", String.format("%.2f%%", result.getPercentage()), accentColor, scoreBg);
            addScoreBox(scoreTable, "Result Status", result.isPassed() ? "PASSED" : "FAILED", statusTextColor, scoreBg);
            addScoreBox(scoreTable, "Anti-Cheat Violations", String.valueOf(result.getViolationCount()), primaryColor, scoreBg);

            document.add(scoreTable);

            // Question Breakdown Title
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
            Paragraph sectionTitle = new Paragraph("Detailed Question Breakdown", sectionTitleFont);
            sectionTitle.setSpacingAfter(10);
            document.add(sectionTitle);

            // Questions List
            int qIndex = 1;
            for (QuestionResultDto q : result.getQuestionResults()) {
                PdfPTable qTable = new PdfPTable(1);
                qTable.setWidthPercentage(100);
                qTable.setSpacingAfter(10);

                Color qHeaderBg = q.isCorrect() ? new Color(240, 253, 244) : new Color(255, 241, 242);
                Font qFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryColor);

                PdfPCell qCell = new PdfPCell(new Phrase("Q" + qIndex + ". " + q.getQuestionText() + " (" + q.getMarksAwarded() + "/" + q.getMarks() + " Marks)", qFont));
                qCell.setBackgroundColor(qHeaderBg);
                qCell.setPadding(8);
                qTable.addCell(qCell);

                StringBuilder optionsText = new StringBuilder();
                if (q.getOptions() != null) {
                    for (OptionDto opt : q.getOptions()) {
                        boolean isSelected = q.getSelectedOptionIds() != null && q.getSelectedOptionIds().contains(opt.getId());
                        String mark = isSelected ? "[X] " : "[ ] ";
                        String correctMark = opt.isCorrect() ? " (Correct Answer)" : "";
                        optionsText.append(mark).append(opt.getOptionText()).append(correctMark).append("\n");
                    }
                }

                Font optFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
                PdfPCell optCell = new PdfPCell(new Phrase(optionsText.toString(), optFont));
                optCell.setPadding(6);
                qTable.addCell(optCell);

                if (q.getExplanation() != null && !q.getExplanation().trim().isEmpty()) {
                    Font expFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, accentColor);
                    PdfPCell expCell = new PdfPCell(new Phrase("Explanation: " + q.getExplanation(), expFont));
                    expCell.setBackgroundColor(new Color(248, 250, 252));
                    expCell.setPadding(6);
                    qTable.addCell(expCell);
                }

                document.add(qTable);
                qIndex++;
            }

            // Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Paragraph footer = new Paragraph("Generated by ExamiQ Examination Platform on " + java.time.LocalDateTime.now().toString(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addMetaRow(PdfPTable table, String label, String value, Font labelFont, Font valFont) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, labelFont));
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPadding(4);
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value, valFont));
        vCell.setBorder(Rectangle.NO_BORDER);
        vCell.setPadding(4);
        table.addCell(vCell);
    }

    private void addScoreBox(PdfPTable table, String title, String value, Color textColor, Color bgColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(10);

        Font tFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        Font vFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, textColor);

        Paragraph p1 = new Paragraph(title, tFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph(value, vFont);
        p2.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(p1);
        cell.addElement(p2);
        table.addCell(cell);
    }
}
