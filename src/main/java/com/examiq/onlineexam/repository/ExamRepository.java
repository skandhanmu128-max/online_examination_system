package com.examiq.onlineexam.repository;

import com.examiq.onlineexam.model.Exam;
import com.examiq.onlineexam.model.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStatus(ExamStatus status);
    long countByStatus(ExamStatus status);
}
