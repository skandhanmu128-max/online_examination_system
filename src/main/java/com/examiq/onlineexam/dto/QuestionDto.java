package com.examiq.onlineexam.dto;

import com.examiq.onlineexam.model.Question;
import com.examiq.onlineexam.model.QuestionType;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDto {
    private Long id;
    private Long examId;
    private String questionText;
    private QuestionType type;
    private Double marks;
    private String explanation;
    private List<OptionDto> options;

    public QuestionDto() {}

    public QuestionDto(Question q, boolean includeAnswers) {
        this.id = q.getId();
        this.examId = q.getExam() != null ? q.getExam().getId() : null;
        this.questionText = q.getQuestionText();
        this.type = q.getType();
        this.marks = q.getMarks();
        if (includeAnswers) {
            this.explanation = q.getExplanation();
        }
        if (q.getOptions() != null) {
            this.options = q.getOptions().stream()
                    .map(opt -> new OptionDto(opt, includeAnswers))
                    .collect(Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public Double getMarks() { return marks; }
    public void setMarks(Double marks) { this.marks = marks; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<OptionDto> getOptions() { return options; }
    public void setOptions(List<OptionDto> options) { this.options = options; }
}
