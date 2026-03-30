package com.edtech.app.courses.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, String> {

    List<CourseEntity> findAllByTeacherIdOrderByCreatedAtDesc(String teacherId);

    Optional<CourseEntity> findByIdAndTeacherId(String id, String teacherId);

    boolean existsByTeacherIdAndNameIgnoreCase(String teacherId, String name);
}
