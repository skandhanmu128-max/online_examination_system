import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_color):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_color}"/>')
    tcPr.append(shd)

def create_docx_report(filename="ExamiQ_Project_Report.docx"):
    doc = docx.Document()

    # Page Margins
    sections = doc.sections
    for section in sections:
        section.top_margin = Inches(0.75)
        section.bottom_margin = Inches(0.75)
        section.left_margin = Inches(0.75)
        section.right_margin = Inches(0.75)

    # Title Page
    p_sub = doc.add_paragraph()
    run_sub = p_sub.add_run("PROJECT DOCUMENTATION REPORT")
    run_sub.font.name = 'Arial'
    run_sub.font.size = Pt(10)
    run_sub.font.bold = True
    run_sub.font.color.rgb = RGBColor(99, 102, 241)

    p_title = doc.add_paragraph()
    run_title = p_title.add_run("ExamiQ - Secure Online Examination & Evaluation Platform")
    run_title.font.name = 'Arial'
    run_title.font.size = Pt(24)
    run_title.font.bold = True
    run_title.font.color.rgb = RGBColor(30, 27, 75)

    p_subtitle = doc.add_paragraph()
    run_subtitle = p_subtitle.add_run("A Modern Java Enterprise System with Proctored Testing, Real-Time Timer Control, Instant Auto-Evaluation, UI Screenshots, Source Code, and PDF Scorecards.")
    run_subtitle.font.name = 'Arial'
    run_subtitle.font.size = Pt(12)
    run_subtitle.font.color.rgb = RGBColor(79, 70, 229)

    doc.add_paragraph().paragraph_format.space_after = Pt(30)

    # Metadata Table
    table = doc.add_table(rows=6, cols=2)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    meta_info = [
        ("Project Name:", "ExamiQ Online Examination Platform"),
        ("Architecture:", "Java 21 / Spring Boot 3 / JPA / H2 / OpenPDF"),
        ("Primary Modules:", "Student Auth, Question Bank, Timer Engine, Anti-Cheat, Auto-Evaluation, PDF Export, Admin Dashboard"),
        ("Deliverables Included:", "Java Source Code, Runnable JAR, System Screenshots, PDF & DOCX Reports"),
        ("Status:", "COMPLETED & FULLY VERIFIED"),
        ("Date:", "September 2026")
    ]

    for idx, (label, val) in enumerate(meta_info):
        row = table.rows[idx]
        cell_lbl, cell_val = row.cells[0], row.cells[1]
        
        p0 = cell_lbl.paragraphs[0]
        r0 = p0.add_run(label)
        r0.bold = True
        r0.font.size = Pt(9.5)

        p1 = cell_val.paragraphs[0]
        r1 = p1.add_run(val)
        r1.font.size = Pt(9.5)

        set_cell_background(cell_lbl, "F8FAFC")
        set_cell_background(cell_val, "F8FAFC")

    doc.add_page_break()

    # Section 1
    h1 = doc.add_heading("1. Executive Summary & Project Overview", level=1)
    p_body = doc.add_paragraph(
        "ExamiQ is a full-featured, secure online examination platform engineered to streamline test creation, candidate authentication, proctored exam delivery, real-time timer control, automated evaluation, and comprehensive result reporting. Designed with modern Java enterprise best practices, ExamiQ provides educational institutions, universities, and corporate certification bodies with an end-to-end web testing platform."
    )

    doc.add_heading("Key Capabilities Delivered:", level=2)
    bullets = [
        "Role-Based Access Control (RBAC): Dedicated portals for Students and Administrators with secure BCrypt password hashing.",
        "Flexible Question Bank Management: Supports Single Choice (MCQ), Multiple Choice, and True/False questions with positive marks & negative marking.",
        "Timer-Based Exam Engine: Synchronized real-time server/client countdown clock with automatic test submission upon timer expiration.",
        "Anti-Cheat Proctored Control: Live tab-switching and window-focus detector issuing warnings and auto-terminating tests upon 3 violations.",
        "Automated Evaluation Engine: Instant score calculation, percentage computing, grade assignment, and pass/fail status determination.",
        "PDF Scorecard Generation: Server-side dynamic PDF report creation using OpenPDF for printable, official student scorecards.",
        "Admin Command Center: Interactive dashboard featuring real-time statistics, candidate attempt tables, and Chart.js performance charts."
    ]
    for b in bullets:
        doc.add_paragraph(b, style='List Bullet')

    doc.add_page_break()

    # Section 2: Screenshots
    doc.add_heading("2. Application Screenshots & User Interface", level=1)

    screenshots = [
        ("screenshot_login.png", "Figure 2.1: Student & Administrator Glassmorphism Login Interface"),
        ("screenshot_student_portal.png", "Figure 2.2: Student Examination Portal & Attempt Records"),
        ("screenshot_exam_workspace.png", "Figure 2.3: Proctored Exam Workspace with Live Timer & Question Palette"),
        ("screenshot_anticheat_alert.png", "Figure 2.4: Anti-Cheat Security Violation Alert Overlay"),
        ("screenshot_scorecard.png", "Figure 2.5: Automated Scorecard & Detailed Answer Review"),
        ("screenshot_admin_dashboard.png", "Figure 2.6: Administrator Control Center & Chart.js Metrics")
    ]

    for img_path, caption in screenshots:
        if os.path.exists(img_path):
            doc.add_heading(caption, level=2)
            doc.add_picture(img_path, width=Inches(6.0))
            p_cap = doc.add_paragraph(f"Caption: Screenshot of {caption}")
            p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
            doc.add_paragraph()

    doc.add_page_break()

    # Section 3: Source Code
    doc.add_heading("3. Core Source Code Implementation", level=1)

    doc.add_heading("3.1 Auto-Evaluation Engine (ExamService.java)", level=2)
    code_eval = """private AttemptResultDto evaluateAndFinalizeAttempt(ExamAttempt attempt, AttemptStatus targetStatus) {
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

        if (sa == null || sa.getSelectedOptionIds() == null || sa.getSelectedOptionIds().isEmpty()) continue;

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
    p_code1 = doc.add_paragraph()
    r_code1 = p_code1.add_run(code_eval)
    r_code1.font.name = 'Courier New'
    r_code1.font.size = Pt(8.5)

    doc.add_heading("3.2 Anti-Cheat Violation Tracker (ExamService.java)", level=2)
    code_anticheat = """@Transactional
public void logViolation(Long attemptId) {
    ExamAttempt attempt = attemptRepository.findById(attemptId)
            .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
    attempt.setViolationCount(attempt.getViolationCount() + 1);
    
    if (attempt.getViolationCount() >= 3 && attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
        attempt.setStatus(AttemptStatus.FLAGGED_VIOLATION);
        evaluateAndFinalizeAttempt(attempt, AttemptStatus.FLAGGED_VIOLATION);
    } else {
        attemptRepository.save(attempt);
    }
}"""
    p_code2 = doc.add_paragraph()
    r_code2 = p_code2.add_run(code_anticheat)
    r_code2.font.name = 'Courier New'
    r_code2.font.size = Pt(8.5)

    doc.save(filename)
    print(f"Successfully generated {filename}")

if __name__ == "__main__":
    create_docx_report()
