/* ExamiQ - Core SPA Logic & Exam Engine */

class ExamiQApp {
    constructor() {
        this.currentUser = null;
        this.activeView = 'authView';
        
        // Active Exam State
        this.currentAttempt = null;
        this.currentExam = null;
        this.questions = [];
        this.currentQIndex = 0;
        this.userAnswers = {};      // qId -> Set of selected optIds
        this.reviewMarks = {};       // qId -> boolean
        
        // Timer & Anti-cheat
        this.timerInterval = null;
        this.remainingSeconds = 0;
        this.violationCount = 0;

        // Chart instance
        this.perfChart = null;

        this.init();
    }

    init() {
        this.checkSession();
        this.setupAntiCheatListeners();
        this.handleQTypeChange();
    }

    // --- NAVIGATION & VIEW SWITCHING ---

    switchView(viewId) {
        document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
        const targetView = document.getElementById(viewId);
        if (targetView) {
            targetView.classList.add('active');
            this.activeView = viewId;
        }

        // Navbar user info visibility
        const navUser = document.getElementById('navUserSection');
        if (this.currentUser && viewId !== 'authView') {
            navUser.style.display = 'flex';
            document.getElementById('navUserName').innerText = this.currentUser.fullName;
            document.getElementById('navRoleBadge').innerText = this.currentUser.role.replace('ROLE_', '');
        } else {
            navUser.style.display = 'none';
        }
    }

    switchAuthTab(tab) {
        const isLogin = tab === 'login';
        document.getElementById('tabLogin').classList.toggle('active', isLogin);
        document.getElementById('tabRegister').classList.toggle('active', !isLogin);
        document.getElementById('loginForm').style.display = isLogin ? 'block' : 'none';
        document.getElementById('registerForm').style.display = isLogin ? 'none' : 'block';
    }

    showToast(message, type = 'success') {
        const container = document.getElementById('toastContainer');
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `<i class="fa-solid fa-${type === 'success' ? 'circle-check' : 'circle-exclamation'}"></i> ${message}`;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 4000);
    }

    // --- AUTHENTICATION ---

    async checkSession() {
        try {
            const res = await fetch('/api/auth/me');
            const data = await res.json();
            if (data.success && data.data) {
                this.currentUser = data.data;
                this.redirectUserBasedOnRole();
            } else {
                this.switchView('authView');
            }
        } catch (e) {
            this.switchView('authView');
        }
    }

    async handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            const data = await res.json();

            if (data.success) {
                this.currentUser = data.data;
                this.showToast(`Welcome back, ${this.currentUser.fullName}!`);
                this.redirectUserBasedOnRole();
            } else {
                this.showToast(data.message, 'danger');
            }
        } catch (err) {
            this.showToast('Login failed. Network error.', 'danger');
        }
    }

    async handleRegister(e) {
        e.preventDefault();
        const request = {
            fullName: document.getElementById('regFullName').value,
            email: document.getElementById('regEmail').value,
            username: document.getElementById('regUsername').value,
            password: document.getElementById('regPassword').value,
            role: document.getElementById('regRole').value
        };

        try {
            const res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(request)
            });
            const data = await res.json();

            if (data.success) {
                this.showToast('Registration successful! Please login.', 'success');
                this.switchAuthTab('login');
            } else {
                this.showToast(data.message, 'danger');
            }
        } catch (err) {
            this.showToast('Registration failed.', 'danger');
        }
    }

    async logout() {
        await fetch('/api/auth/logout', { method: 'POST' });
        this.currentUser = null;
        this.stopExamTimer();
        this.switchView('authView');
        this.showToast('Logged out successfully.');
    }

    redirectUserBasedOnRole() {
        if (this.currentUser.role === 'ROLE_ADMIN') {
            this.loadAdminDashboard();
            this.switchView('adminDashboardView');
        } else {
            this.loadStudentPortal();
            this.switchView('studentPortalView');
        }
    }

    // --- STUDENT PORTAL ---

    async loadStudentPortal() {
        try {
            const [examsRes, historyRes] = await Promise.all([
                fetch('/api/student/exams'),
                fetch('/api/student/attempts/history')
            ]);
            const examsData = await examsRes.json();
            const historyData = await historyRes.json();

            if (examsData.success) {
                this.renderAvailableExams(examsData.data);
            }
            if (historyData.success) {
                this.renderStudentHistory(historyData.data);
            }
        } catch (err) {
            this.showToast('Failed to load portal data.', 'danger');
        }
    }

    renderAvailableExams(exams) {
        const container = document.getElementById('availableExamsGrid');
        document.getElementById('studentAvailCount').innerText = exams.length;
        
        if (exams.length === 0) {
            container.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 40px;" class="glass-card">No published exams available at the moment.</div>`;
            return;
        }

        container.innerHTML = exams.map(exam => `
            <div class="glass-card exam-card">
                <div>
                    <div class="exam-card-header">
                        <span class="badge badge-primary">${exam.subject}</span>
                        <span class="badge badge-success"><i class="fa-regular fa-clock"></i> ${exam.durationMinutes} Mins</span>
                    </div>
                    <h3 style="margin-bottom: 8px;">${exam.title}</h3>
                    <div class="exam-card-body">
                        <p>${exam.description || 'No description available.'}</p>
                    </div>
                </div>
                
                <div>
                    <div class="exam-meta">
                        <span><i class="fa-solid fa-circle-question"></i> ${exam.questionCount} Questions</span>
                        <span><i class="fa-solid fa-bullseye"></i> ${exam.totalMarks} Marks</span>
                    </div>
                    <button class="btn btn-primary" style="width: 100%;" onclick="app.startExam(${exam.id})">
                        <i class="fa-solid fa-play"></i> Start Examination
                    </button>
                </div>
            </div>
        `).join('');
    }

    renderStudentHistory(attempts) {
        const tbody = document.getElementById('studentHistoryTable');
        document.getElementById('studentCompletedCount').innerText = attempts.length;

        if (attempts.length > 0) {
            const avg = attempts.reduce((acc, a) => acc + a.percentage, 0) / attempts.length;
            document.getElementById('studentAvgScore').innerText = `${Math.round(avg)}%`;
        }

        if (attempts.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No prior attempt records found.</td></tr>`;
            return;
        }

        tbody.innerHTML = attempts.map(att => `
            <tr>
                <td>#EXAM-${att.attemptId}</td>
                <td><strong>${att.examTitle}</strong></td>
                <td>${att.subject}</td>
                <td>${att.scoreObtained} / ${att.totalMarks}</td>
                <td><strong>${att.percentage}%</strong></td>
                <td>
                    <span class="badge ${att.passed ? 'badge-success' : 'badge-danger'}">
                        ${att.passed ? 'PASSED' : 'FAILED'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="app.viewAttemptResult(${att.attemptId})">
                        <i class="fa-solid fa-eye"></i> Scorecard
                    </button>
                    <button class="btn btn-primary btn-sm" onclick="app.downloadPdfDirect(${att.attemptId})">
                        <i class="fa-solid fa-file-pdf"></i> PDF
                    </button>
                </td>
            </tr>
        `).join('');
    }

    // --- EXAM ENGINE & TIMER ---

    async startExam(examId) {
        try {
            const res = await fetch(`/api/student/exams/${examId}/start`, { method: 'POST' });
            const data = await res.json();

            if (!data.success) {
                this.showToast(data.message, 'danger');
                return;
            }

            this.currentAttempt = data.data;
            this.currentExam = this.currentAttempt.exam;

            // Fetch questions
            const qRes = await fetch(`/api/student/exams/${examId}/questions`);
            const qData = await qRes.json();

            if (qData.success && qData.data.length > 0) {
                this.questions = qData.data;
                this.currentQIndex = 0;
                this.userAnswers = {};
                this.reviewMarks = {};
                this.violationCount = 0;

                document.getElementById('examWorkspaceTitle').innerText = this.currentExam.title;
                document.getElementById('examWorkspaceSubject').innerText = this.currentExam.subject;

                this.switchView('examWorkspaceView');
                this.renderQuestion(0);
                this.renderQuestionPalette();
                this.startExamTimer(this.currentExam.durationMinutes);
                this.showToast('Exam started. Security tracking active.', 'success');
            } else {
                this.showToast('This exam has no questions added yet.', 'danger');
            }
        } catch (err) {
            this.showToast('Failed to initialize exam workspace.', 'danger');
        }
    }

    startExamTimer(durationMinutes) {
        this.stopExamTimer();
        this.remainingSeconds = durationMinutes * 60;
        const clockEl = document.getElementById('examTimerClock');

        const updateClock = () => {
            if (this.remainingSeconds <= 0) {
                this.stopExamTimer();
                clockEl.innerText = "00:00";
                this.showToast("Time's up! Auto-submitting exam...", 'danger');
                this.submitExam(true);
                return;
            }

            const mins = Math.floor(this.remainingSeconds / 60);
            const secs = this.remainingSeconds % 60;
            clockEl.innerText = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;

            if (this.remainingSeconds < 120) {
                clockEl.classList.add('warning');
            } else {
                clockEl.classList.remove('warning');
            }

            this.remainingSeconds--;
        };

        updateClock();
        this.timerInterval = setInterval(updateClock, 1000);
    }

    stopExamTimer() {
        if (this.timerInterval) {
            clearInterval(this.timerInterval);
            this.timerInterval = null;
        }
    }

    renderQuestion(index) {
        if (index < 0 || index >= this.questions.length) return;
        this.currentQIndex = index;

        const q = this.questions[index];
        document.getElementById('qNumberBadge').innerText = `Question ${index + 1} of ${this.questions.length}`;
        document.getElementById('qMarksBadge').innerText = `Marks: ${q.marks}`;
        document.getElementById('qTextDisplay').innerText = q.questionText;

        const isMulti = q.type === 'MULTIPLE_CHOICE';
        const inputType = isMulti ? 'checkbox' : 'radio';
        const selectedSet = this.userAnswers[q.id] || new Set();

        const optionsContainer = document.getElementById('optionsContainer');
        optionsContainer.innerHTML = q.options.map(opt => {
            const isChecked = selectedSet.has(opt.id);
            return `
                <div class="option-item ${isChecked ? 'selected' : ''}" onclick="app.selectOption(${opt.id}, '${inputType}')">
                    <input type="${inputType}" name="qOption" value="${opt.id}" ${isChecked ? 'checked' : ''} onclick="event.stopPropagation(); app.selectOption(${opt.id}, '${inputType}')">
                    <span>${opt.optionText}</span>
                </div>
            `;
        }).join('');

        // Navigation controls state
        document.getElementById('btnPrevQ').disabled = (index === 0);
        document.getElementById('btnNextQ').disabled = (index === this.questions.length - 1);

        const isReview = !!this.reviewMarks[q.id];
        const reviewBtn = document.getElementById('btnMarkReview');
        reviewBtn.classList.toggle('btn-warning', isReview);
        reviewBtn.innerHTML = isReview ? `<i class="fa-solid fa-bookmark"></i> Marked for Review` : `<i class="fa-regular fa-bookmark"></i> Mark for Review`;

        this.renderQuestionPalette();
    }

    selectOption(optId, inputType) {
        const q = this.questions[this.currentQIndex];
        if (!this.userAnswers[q.id]) {
            this.userAnswers[q.id] = new Set();
        }

        if (inputType === 'radio') {
            this.userAnswers[q.id].clear();
            this.userAnswers[q.id].add(optId);
        } else {
            if (this.userAnswers[q.id].has(optId)) {
                this.userAnswers[q.id].delete(optId);
            } else {
                this.userAnswers[q.id].add(optId);
            }
        }

        this.renderQuestion(this.currentQIndex);
        this.saveAnswerToServer(q.id);
    }

    toggleMarkForReview() {
        const q = this.questions[this.currentQIndex];
        this.reviewMarks[q.id] = !this.reviewMarks[q.id];
        this.renderQuestion(this.currentQIndex);
        this.saveAnswerToServer(q.id);
    }

    async saveAnswerToServer(qId) {
        if (!this.currentAttempt) return;
        const selectedOptionIds = Array.from(this.userAnswers[qId] || []);
        const isMarkedForReview = !!this.reviewMarks[qId];

        try {
            await fetch(`/api/student/attempts/${this.currentAttempt.id}/save-answer`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ questionId: qId, selectedOptionIds, isMarkedForReview })
            });
        } catch (e) {
            console.error("Save answer sync failed", e);
        }
    }

    navigateQuestion(direction) {
        this.renderQuestion(this.currentQIndex + direction);
    }

    renderQuestionPalette() {
        const grid = document.getElementById('paletteGrid');
        grid.innerHTML = this.questions.map((q, idx) => {
            const isCurrent = idx === this.currentQIndex;
            const hasAnswer = this.userAnswers[q.id] && this.userAnswers[q.id].size > 0;
            const isReview = !!this.reviewMarks[q.id];

            let statusClass = '';
            if (isCurrent) statusClass = 'current';
            else if (isReview) statusClass = 'review';
            else if (hasAnswer) statusClass = 'answered';

            return `
                <div class="palette-btn ${statusClass}" onclick="app.renderQuestion(${idx})">
                    ${idx + 1}
                </div>
            `;
        }).join('');
    }

    // --- ANTI-CHEAT MONITORING ---

    setupAntiCheatListeners() {
        const triggerViolation = () => {
            if (this.activeView === 'examWorkspaceView' && this.currentAttempt) {
                this.violationCount++;
                fetch(`/api/student/attempts/${this.currentAttempt.id}/log-violation`, { method: 'POST' });
                
                document.getElementById('antiCheatCountBadge').innerText = `Violation Warning ${this.violationCount} of 3`;
                document.getElementById('antiCheatModal').classList.add('active');

                if (this.violationCount >= 3) {
                    this.showToast('Maximum anti-cheat violations reached! Auto-submitting exam.', 'danger');
                    this.submitExam(true);
                }
            }
        };

        window.addEventListener('blur', triggerViolation);
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) triggerViolation();
        });
    }

    dismissAntiCheatModal() {
        document.getElementById('antiCheatModal').classList.remove('active');
    }

    confirmSubmitExam() {
        if (confirm("Are you sure you want to finalize and submit your examination?")) {
            this.submitExam(false);
        }
    }

    async submitExam(isTimeout = false) {
        this.stopExamTimer();
        if (!this.currentAttempt) return;

        try {
            const res = await fetch(`/api/student/attempts/${this.currentAttempt.id}/submit?isTimeout=${isTimeout}`, {
                method: 'POST'
            });
            const data = await res.json();

            if (data.success) {
                this.currentAttempt = null;
                this.showAttemptResult(data.data);
            } else {
                this.showToast(data.message, 'danger');
            }
        } catch (e) {
            this.showToast('Error submitting exam.', 'danger');
        }
    }

    // --- SCORECARD & RESULTS ---

    async viewAttemptResult(attemptId) {
        try {
            const res = await fetch(`/api/student/attempts/${attemptId}/result`);
            const data = await res.json();
            if (data.success) {
                this.showAttemptResult(data.data);
            }
        } catch (e) {
            this.showToast('Could not load attempt scorecard.', 'danger');
        }
    }

    showAttemptResult(result) {
        this.latestResult = result;
        
        const badge = document.getElementById('resultStatusBadge');
        badge.className = `badge ${result.passed ? 'badge-success' : 'badge-danger'}`;
        badge.innerText = result.passed ? 'PASSED' : 'FAILED';

        document.getElementById('resultExamTitle').innerText = result.examTitle;
        document.getElementById('resultStudentMeta').innerText = `Candidate: ${result.studentName} | Subject: ${result.subject}`;
        document.getElementById('resultScoreDisplay').innerText = `${result.scoreObtained.toFixed(1)} / ${result.totalMarks.toFixed(1)}`;
        document.getElementById('resultPercentDisplay').innerText = `${result.percentage}% Grade Score`;

        document.getElementById('resCorrectCount').innerText = result.correctQuestions;
        document.getElementById('resIncorrectCount').innerText = result.incorrectQuestions;
        document.getElementById('resUnattemptedCount').innerText = result.unattemptedQuestions;

        // Question breakdown
        const breakdownContainer = document.getElementById('resultQuestionBreakdown');
        breakdownContainer.innerHTML = result.questionResults.map((q, idx) => `
            <div class="glass-card" style="padding: 20px; margin-bottom: 16px; border-left: 4px solid ${q.correct ? 'var(--success)' : 'var(--danger)'};">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                    <span style="font-weight: 700; font-size: 0.95rem;">Q${idx + 1}. ${q.questionText}</span>
                    <span class="badge ${q.correct ? 'badge-success' : 'badge-danger'}">
                        ${q.marksAwarded} / ${q.marks} Marks
                    </span>
                </div>

                <div style="display: flex; flex-direction: column; gap: 6px; margin: 12px 0;">
                    ${q.options.map(opt => {
                        const isSelected = q.selectedOptionIds && q.selectedOptionIds.includes(opt.id);
                        let bg = 'transparent';
                        let border = 'var(--border-color)';
                        if (opt.correct) { bg = 'rgba(16, 185, 129, 0.15)'; border = 'var(--success)'; }
                        else if (isSelected && !opt.correct) { bg = 'rgba(239, 68, 68, 0.15)'; border = 'var(--danger)'; }

                        return `
                            <div style="padding: 8px 12px; border-radius: 6px; background: ${bg}; border: 1px solid ${border}; font-size: 0.85rem;">
                                ${isSelected ? '<b>[Your Choice]</b> ' : ''}${opt.optionText} ${opt.correct ? ' ✓ (Correct Answer)' : ''}
                            </div>
                        `;
                    }).join('')}
                </div>

                ${q.explanation ? `<div style="font-size: 0.8rem; color: #a5b4fc; background: rgba(99, 102, 241, 0.1); padding: 8px 12px; border-radius: 6px;">💡 <b>Explanation:</b> ${q.explanation}</div>` : ''}
            </div>
        `).join('');

        this.switchView('resultView');
    }

    downloadResultPdf() {
        if (this.latestResult) {
            this.downloadPdfDirect(this.latestResult.attemptId);
        }
    }

    downloadPdfDirect(attemptId) {
        window.open(`/api/student/attempts/${attemptId}/pdf`, '_blank');
    }

    returnToDashboard() {
        this.redirectUserBasedOnRole();
    }

    // --- ADMIN DASHBOARD ---

    async loadAdminDashboard() {
        try {
            const [statsRes, examsRes] = await Promise.all([
                fetch('/api/admin/stats'),
                fetch('/api/admin/exams')
            ]);
            const stats = (await statsRes.json()).data;
            const exams = (await examsRes.json()).data;

            document.getElementById('adminTotalStudents').innerText = stats.totalStudents;
            document.getElementById('adminTotalExams').innerText = stats.totalExams;
            document.getElementById('adminPassRate').innerText = `${stats.totalAttempts > 0 ? Math.round((stats.passedAttempts / stats.totalAttempts) * 100) : 0}%`;
            document.getElementById('adminClassAvg').innerText = `${stats.averagePercentage}%`;

            this.renderAdminExamsTable(exams);
            this.renderAdminChart(stats);
        } catch (e) {
            this.showToast('Failed to load admin metrics.', 'danger');
        }
    }

    renderAdminChart(stats) {
        const ctx = document.getElementById('adminPerformanceChart').getContext('2d');
        if (this.perfChart) this.perfChart.destroy();

        this.perfChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Passed Attempts', 'Failed Attempts'],
                datasets: [{
                    label: 'Count',
                    data: [stats.passedAttempts, stats.failedAttempts],
                    backgroundColor: ['rgba(16, 185, 129, 0.7)', 'rgba(239, 68, 68, 0.7)'],
                    borderRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    renderAdminExamsTable(exams) {
        const tbody = document.getElementById('adminExamsTable');
        if (exams.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No exams created yet.</td></tr>`;
            return;
        }

        tbody.innerHTML = exams.map(e => `
            <tr>
                <td>#${e.id}</td>
                <td><strong>${e.title}</strong></td>
                <td>${e.subject}</td>
                <td>${e.durationMinutes} Mins</td>
                <td>${e.questionCount} Questions</td>
                <td>
                    <span class="badge ${e.status === 'PUBLISHED' ? 'badge-success' : 'badge-warning'}">
                        ${e.status}
                    </span>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="app.toggleExamPublish(${e.id}, '${e.status}')">
                        ${e.status === 'PUBLISHED' ? 'Unpublish' : 'Publish'}
                    </button>
                    <button class="btn btn-primary btn-sm" onclick="app.openQuestionModal(${e.id}, '${e.title}')">
                        <i class="fa-solid fa-layer-group"></i> Questions
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="app.deleteExam(${e.id})">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    }

    async handleCreateExam(e) {
        e.preventDefault();
        const payload = {
            title: document.getElementById('newExamTitle').value,
            subject: document.getElementById('newExamSubject').value,
            durationMinutes: parseInt(document.getElementById('newExamDuration').value),
            passMarks: parseFloat(document.getElementById('newExamPassMarks').value),
            description: document.getElementById('newExamDesc').value,
            totalMarks: 20.0,
            status: 'DRAFT'
        };

        try {
            const res = await fetch('/api/admin/exams', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                this.showToast('Exam draft created!');
                this.loadAdminDashboard();
            } else {
                this.showToast(data.message, 'danger');
            }
        } catch (err) {
            this.showToast('Error creating exam.', 'danger');
        }
    }

    async toggleExamPublish(id, currentStatus) {
        const newStatus = currentStatus === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED';
        try {
            const examRes = await fetch(`/api/admin/exams`);
            const exams = (await examRes.json()).data;
            const target = exams.find(e => e.id === id);
            if (!target) return;

            target.status = newStatus;
            const res = await fetch(`/api/admin/exams/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(target)
            });
            if ((await res.json()).success) {
                this.showToast(`Exam status updated to ${newStatus}`);
                this.loadAdminDashboard();
            }
        } catch (e) {
            this.showToast('Failed to update status.', 'danger');
        }
    }

    async deleteExam(id) {
        if (confirm("Are you sure you want to delete this exam?")) {
            await fetch(`/api/admin/exams/${id}`, { method: 'DELETE' });
            this.showToast('Exam deleted.');
            this.loadAdminDashboard();
        }
    }

    // --- QUESTION BANK MODAL ---

    async openQuestionModal(examId, title) {
        this.activeModalExamId = examId;
        document.getElementById('modalExamTitle').innerText = `Questions for: ${title}`;
        document.getElementById('questionModal').classList.add('active');
        this.loadModalQuestions();
    }

    closeModal() {
        document.getElementById('questionModal').classList.remove('active');
    }

    handleQTypeChange() {
        const type = document.getElementById('qInputType').value;
        const container = document.getElementById('modalOptionsList');

        if (type === 'TRUE_FALSE') {
            container.innerHTML = `
                <div style="display: flex; gap: 10px; align-items: center;">
                    <input type="radio" name="tfCorrect" value="0" checked> Option A: True
                </div>
                <div style="display: flex; gap: 10px; align-items: center;">
                    <input type="radio" name="tfCorrect" value="1"> Option B: False
                </div>
            `;
        } else {
            container.innerHTML = `
                <div style="display: flex; gap: 10px; align-items: center; margin-bottom: 6px;">
                    <input type="radio" name="mcqCorrect" value="0" checked>
                    <input type="text" class="form-control opt-text" placeholder="Option 1..." required>
                </div>
                <div style="display: flex; gap: 10px; align-items: center; margin-bottom: 6px;">
                    <input type="radio" name="mcqCorrect" value="1">
                    <input type="text" class="form-control opt-text" placeholder="Option 2..." required>
                </div>
                <div style="display: flex; gap: 10px; align-items: center; margin-bottom: 6px;">
                    <input type="radio" name="mcqCorrect" value="2">
                    <input type="text" class="form-control opt-text" placeholder="Option 3..." required>
                </div>
                <div style="display: flex; gap: 10px; align-items: center;">
                    <input type="radio" name="mcqCorrect" value="3">
                    <input type="text" class="form-control opt-text" placeholder="Option 4..." required>
                </div>
            `;
        }
    }

    async handleAddQuestion(e) {
        e.preventDefault();
        const type = document.getElementById('qInputType').value;
        const text = document.getElementById('qInputText').value;
        const marks = parseFloat(document.getElementById('qInputMarks').value);
        const explanation = document.getElementById('qInputExplanation').value;

        let options = [];
        if (type === 'TRUE_FALSE') {
            const selectedVal = document.querySelector('input[name="tfCorrect"]:checked').value;
            options = [
                { optionText: 'True', correct: selectedVal === '0' },
                { optionText: 'False', correct: selectedVal === '1' }
            ];
        } else {
            const selectedIdx = parseInt(document.querySelector('input[name="mcqCorrect"]:checked').value);
            const optInputs = document.querySelectorAll('.opt-text');
            optInputs.forEach((inp, idx) => {
                if (inp.value.trim()) {
                    options.push({ optionText: inp.value.trim(), correct: idx === selectedIdx });
                }
            });
        }

        const payload = { questionText: text, type, marks, explanation, options };

        try {
            const res = await fetch(`/api/admin/exams/${this.activeModalExamId}/questions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if ((await res.json()).success) {
                this.showToast('Question added!');
                document.getElementById('qInputText').value = '';
                this.loadModalQuestions();
                this.loadAdminDashboard();
            }
        } catch (err) {
            this.showToast('Failed to save question.', 'danger');
        }
    }

    async loadModalQuestions() {
        try {
            const res = await fetch(`/api/admin/exams/${this.activeModalExamId}/questions`);
            const questions = (await res.json()).data;
            const container = document.getElementById('modalQuestionsList');

            if (questions.length === 0) {
                container.innerHTML = `<p style="color: var(--text-muted); font-size: 0.85rem;">No questions added yet.</p>`;
                return;
            }

            container.innerHTML = questions.map((q, idx) => `
                <div style="background: rgba(15, 23, 42, 0.6); padding: 12px; border-radius: 8px; border: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <strong>Q${idx + 1}. ${q.questionText}</strong>
                        <div style="font-size: 0.75rem; color: var(--text-muted);">${q.type} | ${q.marks} Marks</div>
                    </div>
                    <button class="btn btn-danger btn-sm" onclick="app.deleteQuestion(${q.id})"><i class="fa-solid fa-trash"></i></button>
                </div>
            `).join('');
        } catch (e) {
            console.error("Modal questions error", e);
        }
    }

    async deleteQuestion(qId) {
        if (confirm("Delete this question?")) {
            await fetch(`/api/admin/questions/${qId}`, { method: 'DELETE' });
            this.showToast('Question deleted.');
            this.loadModalQuestions();
            this.loadAdminDashboard();
        }
    }
}

// Global App Initialization
const app = new ExamiQApp();
