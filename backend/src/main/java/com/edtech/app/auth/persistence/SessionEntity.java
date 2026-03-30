package com.edtech.app.auth.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String token;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    protected SessionEntity() {
    }

    public SessionEntity(String token, String teacherId, String createdAt) {
        this.token = token;
        this.teacherId = teacherId;
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
