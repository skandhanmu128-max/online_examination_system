package com.examiq.onlineexam.config;

import com.examiq.onlineexam.model.*;
import com.examiq.onlineexam.repository.ExamRepository;
import com.examiq.onlineexam.repository.QuestionRepository;
import com.examiq.onlineexam.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ExamRepository examRepository,
                           QuestionRepository questionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Seed Admin User
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    "admin@examiq.edu",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);

            // Seed Sample Student User
            User student1 = new User(
                    "student1",
                    passwordEncoder.encode("pass123"),
                    "Skandhan M U",
                    "skandhan@student.edu",
                    Role.ROLE_STUDENT
            );
            userRepository.save(student1);

            User student2 = new User(
                    "student2",
                    passwordEncoder.encode("pass123"),
                    "Alex Rivera",
                    "alex@student.edu",
                    Role.ROLE_STUDENT
            );
            userRepository.save(student2);

            System.out.println(">>> Seeded Admin and Student Users successfully.");
        }

        if (examRepository.count() == 0) {
            // Seed Exam 1: Java SE 21 Professional Certification
            Exam javaExam = new Exam(
                    "Java SE 21 Core Certification",
                    "Computer Science",
                    "Comprehensive assessment covering Java OOPs, Concurrency, Streams API, JVM memory architecture, and Exception Handling.",
                    10, // 10 minutes timer
                    25.0, // total marks
                    15.0, // pass marks
                    true,
                    0.5,
                    ExamStatus.PUBLISHED
            );
            Exam savedJavaExam = examRepository.save(javaExam);

            // Q1
            Question q1 = new Question(
                    savedJavaExam,
                    "Which JVM memory region is shared among all Java threads for storing objects?",
                    QuestionType.SINGLE_CHOICE,
                    5.0,
                    "The Heap area is the runtime data space from which memory for all class instances and arrays is allocated. It is shared across all threads."
            );
            q1.addOption(new QuestionOption("JVM Stack", false));
            q1.addOption(new QuestionOption("Heap Memory", true));
            q1.addOption(new QuestionOption("Program Counter (PC) Register", false));
            q1.addOption(new QuestionOption("Native Method Stack", false));
            questionRepository.save(q1);

            // Q2
            Question q2 = new Question(
                    savedJavaExam,
                    "What is the output of compiling and executing a Java file with 'public final class' attempting to inherit another class?",
                    QuestionType.SINGLE_CHOICE,
                    5.0,
                    "A final class can inherit (extend) non-final parent classes, but no child class can extend a final class."
            );
            q2.addOption(new QuestionOption("Compilation Error: Final classes cannot extend any parent", false));
            q2.addOption(new QuestionOption("Compiles cleanly if the parent class is not final", true));
            q2.addOption(new QuestionOption("Runtime Exception: SecurityViolationException", false));
            q2.addOption(new QuestionOption("Compiles only if parent class is abstract", false));
            questionRepository.save(q2);

            // Q3
            Question q3 = new Question(
                    savedJavaExam,
                    "Which interfaces belong to the Java Collections Framework? (Select all that apply)",
                    QuestionType.MULTIPLE_CHOICE,
                    5.0,
                    "List, Set, and Map are core collection interfaces. Map does not inherit from Collection interface directly, but is part of the Java Collections Framework."
            );
            q3.addOption(new QuestionOption("java.util.List", true));
            q3.addOption(new QuestionOption("java.util.Set", true));
            q3.addOption(new QuestionOption("java.util.Map", true));
            q3.addOption(new QuestionOption("java.lang.Thread", false));
            questionRepository.save(q3);

            // Q4
            Question q4 = new Question(
                    savedJavaExam,
                    "In Java, string literals defined with double quotes are stored in the String Constant Pool.",
                    QuestionType.TRUE_FALSE,
                    5.0,
                    "True. Java maintains a specialized String Pool inside the Heap to reuse identical string literals and conserve memory."
            );
            q4.addOption(new QuestionOption("True", true));
            q4.addOption(new QuestionOption("False", false));
            questionRepository.save(q4);

            // Q5
            Question q5 = new Question(
                    savedJavaExam,
                    "What happens when a checked exception is thrown in a Java method without a try-catch block or a throws clause?",
                    QuestionType.SINGLE_CHOICE,
                    5.0,
                    "Checked exceptions must be caught or declared in the method signature; failing to do so causes a compilation error."
            );
            q5.addOption(new QuestionOption("Method executes normally and ignores exception", false));
            q5.addOption(new QuestionOption("Compilation Error", true));
            q5.addOption(new QuestionOption("Runtime Exception", false));
            q5.addOption(new QuestionOption("JVM terminates quietly", false));
            questionRepository.save(q5);

            // Seed Exam 2: Data Structures & Algorithms
            Exam dsaExam = new Exam(
                    "Data Structures & Algorithms Final Test",
                    "Computer Science",
                    "Evaluation of time complexity, binary search trees, graph algorithms, and dynamic programming.",
                    15,
                    20.0,
                    12.0,
                    false,
                    0.0,
                    ExamStatus.PUBLISHED
            );
            Exam savedDsaExam = examRepository.save(dsaExam);

            Question dq1 = new Question(
                    savedDsaExam,
                    "What is the worst-case time complexity of QuickSort when bad pivot choices are made?",
                    QuestionType.SINGLE_CHOICE,
                    10.0,
                    "In the worst case (e.g. sorted array with first element pivot), QuickSort makes O(n^2) comparisons."
            );
            dq1.addOption(new QuestionOption("O(n log n)", false));
            dq1.addOption(new QuestionOption("O(n^2)", true));
            dq1.addOption(new QuestionOption("O(n)", false));
            dq1.addOption(new QuestionOption("O(1)", false));
            questionRepository.save(dq1);

            Question dq2 = new Question(
                    savedDsaExam,
                    "A Stack operates on a Last-In, First-Out (LIFO) principle.",
                    QuestionType.TRUE_FALSE,
                    10.0,
                    "True. Stack element pushed last is popped first."
            );
            dq2.addOption(new QuestionOption("True", true));
            dq2.addOption(new QuestionOption("False", false));
            questionRepository.save(dq2);

            System.out.println(">>> Seeded Published Exams and Questions successfully.");
        }
    }
}
