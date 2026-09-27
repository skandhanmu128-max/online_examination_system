package com.examiq.onlineexam.service;

import com.examiq.onlineexam.dto.*;
import com.examiq.onlineexam.model.*;
import com.examiq.onlineexam.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final ExamAttemptRepository attemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserRepository userRepository;

    public ExamService(ExamRepository examRepository,
                       QuestionRepository questionRepository,
                       QuestionOptionRepository optionRepository,
                       ExamAttemptRepository attemptRepository,
                       StudentAnswerRepository studentAnswerRepository,
                       UserRepository userRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.userRepository = userRepository;
    }

    // --- EXAM MANAGEMENT (ADMIN) ---

    public List<ExamDto> getAllExams() {
        return examRepository.findAll().stream()
                .map(exam -> new ExamDto(exam, (int) questionRepository.countByExamId(exam.getId())))
                .collect(Collectors.toList());
    }

    public List<ExamDto> getPublishedExams() {
        return examRepository.findByStatus(ExamStatus.PUBLISHED).stream()
                .map(exam -> new ExamDto(exam, (int) questionRepository.countByExamId(exam.getId())))
                .collect(Collectors.toList());
    }

    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + id));
        return new ExamDto(exam, (int) questionRepository.countByExamId(id));
    }

    @Transactional
    public ExamDto createExam(Exam exam) {
        if (exam.getDurationMinutes() == null || exam.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0 minutes.");
        }
        if (exam.getTotalMarks() == null || exam.getTotalMarks() <= 0) {
            exam.setTotalMarks(100.0);
        }
        if (exam.getPassMarks() == null || exam.getPassMarks() < 0) {
            exam.setPassMarks(exam.getTotalMarks() * 0.4);
        }
        Exam saved = examRepository.save(exam);
        return new ExamDto(saved, 0);
    }

    @Transactional
    public ExamDto updateExam(Long id, Exam updatedExam) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + id));
        exam.setTitle(updatedExam.getTitle());
        exam.setSubject(updatedExam.getSubject());
        exam.setDescription(updatedExam.getDescription());
        exam.setDurationMinutes(updatedExam.getDurationMinutes());
        exam.setTotalMarks(updatedExam.getTotalMarks());
        exam.setPassMarks(updatedExam.getPassMarks());
        exam.setEnableNegativeMarking(updatedExam.isEnableNegativeMarking());
        exam.setNegativeMarksPerQuestion(updatedExam.getNegativeMarksPerQuestion());
        exam.setStatus(updatedExam.getStatus());
        
        Exam saved = examRepository.save(exam);
        return new ExamDto(saved, (int) questionRepository.countByExamId(saved.getId()));
    }

    @Transactional
    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    // --- QUESTION MANAGEMENT ---

    public List<QuestionDto> getQuestionsByExamId(Long examId, boolean includeAnswers) {
        List<Question> questions = questionRepository.findByExamId(examId);
        return questions.stream()
                .map(q -> new QuestionDto(q, includeAnswers))
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDto addQuestionToExam(Long examId, QuestionDto questionDto) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        Question question = new Question(
                exam,
                questionDto.getQuestionText(),
                questionDto.getType() != null ? questionDto.getType() : QuestionType.SINGLE_CHOICE,
                questionDto.getMarks() != null ? questionDto.getMarks() : 1.0,
                questionDto.getExplanation()
        );

        if (questionDto.getOptions() != null) {
            for (OptionDto optDto : questionDto.getOptions()) {
                QuestionOption opt = new QuestionOption(optDto.getOptionText(), optDto.isCorrect());
                question.addOption(opt);
            }
        }

        Question saved = questionRepository.save(question);
        
        // Recalculate total marks if needed
        recalculateExamTotalMarks(exam);

        return new QuestionDto(saved, true);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
        Exam exam = q.getExam();
        questionRepository.delete(q);
        recalculateExamTotalMarks(exam);
    }

    private void recalculateExamTotalMarks(Exam exam) {
        List<Question> questions = questionRepository.findByExamId(exam.getId());
        double total = questions.stream().mapToDouble(Question::getMarks).sum();
        if (total > 0) {
            exam.setTotalMarks(total);
            if (exam.getPassMarks() == null || exam.getPassMarks() == 0) {
                exam.setPassMarks(total * 0.4);
            }
            examRepository.save(exam);
        }
    }

    // --- STUDENT EXAM LOGIC & TIMER ---

    @Transactional
    public ExamAttempt startExam(User student, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new IllegalStateException("This exam is currently not available.");
        }

        // Check for existing ongoing attempt
        Optional<ExamAttempt> existing = attemptRepository.findByStudentIdAndExamIdAndStatus(
                student.getId(), examId, AttemptStatus.IN_PROGRESS
        );

        if (existing.isPresent()) {
            return existing.get();
        }

        ExamAttempt attempt = new ExamAttempt(student, exam, LocalDateTime.now());
        return attemptRepository.save(attempt);
    }

    @Transactional
    public void saveAnswer(Long attemptId, SaveAnswerRequest request) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Exam attempt is already submitted or closed.");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        Optional<StudentAnswer> existingAnswerOpt = studentAnswerRepository.findByAttemptIdAndQuestionId(
                attemptId, request.getQuestionId()
        );

        StudentAnswer answer = existingAnswerOpt.orElseGet(() -> new StudentAnswer(attempt, question));
        if (request.getSelectedOptionIds() != null) {
            answer.setSelectedOptionIds(new HashSet<>(request.getSelectedOptionIds()));
        } else {
            answer.getSelectedOptionIds().clear();
        }
        answer.setMarkedForReview(request.isMarkedForReview());
        studentAnswerRepository.save(answer);
    }

    @Transactional
    public void logViolation(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
        attempt.setViolationCount(attempt.getViolationCount() + 1);
        
        // Auto submit if violation threshold (3 violations) reached
        if (attempt.getViolationCount() >= 3 && attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            attempt.setStatus(AttemptStatus.FLAGGED_VIOLATION);
            evaluateAndFinalizeAttempt(attempt, AttemptStatus.FLAGGED_VIOLATION);
        } else {
            attemptRepository.save(attempt);
        }
    }

    @Transactional
    public AttemptResultDto submitExam(Long attemptId, boolean isTimeout) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return getAttemptResult(attemptId);
        }

        AttemptStatus finalStatus = isTimeout ? AttemptStatus.AUTO_SUBMITTED_TIMEOUT : AttemptStatus.SUBMITTED;
        return evaluateAndFinalizeAttempt(attempt, finalStatus);
    }

    // --- AUTO-EVALUATION SCORE ENGINE ---

    private AttemptResultDto evaluateAndFinalizeAttempt(ExamAttempt attempt, AttemptStatus targetStatus) {
        Exam exam = attempt.getExam();
        List<Question> questions = questionRepository.findByExamId(exam.getId());
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attempt.getId());
        Map<Long, StudentAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), ans -> ans));

        double scoreObtained = 0.0;
        int correctCount = 0;
        int incorrectCount = 0;
        int unattemptedCount = 0;

        for (Question q : questions) {
            StudentAnswer sa = answerMap.get(q.getId());
            Set<Long> correctOptionIds = q.getOptions().stream()
                    .filter(QuestionOption::isCorrect)
                    .map(QuestionOption::getId)
                    .collect(Collectors.toSet());

            if (sa == null || sa.getSelectedOptionIds() == null || sa.getSelectedOptionIds().isEmpty()) {
                unattemptedCount++;
                if (sa != null) {
                    sa.setMarksAwarded(0.0);
                    sa.setCorrect(false);
                    studentAnswerRepository.save(sa);
                }
                continue;
            }

            Set<Long> studentOptions = sa.getSelectedOptionIds();
            boolean isMatch = studentOptions.equals(correctOptionIds);

            if (isMatch) {
                double marks = q.getMarks();
                scoreObtained += marks;
                correctCount++;
                sa.setMarksAwarded(marks);
                sa.setCorrect(true);
            } else {
                incorrectCount++;
                double penalty = 0.0;
                if (exam.isEnableNegativeMarking() && exam.getNegativeMarksPerQuestion() != null) {
                    penalty = exam.getNegativeMarksPerQuestion();
                }
                scoreObtained -= penalty;
                sa.setMarksAwarded(-penalty);
                sa.setCorrect(false);
            }
            studentAnswerRepository.save(sa);
        }

        if (scoreObtained < 0) {
            scoreObtained = 0.0;
        }

        attempt.setScoreObtained(scoreObtained);
        attempt.setTotalMarks(exam.getTotalMarks());
        attempt.setSubmitTime(LocalDateTime.now());
        attempt.setPassed(scoreObtained >= exam.getPassMarks());
        attempt.setStatus(targetStatus);
        attemptRepository.save(attempt);

        return getAttemptResult(attempt.getId());
    }

    public AttemptResultDto getAttemptResult(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found with ID: " + attemptId));

        Exam exam = attempt.getExam();
        User student = attempt.getStudent();
        List<Question> questions = questionRepository.findByExamId(exam.getId());
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByAttemptId(attemptId);
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getId(), sa -> sa));

        AttemptResultDto result = new AttemptResultDto();
        result.setAttemptId(attempt.getId());
        result.setExamId(exam.getId());
        result.setExamTitle(exam.getTitle());
        result.setSubject(exam.getSubject());
        result.setStudentName(student.getFullName());
        result.setStudentEmail(student.getEmail());
        result.setStartTime(attempt.getStartTime());
        result.setSubmitTime(attempt.getSubmitTime());
        result.setScoreObtained(attempt.getScoreObtained());
        result.setTotalMarks(attempt.getTotalMarks());
        result.setPassMarks(exam.getPassMarks());
        result.setPassed(attempt.isPassed());
        result.setStatus(attempt.getStatus());
        result.setViolationCount(attempt.getViolationCount());

        double percentage = exam.getTotalMarks() > 0 ? (attempt.getScoreObtained() / exam.getTotalMarks()) * 100.0 : 0.0;
        result.setPercentage(Math.round(percentage * 100.0) / 100.0);

        int correct = 0;
        int incorrect = 0;
        int unattempted = 0;

        List<QuestionResultDto> qResults = new ArrayList<>();
        for (Question q : questions) {
            QuestionResultDto qDto = new QuestionResultDto();
            qDto.setQuestionId(q.getId());
            qDto.setQuestionText(q.getQuestionText());
            qDto.setType(q.getType());
            qDto.setMarks(q.getMarks());
            qDto.setExplanation(q.getExplanation());
            qDto.setOptions(q.getOptions().stream().map(o -> new OptionDto(o, true)).collect(Collectors.toList()));

            StudentAnswer sa = answerMap.get(q.getId());
            if (sa != null && sa.getSelectedOptionIds() != null && !sa.getSelectedOptionIds().isEmpty()) {
                qDto.setSelectedOptionIds(sa.getSelectedOptionIds());
                qDto.setMarksAwarded(sa.getMarksAwarded());
                qDto.setCorrect(sa.isCorrect());
                if (sa.isCorrect()) correct++; else incorrect++;
            } else {
                qDto.setSelectedOptionIds(Collections.emptySet());
                qDto.setMarksAwarded(0.0);
                qDto.setCorrect(false);
                unattempted++;
            }
            qResults.add(qDto);
        }

        result.setTotalQuestions(questions.size());
        result.setCorrectQuestions(correct);
        result.setIncorrectQuestions(incorrect);
        result.setUnattemptedQuestions(unattempted);
        result.setQuestionResults(qResults);

        return result;
    }

    public List<AttemptResultDto> getStudentAttemptHistory(Long studentId) {
        List<ExamAttempt> attempts = attemptRepository.findByStudentId(studentId);
        return attempts.stream()
                .map(att -> getAttemptResult(att.getId()))
                .collect(Collectors.toList());
    }

    public List<AttemptResultDto> getAllAttemptsForExam(Long examId) {
        List<ExamAttempt> attempts = attemptRepository.findByExamId(examId);
        return attempts.stream()
                .map(att -> getAttemptResult(att.getId()))
                .collect(Collectors.toList());
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalStudents(userRepository.countByRole(Role.ROLE_STUDENT));
        stats.setTotalExams(examRepository.count());
        stats.setPublishedExams(examRepository.countByStatus(ExamStatus.PUBLISHED));
        stats.setTotalAttempts(attemptRepository.count());

        List<ExamAttempt> attempts = attemptRepository.findAll();
        long passed = attempts.stream().filter(ExamAttempt::isPassed).count();
        stats.setPassedAttempts(passed);
        stats.setFailedAttempts(attempts.size() - passed);

        if (!attempts.isEmpty()) {
            double avg = attempts.stream()
                    .mapToDouble(att -> att.getTotalMarks() > 0 ? (att.getScoreObtained() / att.getTotalMarks()) * 100.0 : 0.0)
                    .average()
                    .orElse(0.0);
            stats.setAveragePercentage(Math.round(avg * 100.0) / 100.0);
        } else {
            stats.setAveragePercentage(0.0);
        }

        return stats;
    }
}
