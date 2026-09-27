package com.examiq.onlineexam.dto;

import java.util.Set;

public class SaveAnswerRequest {
    private Long questionId;
    private Set<Long> selectedOptionIds;
    private boolean isMarkedForReview;

    public SaveAnswerRequest() {}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Set<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(Set<Long> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

    public boolean isMarkedForReview() { return isMarkedForReview; }
    public void setMarkedForReview(boolean markedForReview) { isMarkedForReview = markedForReview; }
}
