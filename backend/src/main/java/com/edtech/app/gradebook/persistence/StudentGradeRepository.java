package com.edtech.app.gradebook.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentGradeRepository extends JpaRepository<StudentGradeEntity, String> {

    List<StudentGradeEntity> findAllByCourseId(String courseId);

    boolean existsByCourseIdAndStudentIdAndActivityId(String courseId, String studentId, String activityId);
}
