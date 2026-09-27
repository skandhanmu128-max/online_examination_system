package com.examiq.onlineexam.dto;

public class DashboardStatsDto {
    private long totalStudents;
    private long totalExams;
    private long publishedExams;
    private long totalAttempts;
    private long passedAttempts;
    private long failedAttempts;
    private double averagePercentage;

    public DashboardStatsDto() {}

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalExams() { return totalExams; }
    public void setTotalExams(long totalExams) { this.totalExams = totalExams; }

    public long getPublishedExams() { return publishedExams; }
    public void setPublishedExams(long publishedExams) { this.publishedExams = publishedExams; }

    public long getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(long totalAttempts) { this.totalAttempts = totalAttempts; }

    public long getPassedAttempts() { return passedAttempts; }
    public void setPassedAttempts(long passedAttempts) { this.passedAttempts = passedAttempts; }

    public long getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(long failedAttempts) { this.failedAttempts = failedAttempts; }

    public double getAveragePercentage() { return averagePercentage; }
    public void setAveragePercentage(double averagePercentage) { this.averagePercentage = averagePercentage; }
}
