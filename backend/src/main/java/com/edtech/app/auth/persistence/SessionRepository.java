package com.edtech.app.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, String> {

    Optional<SessionEntity> findByToken(String token);

    List<SessionEntity> findByTeacherId(String teacherId);
}
