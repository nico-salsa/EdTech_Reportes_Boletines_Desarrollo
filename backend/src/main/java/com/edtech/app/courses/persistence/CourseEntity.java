package com.edtech.app.courses.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class CourseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    protected CourseEntity() {
    }

    public CourseEntity(String id, String teacherId, String name, String createdAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
