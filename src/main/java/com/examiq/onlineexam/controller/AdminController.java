package com.examiq.onlineexam.controller;

import com.examiq.onlineexam.dto.*;
import com.examiq.onlineexam.model.Exam;
import com.examiq.onlineexam.model.Role;
import com.examiq.onlineexam.model.User;
import com.examiq.onlineexam.service.ExamService;
import com.examiq.onlineexam.service.PdfReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ExamService examService;
    private final PdfReportService pdfReportService;

    public AdminController(ExamService examService, PdfReportService pdfReportService) {
        this.examService = examService;
        this.pdfReportService = pdfReportService;
    }

    private void verifyAdmin(HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user == null || user.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalStateException("Access Denied: Admin privileges required.");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats(HttpSession session) {
        verifyAdmin(session);
        return ResponseEntity.ok(ApiResponse.ok("Dashboard statistics", examService.getDashboardStats()));
    }

    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getAllExams(HttpSession session) {
        verifyAdmin(session);
        return ResponseEntity.ok(ApiResponse.ok("All exams list", examService.getAllExams()));
    }

    @PostMapping("/exams")
    public ResponseEntity<ApiResponse<ExamDto>> createExam(@RequestBody Exam exam, HttpSession session) {
        verifyAdmin(session);
        try {
            ExamDto created = examService.createExam(exam);
            return ResponseEntity.ok(ApiResponse.ok("Exam created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<ApiResponse<ExamDto>> updateExam(@PathVariable Long id, @RequestBody Exam exam, HttpSession session) {
        verifyAdmin(session);
        try {
            ExamDto updated = examService.updateExam(id, exam);
            return ResponseEntity.ok(ApiResponse.ok("Exam updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExam(@PathVariable Long id, HttpSession session) {
        verifyAdmin(session);
        try {
            examService.deleteExam(id);
            return ResponseEntity.ok(ApiResponse.ok("Exam deleted successfully", "DELETED"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/exams/{examId}/questions")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getExamQuestions(@PathVariable Long examId, HttpSession session) {
        verifyAdmin(session);
        return ResponseEntity.ok(ApiResponse.ok("Questions for exam", examService.getQuestionsByExamId(examId, true)));
    }

    @PostMapping("/exams/{examId}/questions")
    public ResponseEntity<ApiResponse<QuestionDto>> addQuestion(@PathVariable Long examId,
                                                                 @RequestBody QuestionDto questionDto,
                                                                 HttpSession session) {
        verifyAdmin(session);
        try {
            QuestionDto created = examService.addQuestionToExam(examId, questionDto);
            return ResponseEntity.ok(ApiResponse.ok("Question added successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Long questionId, HttpSession session) {
        verifyAdmin(session);
        try {
            examService.deleteQuestion(questionId);
            return ResponseEntity.ok(ApiResponse.ok("Question deleted successfully", "DELETED"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/exams/{examId}/attempts")
    public ResponseEntity<ApiResponse<List<AttemptResultDto>>> getExamAttempts(@PathVariable Long examId, HttpSession session) {
        verifyAdmin(session);
        return ResponseEntity.ok(ApiResponse.ok("Attempts for exam", examService.getAllAttemptsForExam(examId)));
    }

    @GetMapping("/attempts/{attemptId}/pdf")
    public ResponseEntity<byte[]> downloadAttemptPdf(@PathVariable Long attemptId, HttpSession session) {
        verifyAdmin(session);
        AttemptResultDto result = examService.getAttemptResult(attemptId);
        byte[] pdfBytes = pdfReportService.generateAttemptReportPdf(result);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "Admin_Scorecard_" + attemptId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
