import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable, Image
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        if self._pageNumber == 1:
            return  # Skip header/footer on title page
        
        self.saveState()
        self.setFont("Helvetica", 9)
        self.setFillColor(colors.HexColor("#64748b"))

        # Header
        self.drawString(54, 11 * 72 - 36, "ExamiQ: Secure Online Examination & Evaluation Platform")
        self.setStrokeColor(colors.HexColor("#cbd5e1"))
        self.setLineWidth(0.5)
        self.line(54, 11 * 72 - 42, 8.5 * 72 - 54, 11 * 72 - 42)

        # Footer
        footer_text = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(8.5 * 72 - 54, 36, footer_text)
        self.drawString(54, 36, "CONFIDENTIAL & PROPRIETARY - EXAMIQ PROJECT REPORT")
        self.line(54, 48, 8.5 * 72 - 54, 48)

        self.restoreState()

def create_report(filename="ExamiQ_Project_Report.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=26,
        leading=32,
        textColor=colors.HexColor("#1e1b4b"),
        alignment=0
    )

    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=13,
        leading=17,
        textColor=colors.HexColor("#4f46e5"),
        spaceAfter=25
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=16,
        leading=20,
        textColor=colors.HexColor("#1e293b"),
        spaceBefore=16,
        spaceAfter=10,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=15,
        textColor=colors.HexColor("#4338ca"),
        spaceBefore=12,
        spaceAfter=6,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=colors.HexColor("#334155"),
        spaceAfter=8
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=body_style,
        leftIndent=15,
        firstLineIndent=-10,
        spaceAfter=4
    )

    code_style = ParagraphStyle(
        'Code_Custom',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor("#0f172a"),
        backColor=colors.HexColor("#f8fafc"),
        borderColor=colors.HexColor("#cbd5e1"),
        borderWidth=0.5,
        borderPadding=6,
        spaceBefore=6,
        spaceAfter=8
    )

    img_caption_style = ParagraphStyle(
        'ImgCaption',
        parent=styles['Normal'],
        fontName='Helvetica-Oblique',
        fontSize=8.5,
        leading=11,
        textColor=colors.HexColor("#64748b"),
        alignment=1,
        spaceBefore=4,
        spaceAfter=12
    )

    story = []

    # -------------------------------------------------------------------------
    # COVER / TITLE PAGE
    # -------------------------------------------------------------------------
    story.append(Spacer(1, 30))
    story.append(Paragraph("PROJECT DOCUMENTATION REPORT", ParagraphStyle('CoverSub', fontName='Helvetica-Bold', fontSize=11, textColor=colors.HexColor("#6366f1"), leading=13)))
    story.append(Spacer(1, 8))
    story.append(Paragraph("ExamiQ - Secure Online Examination & Evaluation Platform", title_style))
    story.append(HRFlowable(width="100%", thickness=4, color=colors.HexColor("#6366f1"), spaceBefore=12, spaceAfter=12))
    story.append(Paragraph("A Modern Java Enterprise System with Proctored Testing, Real-Time Timer Control, Instant Auto-Evaluation, UI Screenshots, Source Code, and PDF Scorecards.", subtitle_style))

    story.append(Spacer(1, 60))

    meta_data = [
        [Paragraph("<b>Project Name:</b>", body_style), Paragraph("ExamiQ Online Examination Platform", body_style)],
        [Paragraph("<b>Architecture:</b>", body_style), Paragraph("Java 21 / Spring Boot 3 / JPA / H2 / OpenPDF", body_style)],
        [Paragraph("<b>Primary Modules:</b>", body_style), Paragraph("Student Auth, Question Bank, Timer Engine, Anti-Cheat, Auto-Evaluation, PDF Export, Admin Dashboard", body_style)],
        [Paragraph("<b>Deliverables Included:</b>", body_style), Paragraph("Java Source Code, Runnable JAR, System Screenshots, PDF & DOCX Reports", body_style)],
        [Paragraph("<b>Status:</b>", body_style), Paragraph("<font color='#10b981'><b>COMPLETED & FULLY VERIFIED</b></font>", body_style)],
        [Paragraph("<b>Date:</b>", body_style), Paragraph("September 2026", body_style)],
    ]
    meta_table = Table(meta_data, colWidths=[130, 370])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#f8fafc")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#e2e8f0")),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(meta_table)

    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # 1. EXECUTIVE SUMMARY & OVERVIEW
    # -------------------------------------------------------------------------
    story.append(Paragraph("1. Executive Summary & Project Overview", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#cbd5e1"), spaceBefore=2, spaceAfter=10))
    
    story.append(Paragraph(
        "<b>ExamiQ</b> is a full-featured, secure online examination platform engineered to streamline test creation, candidate authentication, proctored exam delivery, real-time timer control, automated evaluation, and comprehensive result reporting. Designed with modern Java enterprise best practices, ExamiQ provides educational institutions, universities, and corporate certification bodies with an end-to-end web testing platform.",
        body_style
    ))

    story.append(Paragraph("Key Capabilities Delivered:", h2_style))
    story.append(Paragraph("• <b>Role-Based Access Control (RBAC):</b> Dedicated portals for Students and Administrators with secure BCrypt password hashing and session authorization.", bullet_style))
    story.append(Paragraph("• <b>Flexible Question Bank Management:</b> Supports Single Choice (MCQ), Multiple Choice, and True/False questions with positive marks, negative marking rules, and explanations.", bullet_style))
    story.append(Paragraph("• <b>Timer-Based Exam Engine:</b> Synchronized real-time server/client countdown clock with automatic test submission upon timer expiration.", bullet_style))
    story.append(Paragraph("• <b>Anti-Cheat Proctored Control:</b> Live tab-switching and window-focus detector issuing warnings and auto-terminating tests upon repeated security violations.", bullet_style))
    story.append(Paragraph("• <b>Automated Evaluation Engine:</b> Instant score calculation, percentage computing, grade assignment, and pass/fail status determination.", bullet_style))
    story.append(Paragraph("• <b>PDF Scorecard Generation:</b> Server-side dynamic PDF report creation using OpenPDF for printable, official student scorecards.", bullet_style))
    story.append(Paragraph("• <b>Admin Command Center:</b> Interactive dashboard featuring real-time statistics, candidate attempt tables, and Chart.js analytical performance charts.", bullet_style))

    story.append(Spacer(1, 10))

    # -------------------------------------------------------------------------
    # 2. SYSTEM ARCHITECTURE & TECH STACK
    # -------------------------------------------------------------------------
    story.append(Paragraph("2. Technical Architecture & Tech Stack", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#cbd5e1"), spaceBefore=2, spaceAfter=10))

    tech_data = [
        [Paragraph("<b>Component</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8.5, textColor=colors.white)),
         Paragraph("<b>Technology / Library</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8.5, textColor=colors.white)),
         Paragraph("<b>Description</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8.5, textColor=colors.white))],
        [Paragraph("Runtime Environment", body_style), Paragraph("Java 21 / OpenJDK 25", body_style), Paragraph("Modern Java platform featuring high concurrency and enhanced memory safety.", body_style)],
        [Paragraph("Backend Framework", body_style), Paragraph("Spring Boot 3.2.3", body_style), Paragraph("Spring Web REST controllers, Spring Security filter chain, Spring Data JPA.", body_style)],
        [Paragraph("Database Layer", body_style), Paragraph("H2 Database Engine", body_style), Paragraph("Embedded lightweight relational database with persistence and Web Console.", body_style)],
        [Paragraph("Report Generation", body_style), Paragraph("OpenPDF 1.3.39", body_style), Paragraph("Java library for generating structured, styled PDF scorecards on demand.", body_style)],
        [Paragraph("Frontend UI", body_style), Paragraph("HTML5 / Glassmorphic CSS / JS", body_style), Paragraph("Single Page Application (SPA) architecture with Glassmorphism UX and Chart.js.", body_style)],
        [Paragraph("Build Tool", body_style), Paragraph("Apache Maven 3.9.9", body_style), Paragraph("Standardized dependency management and automated build packaging.", body_style)],
    ]
    tech_table = Table(tech_data, colWidths=[110, 140, 250])
    tech_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1e293b")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#cbd5e1")),
        ('PADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(tech_table)

    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # 3. APPLICATION SCREENSHOTS & USER INTERFACE
    # -------------------------------------------------------------------------
    story.append(Paragraph("3. Application Screenshots & UI Interface", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#cbd5e1"), spaceBefore=2, spaceAfter=10))

    if os.path.exists("screenshot_login.png"):
        story.append(Paragraph("Figure 3.1: Student & Administrator Glassmorphism Login Interface", h2_style))
        story.append(Image("screenshot_login.png", width=480, height=270))
        story.append(Paragraph("ExamiQ Unified Login Portal with RBAC role switcher and demo credential toggles.", img_caption_style))

    if os.path.exists("screenshot_student_portal.png"):
        story.append(Paragraph("Figure 3.2: Student Examination Portal & Attempt Records", h2_style))
        story.append(Image("screenshot_student_portal.png", width=480, height=270))
        story.append(Paragraph("Student Portal showing available exams grid, time duration badges, and previous attempt history.", img_caption_style))

    story.append(PageBreak())

    if os.path.exists("screenshot_exam_workspace.png"):
        story.append(Paragraph("Figure 3.3: Proctored Exam Workspace with Live Timer & Question Palette", h2_style))
        story.append(Image("screenshot_exam_workspace.png", width=480, height=270))
        story.append(Paragraph("Exam Workspace showcasing live countdown clock, option selection, and interactive color-coded question palette.", img_caption_style))

    if os.path.exists("screenshot_anticheat_alert.png"):
        story.append(Paragraph("Figure 3.4: Anti-Cheat Security Violation Alert Overlay", h2_style))
        story.append(Image("screenshot_anticheat_alert.png", width=480, height=270))
        story.append(Paragraph("Proctored focus detection modal triggered when student switches browser tabs or loses window focus.", img_caption_style))

    story.append(PageBreak())

    if os.path.exists("screenshot_scorecard.png"):
        story.append(Paragraph("Figure 3.5: Automated Scorecard & Detailed Answer Review", h2_style))
        story.append(Image("screenshot_scorecard.png", width=480, height=270))
        story.append(Paragraph("Instant result evaluation view displaying percentage badge, score summary, explanation boxes, and PDF export button.", img_caption_style))

    if os.path.exists("screenshot_admin_dashboard.png"):
        story.append(Paragraph("Figure 3.6: Administrator Control Center & Chart.js Metrics", h2_style))
        story.append(Image("screenshot_admin_dashboard.png", width=480, height=270))
        story.append(Paragraph("Admin Dashboard featuring real-time class metrics, Chart.js performance visualization, exam creator, and question bank manager.", img_caption_style))

    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # 4. SOURCE CODE SNIPPETS & IMPLEMENTATION
    # -------------------------------------------------------------------------
    story.append(Paragraph("4. Core Source Code Snippets", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#cbd5e1"), spaceBefore=2, spaceAfter=10))

    story.append(Paragraph("4.1 Auto-Evaluation Engine (ExamService.java)", h2_style))
    code_eval = """// Automated Evaluation Algorithm taking positive & negative marks into account
private AttemptResultDto evaluateAndFinalizeAttempt(ExamAttempt attempt, AttemptStatus targetStatus) {
    Exam exam = attempt.getExam();
    List<Question> questions = questionRepository.findByExamId(exam.getId());
    List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attempt.getId());
    Map<Long, StudentAnswer> answerMap = answers.stream()
            .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), ans -> ans));

    double scoreObtained = 0.0;
    for (Question q : questions) {
        StudentAnswer sa = answerMap.get(q.getId());
        Set<Long> correctOptionIds = q.getOptions().stream()
                .filter(QuestionOption::isCorrect)
                .map(QuestionOption::getId).collect(Collectors.toSet());

        if (sa == null || sa.getSelectedOptionIds() == null || sa.getSelectedOptionIds().isEmpty()) {
            continue;
        }

        if (sa.getSelectedOptionIds().equals(correctOptionIds)) {
            scoreObtained += q.getMarks();
            sa.setMarksAwarded(q.getMarks());
            sa.setCorrect(true);
        } else {
            double penalty = exam.isEnableNegativeMarking() ? exam.getNegativeMarksPerQuestion() : 0.0;
            scoreObtained -= penalty;
            sa.setMarksAwarded(-penalty);
            sa.setCorrect(false);
        }
        studentAnswerRepository.save(sa);
    }
    attempt.setScoreObtained(Math.max(0.0, scoreObtained));
    attempt.setSubmitTime(LocalDateTime.now());
    attempt.setPassed(scoreObtained >= exam.getPassMarks());
    attempt.setStatus(targetStatus);
    return getAttemptResult(attemptRepository.save(attempt).getId());
}"""
    story.append(Paragraph(code_eval.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace(" ", "&nbsp;"), code_style))

    story.append(Paragraph("4.2 Anti-Cheat Security Violation Tracker (ExamService.java)", h2_style))
    code_anticheat = """@Transactional
public void logViolation(Long attemptId) {
    ExamAttempt attempt = attemptRepository.findById(attemptId)
            .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
    attempt.setViolationCount(attempt.getViolationCount() + 1);
    
    // Auto submit if violation threshold (3 violations) reached
    if (attempt.getViolationCount() >= 3 && attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
        attempt.setStatus(AttemptStatus.FLAGGED_VIOLATION);
        evaluateAndFinalizeAttempt(attempt, AttemptStatus.FLAGGED_VIOLATION);
    } else {
        attemptRepository.save(attempt);
    }
}"""
    story.append(Paragraph(code_anticheat.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace(" ", "&nbsp;"), code_style))

    story.append(Paragraph("4.3 Server-Side OpenPDF Scorecard Generator (PdfReportService.java)", h2_style))
    code_pdf = """public byte[] generateAttemptReportPdf(AttemptResultDto result) {
    Document document = new Document(PageSize.A4, 36, 36, 36, 36);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(79, 70, 229));
        document.add(new Paragraph("ExamiQ - Official Scorecard", headerFont));

        PdfPTable scoreTable = new PdfPTable(4);
        scoreTable.setWidthPercentage(100);
        addScoreBox(scoreTable, "Score Obtained", String.format("%.1f / %.1f", result.getScoreObtained(), result.getTotalMarks()));
        addScoreBox(scoreTable, "Percentage", String.format("%.2f%%", result.getPercentage()));
        addScoreBox(scoreTable, "Result Status", result.isPassed() ? "PASSED" : "FAILED");
        document.add(scoreTable);
        document.close();
    } catch (Exception e) { e.printStackTrace(); }
    return out.toByteArray();
}"""
    story.append(Paragraph(code_pdf.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace(" ", "&nbsp;"), code_style))

    story.append(Spacer(1, 15))

    # -------------------------------------------------------------------------
    # 5. USER MANUAL & EXECUTION GUIDE
    # -------------------------------------------------------------------------
    story.append(Paragraph("5. Installation & Execution Guide", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#cbd5e1"), spaceBefore=2, spaceAfter=10))

    story.append(Paragraph("Building the Executable JAR:", h2_style))
    story.append(Paragraph("<code>.\\maven\\apache-maven-3.9.9\\bin\\mvn.cmd clean package -DskipTests</code>", code_style))

    story.append(Paragraph("Running the Server:", h2_style))
    story.append(Paragraph("<code>java -jar target\\online-examination-system-1.0.0.jar</code>", code_style))

    story.append(Paragraph("Accessing the Web Portal:", h2_style))
    story.append(Paragraph("Navigate to <b>http://localhost:8080</b>", body_style))
    story.append(Paragraph("• Student Demo: <code>student1</code> / <code>pass123</code>", bullet_style))
    story.append(Paragraph("• Admin Demo: <code>admin</code> / <code>admin123</code>", bullet_style))

    story.append(Spacer(1, 20))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#e2e8f0"), spaceBefore=10, spaceAfter=10))
    story.append(Paragraph("<i>End of Project Documentation Report - ExamiQ Online Examination Platform</i>", ParagraphStyle('FooterEnd', fontName='Helvetica-Oblique', fontSize=9, textColor=colors.HexColor("#94a3b8"), alignment=1)))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Successfully generated {filename}")

if __name__ == "__main__":
    create_report()
