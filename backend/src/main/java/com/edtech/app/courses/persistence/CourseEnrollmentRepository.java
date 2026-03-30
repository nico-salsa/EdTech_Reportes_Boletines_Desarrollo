package com.edtech.app.courses.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollmentEntity, String> {

    List<CourseEnrollmentEntity> findAllByCourseId(String courseId);

    boolean existsByCourseIdAndStudentId(String courseId, String studentId);
}
