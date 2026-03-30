package com.edtech.app.evaluationprogram.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "evaluation_activities")
public class EvaluationActivityEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String name;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(nullable = false)
    private int position;

    protected EvaluationActivityEntity() {
    }

    public EvaluationActivityEntity(String id, String courseId, String name, double weight, int position) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.weight = weight;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public int getPosition() {
        return position;
    }
}
