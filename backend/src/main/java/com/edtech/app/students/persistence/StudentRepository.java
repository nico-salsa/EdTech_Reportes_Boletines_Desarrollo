package com.edtech.app.students.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, String> {

    Optional<StudentEntity> findByStudentIdentifier(String studentIdentifier);

    boolean existsByStudentIdentifier(String studentIdentifier);
}
