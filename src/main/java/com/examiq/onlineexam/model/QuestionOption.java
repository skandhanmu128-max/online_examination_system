package com.examiq.onlineexam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "question_options")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(nullable = false, length = 1000)
    private String optionText;

    @Column(nullable = false)
    private boolean isCorrect = false;

    public QuestionOption() {}

    public QuestionOption(String optionText, boolean isCorrect) {
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
