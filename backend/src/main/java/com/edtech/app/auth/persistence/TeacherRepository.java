package com.edtech.app.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<TeacherEntity, String> {

    Optional<TeacherEntity> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
