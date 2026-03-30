package com.edtech.app.students.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "student_identifier", nullable = false, unique = true)
    private String studentIdentifier;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    protected StudentEntity() {
    }

    public StudentEntity(String id, String studentIdentifier, String fullName, String email, String createdAt) {
        this.id = id;
        this.studentIdentifier = studentIdentifier;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getStudentIdentifier() {
        return studentIdentifier;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
