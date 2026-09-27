package com.examiq.onlineexam.controller;

import com.examiq.onlineexam.dto.*;
import com.examiq.onlineexam.model.ExamAttempt;
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
@RequestMapping("/api/student")
public class StudentExamController {

    private final ExamService examService;
    private final PdfReportService pdfReportService;

    public StudentExamController(ExamService examService, PdfReportService pdfReportService) {
        this.examService = examService;
        this.pdfReportService = pdfReportService;
    }

    private User getAuthenticatedUser(HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            throw new IllegalStateException("User is not authenticated. Please log in.");
        }
        return user;
    }

    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getAvailableExams(HttpSession session) {
        getAuthenticatedUser(session);
        return ResponseEntity.ok(ApiResponse.ok("Available published exams", examService.getPublishedExams()));
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<ExamAttempt>> startExam(@PathVariable Long examId, HttpSession session) {
        User student = getAuthenticatedUser(session);
        try {
            ExamAttempt attempt = examService.startExam(student, examId);
            return ResponseEntity.ok(ApiResponse.ok("Exam attempt started", attempt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/exams/{examId}/questions")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getExamQuestions(@PathVariable Long examId, HttpSession session) {
        getAuthenticatedUser(session);
        return ResponseEntity.ok(ApiResponse.ok("Exam questions", examService.getQuestionsByExamId(examId, false)));
    }

    @PostMapping("/attempts/{attemptId}/save-answer")
    public ResponseEntity<ApiResponse<String>> saveAnswer(@PathVariable Long attemptId,
                                                           @RequestBody SaveAnswerRequest request,
                                                           HttpSession session) {
        getAuthenticatedUser(session);
        try {
            examService.saveAnswer(attemptId, request);
            return ResponseEntity.ok(ApiResponse.ok("Answer recorded", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/attempts/{attemptId}/log-violation")
    public ResponseEntity<ApiResponse<String>> logViolation(@PathVariable Long attemptId, HttpSession session) {
        getAuthenticatedUser(session);
        try {
            examService.logViolation(attemptId);
            return ResponseEntity.ok(ApiResponse.ok("Violation logged", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<ApiResponse<AttemptResultDto>> submitExam(@PathVariable Long attemptId,
                                                                     @RequestParam(defaultValue = "false") boolean isTimeout,
                                                                     HttpSession session) {
        getAuthenticatedUser(session);
        try {
            AttemptResultDto result = examService.submitExam(attemptId, isTimeout);
            return ResponseEntity.ok(ApiResponse.ok("Exam submitted successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<ApiResponse<AttemptResultDto>> getAttemptResult(@PathVariable Long attemptId, HttpSession session) {
        getAuthenticatedUser(session);
        try {
            AttemptResultDto result = examService.getAttemptResult(attemptId);
            return ResponseEntity.ok(ApiResponse.ok("Attempt result fetched", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/attempts/history")
    public ResponseEntity<ApiResponse<List<AttemptResultDto>>> getStudentAttemptHistory(HttpSession session) {
        User student = getAuthenticatedUser(session);
        List<AttemptResultDto> history = examService.getStudentAttemptHistory(student.getId());
        return ResponseEntity.ok(ApiResponse.ok("Attempt history", history));
    }

    @GetMapping("/attempts/{attemptId}/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable Long attemptId, HttpSession session) {
        getAuthenticatedUser(session);
        AttemptResultDto result = examService.getAttemptResult(attemptId);
        byte[] pdfBytes = pdfReportService.generateAttemptReportPdf(result);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "ExamiQ_Scorecard_" + attemptId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
