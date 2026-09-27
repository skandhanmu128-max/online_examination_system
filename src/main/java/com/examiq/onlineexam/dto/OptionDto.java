package com.examiq.onlineexam.dto;

import com.examiq.onlineexam.model.QuestionOption;

public class OptionDto {
    private Long id;
    private String optionText;
    private boolean isCorrect;

    public OptionDto() {}

    public OptionDto(QuestionOption option, boolean includeAnswer) {
        this.id = option.getId();
        this.optionText = option.getOptionText();
        if (includeAnswer) {
            this.isCorrect = option.isCorrect();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
