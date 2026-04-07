package com.edtech.app.auth;

import com.edtech.app.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public SessionResponse register(String username, String password) {
        String normalizedUsername = normalizeUsername(username);
        validatePassword(password);

        Integer exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teachers WHERE lower(username) = lower(?)",
                Integer.class,
                normalizedUsername
        );

        if (exists != null && exists > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }

        String teacherId = UUID.randomUUID().toString();
        String createdAt = Instant.now().toString();
        jdbcTemplate.update(
                "INSERT INTO teachers(id, username, password_hash, created_at) VALUES (?, ?, ?, ?)",
                teacherId,
                normalizedUsername,
                passwordEncoder.encode(password),
                createdAt
        );

        return createSession(teacherId, normalizedUsername);
    }

    @Transactional
    public SessionResponse login(String username, String password) {
        String normalizedUsername = normalizeUsername(username);
        validatePassword(password);

        List<TeacherRecord> teachers = jdbcTemplate.query(
                "SELECT id, username, password_hash FROM teachers WHERE lower(username) = lower(?)",
                (rs, rowNum) -> new TeacherRecord(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password_hash")
                ),
                normalizedUsername
        );

        if (teachers.isEmpty() || !passwordEncoder.matches(password, teachers.getFirst().passwordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        TeacherRecord teacher = teachers.getFirst();
        jdbcTemplate.update("DELETE FROM sessions WHERE teacher_id = ?", teacher.id());
        return createSession(teacher.id(), teacher.username());
    }

    public SessionResponse getSession(String token) {
        String normalizedToken = requireToken(token);
        List<SessionUserRecord> sessions = jdbcTemplate.query(
                """
                SELECT s.token, t.id AS teacher_id, t.username
                FROM sessions s
                JOIN teachers t ON t.id = s.teacher_id
                WHERE s.token = ?
                """,
                (rs, rowNum) -> new SessionUserRecord(
                        rs.getString("token"),
                        rs.getString("teacher_id"),
                        rs.getString("username")
                ),
                normalizedToken
        );

        if (sessions.isEmpty()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Sesion invalida o expirada");
        }

        SessionUserRecord session = sessions.getFirst();
        return new SessionResponse(new UserResponse(session.teacherId(), session.username()), session.token());
    }

    @Transactional
    public void logout(String token) {
        String normalizedToken = requireToken(token);
        int deleted = jdbcTemplate.update("DELETE FROM sessions WHERE token = ?", normalizedToken);
        if (deleted == 0) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Sesion invalida o expirada");
        }
    }

    public UserResponse requireUser(String token) {
        return getSession(token).user();
    }

    private SessionResponse createSession(String teacherId, String username) {
        String token = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO sessions(token, teacher_id, created_at) VALUES (?, ?, ?)",
                token,
                teacherId,
                Instant.now().toString()
        );
        return new SessionResponse(new UserResponse(teacherId, username), token);
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El nombre de usuario es obligatorio");
        }
        return username.trim();
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La contrasena es obligatoria");
        }
    }

    private String requireToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Falta el token de sesion");
        }
        return token.trim();
    }

    private record TeacherRecord(String id, String username, String passwordHash) {
    }

    private record SessionUserRecord(String token, String teacherId, String username) {
    }

    public record UserResponse(String id, String username) {
    }

    public record SessionResponse(UserResponse user, String token) {
    }
}
