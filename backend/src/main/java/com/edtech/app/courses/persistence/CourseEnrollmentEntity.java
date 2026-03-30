package com.edtech.app.courses.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_students")
public class CourseEnrollmentEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    protected CourseEnrollmentEntity() {
    }

    public CourseEnrollmentEntity(String id, String courseId, String studentId, String createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
        return createdAt;
    }
}
