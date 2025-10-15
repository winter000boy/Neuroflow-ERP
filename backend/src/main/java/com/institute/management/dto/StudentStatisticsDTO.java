package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * DTO for student statistics
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentStatisticsDTO {
    
    private long totalStudents;
    private long activeStudents;
    private long graduatedStudents;
    private long droppedOutStudents;
    private long suspendedStudents;
    private long inactiveStudents;
    private long studentsWithoutBatch;
    private long placedGraduates;
    private long unplacedGraduates;
    private Map<String, Long> enrollmentsByMonth;
    private Map<String, Long> graduationsByMonth;
    private Map<String, Long> studentsByBatch;
    private Map<String, Long> studentsByCourse;
    private Map<String, Long> gradeDistribution;
    
    // Constructors
    public StudentStatisticsDTO() {}
    
    // Getters and Setters
    public long getTotalStudents() {
        return totalStudents;
    }
    
    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }
    
    public long getActiveStudents() {
        return activeStudents;
    }
    
    public void setActiveStudents(long activeStudents) {
        this.activeStudents = activeStudents;
    }
    
    public long getGraduatedStudents() {
        return graduatedStudents;
    }
    
    public void setGraduatedStudents(long graduatedStudents) {
        this.graduatedStudents = graduatedStudents;
    }
    
    public long getDroppedOutStudents() {
        return droppedOutStudents;
    }
    
    public void setDroppedOutStudents(long droppedOutStudents) {
        this.droppedOutStudents = droppedOutStudents;
    }
    
    public long getSuspendedStudents() {
        return suspendedStudents;
    }
    
    public void setSuspendedStudents(long suspendedStudents) {
        this.suspendedStudents = suspendedStudents;
    }
    
    public long getInactiveStudents() {
        return inactiveStudents;
    }
    
    public void setInactiveStudents(long inactiveStudents) {
        this.inactiveStudents = inactiveStudents;
    }
    
    public long getStudentsWithoutBatch() {
        return studentsWithoutBatch;
    }
    
    public void setStudentsWithoutBatch(long studentsWithoutBatch) {
        this.studentsWithoutBatch = studentsWithoutBatch;
    }
    
    public long getPlacedGraduates() {
        return placedGraduates;
    }
    
    public void setPlacedGraduates(long placedGraduates) {
        this.placedGraduates = placedGraduates;
    }
    
    public long getUnplacedGraduates() {
        return unplacedGraduates;
    }
    
    public void setUnplacedGraduates(long unplacedGraduates) {
        this.unplacedGraduates = unplacedGraduates;
    }
    
    public Map<String, Long> getEnrollmentsByMonth() {
        return enrollmentsByMonth;
    }
    
    public void setEnrollmentsByMonth(Map<String, Long> enrollmentsByMonth) {
        this.enrollmentsByMonth = enrollmentsByMonth;
    }
    
    public Map<String, Long> getGraduationsByMonth() {
        return graduationsByMonth;
    }
    
    public void setGraduationsByMonth(Map<String, Long> graduationsByMonth) {
        this.graduationsByMonth = graduationsByMonth;
    }
    
    public Map<String, Long> getStudentsByBatch() {
        return studentsByBatch;
    }
    
    public void setStudentsByBatch(Map<String, Long> studentsByBatch) {
        this.studentsByBatch = studentsByBatch;
    }
    
    public Map<String, Long> getStudentsByCourse() {
        return studentsByCourse;
    }
    
    public void setStudentsByCourse(Map<String, Long> studentsByCourse) {
        this.studentsByCourse = studentsByCourse;
    }
    
    public Map<String, Long> getGradeDistribution() {
        return gradeDistribution;
    }
    
    public void setGradeDistribution(Map<String, Long> gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }
}