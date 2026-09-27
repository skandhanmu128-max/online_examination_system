package com.examiq.onlineexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Double totalMarks;

    @Column(nullable = false)
    private Double passMarks;

    private boolean enableNegativeMarking = false;
    private Double negativeMarksPerQuestion = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status = ExamStatus.DRAFT;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Exam() {}

    public Exam(String title, String subject, String description, Integer durationMinutes, Double totalMarks, Double passMarks, boolean enableNegativeMarking, Double negativeMarksPerQuestion, ExamStatus status) {
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passMarks = passMarks;
        this.enableNegativeMarking = enableNegativeMarking;
        this.negativeMarksPerQuestion = negativeMarksPerQuestion;
        this.status = status;
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
