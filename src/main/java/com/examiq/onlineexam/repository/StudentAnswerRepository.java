package com.examiq.onlineexam.repository;

import com.examiq.onlineexam.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByAttemptId(Long attemptId);
    Optional<StudentAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
}
