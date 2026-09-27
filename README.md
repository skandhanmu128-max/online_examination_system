# 🎓 ExamiQ - Secure Online Examination Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

**ExamiQ** is a full-featured, secure online examination platform engineered to streamline test creation, candidate authentication, proctored exam delivery, real-time timer control, automated evaluation, and comprehensive result reporting.

---

## 🌟 Key Features

- 🔒 **Student Authentication & RBAC**: Secure login/registration supporting `ROLE_STUDENT` and `ROLE_ADMIN` with BCrypt password hashing.
- ⚙️ **Question Set Management**: Add, edit, and publish single-select (MCQ), multi-select, and True/False questions with positive & negative marking rules.
- ⏱️ **Timer-Based Exam Engine**: Real-time server-synced countdown timer that automatically submits tests when time expires.
- 🛡️ **Anti-Cheat Proctored Control**: Live window-blur and tab-switching detector that logs security warnings and auto-terminates tests on 3 violations.
- 📈 **Automated Evaluation Engine**: Instant scoring, percentage computing, grade calculation, and detailed answer review with explanation boxes.
- 📄 **PDF Scorecard Generation**: Server-side OpenPDF generator producing downloadable, official student scorecards.
- 📊 **Admin Command Center**: Real-time metrics dashboard, Chart.js performance analytics, candidate attempt tracking, and question bank manager.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Backend Framework** | Java 21, Spring Boot 3.2.3 (Spring Web, Spring Security, Spring Data JPA) |
| **Database** | Embedded H2 Relational Database with Web Console |
| **PDF Generation** | OpenPDF (LibrePDF 1.3.39) |
| **Frontend UI** | HTML5, Vanilla Glassmorphism CSS3, JavaScript (ES6), Chart.js |
| **Build Tool** | Apache Maven 3.9.9 |

---

## 🚀 Quick Start & Installation

### Prerequisites
- Java Development Kit (JDK 21 or higher)
- Git

### Build & Run Instructions

1. **Clone the repository:**
   ```bash
   git clone <YOUR_GITHUB_REPOSITORY_URL>
   cd "Online Examination System"
   ```

2. **Compile and Package using Maven:**
   ```powershell
   mvn clean package -DskipTests
   ```

3. **Run the Application:**
   ```powershell
   java -jar target/online-examination-system-1.0.0.jar
   ```

4. **Access the Web Interface:**
   Open your browser and navigate to `http://localhost:8080`

---

## 🔑 Pre-seeded Demo Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Student** | `student1` | `pass123` |
| **Administrator** | `admin` | `admin123` |

---

## 📄 Project Reports & Deliverables

- **PDF Project Report**: [`ExamiQ_Project_Report.pdf`](./ExamiQ_Project_Report.pdf) (Complete architecture report with UI screenshots and source code).
- **Word Project Report**: [`ExamiQ_Project_Report.docx`](./ExamiQ_Project_Report.docx)

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).
