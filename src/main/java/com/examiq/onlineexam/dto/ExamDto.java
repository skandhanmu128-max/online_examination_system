package com.examiq.onlineexam.dto;

import com.examiq.onlineexam.model.Exam;
import com.examiq.onlineexam.model.ExamStatus;
import java.time.LocalDateTime;

public class ExamDto {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private Integer durationMinutes;
    private Double totalMarks;
    private Double passMarks;
    private boolean enableNegativeMarking;
    private Double negativeMarksPerQuestion;
    private ExamStatus status;
    private int questionCount;
    private LocalDateTime createdAt;

    public ExamDto() {}

    public ExamDto(Exam exam, int questionCount) {
        this.id = exam.getId();
        this.title = exam.getTitle();
        this.subject = exam.getSubject();
        this.description = exam.getDescription();
        this.durationMinutes = exam.getDurationMinutes();
        this.totalMarks = exam.getTotalMarks();
        this.passMarks = exam.getPassMarks();
        this.enableNegativeMarking = exam.isEnableNegativeMarking();
        this.negativeMarksPerQuestion = exam.getNegativeMarksPerQuestion();
        this.status = exam.getStatus();
        this.questionCount = questionCount;
        this.createdAt = exam.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }

    public Double getPassMarks() { return passMarks; }
    public void setPassMarks(Double passMarks) { this.passMarks = passMarks; }

    public boolean isEnableNegativeMarking() { return enableNegativeMarking; }
    public void setEnableNegativeMarking(boolean enableNegativeMarking) { this.enableNegativeMarking = enableNegativeMarking; }

    public Double getNegativeMarksPerQuestion() { return negativeMarksPerQuestion; }
    public void setNegativeMarksPerQuestion(Double negativeMarksPerQuestion) { this.negativeMarksPerQuestion = negativeMarksPerQuestion; }

    public ExamStatus getStatus() { return status; }
    public void setStatus(ExamStatus status) { this.status = status; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
