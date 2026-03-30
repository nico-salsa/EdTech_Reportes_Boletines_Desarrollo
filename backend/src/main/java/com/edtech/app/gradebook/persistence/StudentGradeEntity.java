package com.edtech.app.gradebook.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_grades")
public class StudentGradeEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "activity_id", nullable = false)
    private String activityId;

    @Column
    private Double grade;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    protected StudentGradeEntity() {
    }

    public StudentGradeEntity(String id, String courseId, String studentId, String activityId, Double grade, String createdAt, String updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.activityId = activityId;
        this.grade = grade;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getActivityId() {
        return activityId;
    }

    public Double getGrade() {
        return grade;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
