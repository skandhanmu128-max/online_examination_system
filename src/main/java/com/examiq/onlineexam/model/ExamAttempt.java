package com.examiq.onlineexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime submitTime;

    private Double scoreObtained = 0.0;
    private Double totalMarks = 0.0;
    private boolean passed = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    private int violationCount = 0;

    public ExamAttempt() {}

    public ExamAttempt(User student, Exam exam, LocalDateTime startTime) {
        this.student = student;
        this.exam = exam;
        this.startTime = startTime;
        this.totalMarks = exam.getTotalMarks();
        this.status = AttemptStatus.IN_PROGRESS;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }

    public Double getScoreObtained() { return scoreObtained; }
    public void setScoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; }

    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public AttemptStatus getStatus() { return status; }
    public void setStatus(AttemptStatus status) { this.status = status; }

    public int getViolationCount() { return violationCount; }
    public void setViolationCount(int violationCount) { this.violationCount = violationCount; }
}
