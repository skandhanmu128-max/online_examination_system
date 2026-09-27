package com.examiq.onlineexam.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_answers")
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_answer_options", joinColumns = @JoinColumn(name = "student_answer_id"))
    @Column(name = "option_id")
    private Set<Long> selectedOptionIds = new HashSet<>();

    private Double marksAwarded = 0.0;
    private boolean isCorrect = false;
    private boolean isMarkedForReview = false;

    public StudentAnswer() {}

    public StudentAnswer(ExamAttempt attempt, Question question) {
        this.attempt = attempt;
        this.question = question;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Set<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(Set<Long> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

    public Double getMarksAwarded() { return marksAwarded; }
    public void setMarksAwarded(Double marksAwarded) { this.marksAwarded = marksAwarded; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public boolean isMarkedForReview() { return isMarkedForReview; }
    public void setMarkedForReview(boolean markedForReview) { isMarkedForReview = markedForReview; }
}
