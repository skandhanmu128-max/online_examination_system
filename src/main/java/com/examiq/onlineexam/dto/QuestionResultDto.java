package com.examiq.onlineexam.dto;

import com.examiq.onlineexam.model.QuestionType;
import java.util.List;
import java.util.Set;

public class QuestionResultDto {
    private Long questionId;
    private String questionText;
    private QuestionType type;
    private Double marks;
    private Double marksAwarded;
    private boolean isCorrect;
    private String explanation;
    private Set<Long> selectedOptionIds;
    private List<OptionDto> options;

    public QuestionResultDto() {}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public Double getMarks() { return marks; }
    public void setMarks(Double marks) { this.marks = marks; }

    public Double getMarksAwarded() { return marksAwarded; }
    public void setMarksAwarded(Double marksAwarded) { this.marksAwarded = marksAwarded; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Set<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(Set<Long> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

    public List<OptionDto> getOptions() { return options; }
    public void setOptions(List<OptionDto> options) { this.options = options; }
}
