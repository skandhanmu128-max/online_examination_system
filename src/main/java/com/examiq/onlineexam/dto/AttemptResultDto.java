package com.examiq.onlineexam.dto;

import com.examiq.onlineexam.model.AttemptStatus;
import java.time.LocalDateTime;
import java.util.List;

public class AttemptResultDto {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private String subject;
    private String studentName;
    private String studentEmail;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Double scoreObtained;
    private Double totalMarks;
    private Double passMarks;
    private Double percentage;
    private boolean passed;
    private AttemptStatus status;
    private int violationCount;
    private int totalQuestions;
    private int correctQuestions;
    private int incorrectQuestions;
    private int unattemptedQuestions;
    private List<QuestionResultDto> questionResults;

    public AttemptResultDto() {}

    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }

    public Double getScoreObtained() { return scoreObtained; }
    public void setScoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; }

    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }

    public Double getPassMarks() { return passMarks; }
    public void setPassMarks(Double passMarks) { this.passMarks = passMarks; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public AttemptStatus getStatus() { return status; }
    public void setStatus(AttemptStatus status) { this.status = status; }

    public int getViolationCount() { return violationCount; }
    public void setViolationCount(int violationCount) { this.violationCount = violationCount; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectQuestions() { return correctQuestions; }
    public void setCorrectQuestions(int correctQuestions) { this.correctQuestions = correctQuestions; }

    public int getIncorrectQuestions() { return incorrectQuestions; }
    public void setIncorrectQuestions(int incorrectQuestions) { this.incorrectQuestions = incorrectQuestions; }

    public int getUnattemptedQuestions() { return unattemptedQuestions; }
    public void setUnattemptedQuestions(int unattemptedQuestions) { this.unattemptedQuestions = unattemptedQuestions; }

    public List<QuestionResultDto> getQuestionResults() { return questionResults; }
    public void setQuestionResults(List<QuestionResultDto> questionResults) { this.questionResults = questionResults; }
}
