package com.edtech.app.auth;

import com.edtech.app.common.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthService}.
 *
 * <p>Why: AuthService is the core authentication module. Every path (register, login, session
 * validation, logout) must be tested in isolation to guarantee that credential handling,
 * token management, and error responses behave correctly.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(jdbcTemplate);
    }

    // ─── REGISTER ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("should create a new teacher and return a session when username is unique")
        void shouldRegisterSuccessfully() {
            // Given: no existing teacher with the same username
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM teachers WHERE lower(username) = lower(?)"),
                    eq(Integer.class),
                    anyString()
            )).thenReturn(0);

            // When
            AuthService.SessionResponse response = authService.register("profesor1", "securePass123");

            // Then: a teacher row was inserted
            ArgumentCaptor<Object[]> teacherCaptor = ArgumentCaptor.forClass(Object[].class);
            verify(jdbcTemplate).update(
                    eq("INSERT INTO teachers(id, username, password_hash, created_at) VALUES (?, ?, ?, ?)"),
                    anyString(),    // id (UUID)
                    eq("profesor1"),// normalized username
                    anyString(),   // BCrypt hash
                    anyString()    // createdAt
            );

            // Then: a session row was inserted
            verify(jdbcTemplate).update(
                    eq("INSERT INTO sessions(token, teacher_id, created_at) VALUES (?, ?, ?)"),
                    anyString(),   // token (UUID)
                    anyString(),   // teacher id
                    anyString()    // createdAt
            );

            // Then: response contains user data + token
            assertThat(response.user().username()).isEqualTo("profesor1");
            assertThat(response.user().id()).isNotBlank();
            assertThat(response.token()).isNotBlank();
        }

        @Test
        @DisplayName("should throw CONFLICT when username already exists")
        void shouldRejectDuplicateUsername() {
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM teachers WHERE lower(username) = lower(?)"),
                    eq(Integer.class),
                    anyString()
            )).thenReturn(1);

            assertThatThrownBy(() -> authService.register("profesor1", "securePass123"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        ApiException apiEx = (ApiException) ex;
                        assertThat(apiEx.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                        assertThat(apiEx.getMessage()).contains("ya existe");
                    });

            // Verify no INSERT happened
            verify(jdbcTemplate, never()).update(
                    eq("INSERT INTO teachers(id, username, password_hash, created_at) VALUES (?, ?, ?, ?)"),
                    any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when username is blank")
        void shouldRejectBlankUsername() {
            assertThatThrownBy(() -> authService.register("  ", "password"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when username is null")
        void shouldRejectNullUsername() {
            assertThatThrownBy(() -> authService.register(null, "password"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when password is blank")
        void shouldRejectBlankPassword() {
            assertThatThrownBy(() -> authService.register("user", ""))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when password is null")
        void shouldRejectNullPassword() {
            assertThatThrownBy(() -> authService.register("user", null))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should trim whitespace from username before storing")
        void shouldTrimUsername() {
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM teachers WHERE lower(username) = lower(?)"),
                    eq(Integer.class),
                    anyString()
            )).thenReturn(0);

            AuthService.SessionResponse response = authService.register("  profesor1  ", "pass123");

            assertThat(response.user().username()).isEqualTo("profesor1");
        }
    }

    // ─── LOGIN ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("should authenticate valid credentials and return a new session")
        @SuppressWarnings("unchecked")
        void shouldLoginSuccessfully() {
            // Given: a teacher exists with the matching password
            // BCrypt hash of "password123"
            String bcryptHash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                    .encode("password123");

            when(jdbcTemplate.query(
                    eq("SELECT id, username, password_hash FROM teachers WHERE lower(username) = lower(?)"),
                    any(RowMapper.class),
                    eq("profesor1")
            )).thenAnswer(invocation -> {
                RowMapper<?> mapper = invocation.getArgument(1);
                var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                when(rs.getString("id")).thenReturn("teacher-uuid-1");
                when(rs.getString("username")).thenReturn("profesor1");
                when(rs.getString("password_hash")).thenReturn(bcryptHash);
                return List.of(mapper.mapRow(rs, 0));
            });

            // When
            AuthService.SessionResponse response = authService.login("profesor1", "password123");

            // Then: old sessions deleted
            verify(jdbcTemplate).update(
                    eq("DELETE FROM sessions WHERE teacher_id = ?"),
                    eq("teacher-uuid-1")
            );

            // Then: new session created
            verify(jdbcTemplate).update(
                    eq("INSERT INTO sessions(token, teacher_id, created_at) VALUES (?, ?, ?)"),
                    anyString(),
                    eq("teacher-uuid-1"),
                    anyString()
            );

            assertThat(response.user().id()).isEqualTo("teacher-uuid-1");
            assertThat(response.user().username()).isEqualTo("profesor1");
            assertThat(response.token()).isNotBlank();
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when teacher does not exist")
        @SuppressWarnings("unchecked")
        void shouldRejectNonExistentUser() {
            when(jdbcTemplate.query(
                    anyString(),
                    any(RowMapper.class),
                    anyString()
            )).thenReturn(List.of());

            assertThatThrownBy(() -> authService.login("unknown", "password"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when password is wrong")
        @SuppressWarnings("unchecked")
        void shouldRejectWrongPassword() {
            String bcryptHash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                    .encode("correctPassword");

            when(jdbcTemplate.query(
                    anyString(),
                    any(RowMapper.class),
                    anyString()
            )).thenAnswer(invocation -> {
                RowMapper<?> mapper = invocation.getArgument(1);
                var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                when(rs.getString("id")).thenReturn("teacher-1");
                when(rs.getString("username")).thenReturn("profesor1");
                when(rs.getString("password_hash")).thenReturn(bcryptHash);
                return List.of(mapper.mapRow(rs, 0));
            });

            assertThatThrownBy(() -> authService.login("profesor1", "wrongPassword"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when login username is blank")
        void shouldRejectBlankUsernameOnLogin() {
            assertThatThrownBy(() -> authService.login("", "password"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when login password is blank")
        void shouldRejectBlankPasswordOnLogin() {
            assertThatThrownBy(() -> authService.login("user", ""))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    // ─── GET SESSION ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getSession()")
    class GetSession {

        @Test
        @DisplayName("should return session data when token is valid")
        @SuppressWarnings("unchecked")
        void shouldReturnValidSession() {
            when(jdbcTemplate.query(
                    anyString(),
                    any(RowMapper.class),
                    eq("valid-token")
            )).thenAnswer(invocation -> {
                RowMapper<?> mapper = invocation.getArgument(1);
                var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                when(rs.getString("token")).thenReturn("valid-token");
                when(rs.getString("teacher_id")).thenReturn("teacher-1");
                when(rs.getString("username")).thenReturn("profesor1");
                return List.of(mapper.mapRow(rs, 0));
            });

            AuthService.SessionResponse response = authService.getSession("valid-token");

            assertThat(response.token()).isEqualTo("valid-token");
            assertThat(response.user().id()).isEqualTo("teacher-1");
            assertThat(response.user().username()).isEqualTo("profesor1");
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when token is not found")
        @SuppressWarnings("unchecked")
        void shouldRejectInvalidToken() {
            when(jdbcTemplate.query(
                    anyString(),
                    any(RowMapper.class),
                    anyString()
            )).thenReturn(List.of());

            assertThatThrownBy(() -> authService.getSession("invalid-token"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when token is null")
        void shouldRejectNullToken() {
            assertThatThrownBy(() -> authService.getSession(null))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when token is blank")
        void shouldRejectBlankToken() {
            assertThatThrownBy(() -> authService.getSession("   "))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }
    }

    // ─── LOGOUT ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("logout()")
    class Logout {

        @Test
        @DisplayName("should delete session when token exists")
        void shouldLogoutSuccessfully() {
            when(jdbcTemplate.update(
                    eq("DELETE FROM sessions WHERE token = ?"),
                    eq("valid-token")
            )).thenReturn(1);

            authService.logout("valid-token");

            verify(jdbcTemplate).update(
                    eq("DELETE FROM sessions WHERE token = ?"),
                    eq("valid-token")
            );
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when token does not match any session")
        void shouldRejectLogoutWithInvalidToken() {
            when(jdbcTemplate.update(
                    eq("DELETE FROM sessions WHERE token = ?"),
                    anyString()
            )).thenReturn(0);

            assertThatThrownBy(() -> authService.logout("nonexistent-token"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }

        @Test
        @DisplayName("should throw UNAUTHORIZED when logout token is null")
        void shouldRejectNullTokenOnLogout() {
            assertThatThrownBy(() -> authService.logout(null))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }
    }

    // ─── REQUIRE USER ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("requireUser()")
    class RequireUser {

        @Test
        @DisplayName("should return UserResponse when session is valid")
        @SuppressWarnings("unchecked")
        void shouldReturnUser() {
            when(jdbcTemplate.query(
                    anyString(),
                    any(RowMapper.class),
                    eq("valid-token")
            )).thenAnswer(invocation -> {
                RowMapper<?> mapper = invocation.getArgument(1);
                var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                when(rs.getString("token")).thenReturn("valid-token");
                when(rs.getString("teacher_id")).thenReturn("teacher-1");
                when(rs.getString("username")).thenReturn("profesor1");
                return List.of(mapper.mapRow(rs, 0));
            });

            AuthService.UserResponse user = authService.requireUser("valid-token");

            assertThat(user.id()).isEqualTo("teacher-1");
            assertThat(user.username()).isEqualTo("profesor1");
        }
    }
}
