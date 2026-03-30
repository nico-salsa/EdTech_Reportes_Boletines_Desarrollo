package com.edtech.app.evaluationprogram.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationActivityRepository extends JpaRepository<EvaluationActivityEntity, String> {

    List<EvaluationActivityEntity> findAllByCourseIdOrderByPositionAsc(String courseId);

    boolean existsByCourseIdAndNameIgnoreCase(String courseId, String name);
}
