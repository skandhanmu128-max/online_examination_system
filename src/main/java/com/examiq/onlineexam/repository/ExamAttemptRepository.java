package com.examiq.onlineexam.repository;

import com.examiq.onlineexam.model.AttemptStatus;
import com.examiq.onlineexam.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByStudentId(Long studentId);
    List<ExamAttempt> findByExamId(Long examId);
    Optional<ExamAttempt> findByStudentIdAndExamIdAndStatus(Long studentId, Long examId, AttemptStatus status);
    List<ExamAttempt> findByStudentIdAndExamId(Long studentId, Long examId);
    long countByStatus(AttemptStatus status);
}
