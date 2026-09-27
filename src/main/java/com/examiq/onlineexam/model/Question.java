package com.examiq.onlineexam.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false, length = 2000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type = QuestionType.SINGLE_CHOICE;

    @Column(nullable = false)
    private Double marks = 1.0;

    @Column(length = 2000)
    private String explanation;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<QuestionOption> options = new ArrayList<>();

    public Question() {}

    public Question(Exam exam, String questionText, QuestionType type, Double marks, String explanation) {
        this.exam = exam;
        this.questionText = questionText;
        this.type = type;
        this.marks = marks;
        this.explanation = explanation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public Double getMarks() { return marks; }
    public void setMarks(Double marks) { this.marks = marks; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<QuestionOption> getOptions() { return options; }
    public void setOptions(List<QuestionOption> options) { this.options = options; }

    public void addOption(QuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }
}
