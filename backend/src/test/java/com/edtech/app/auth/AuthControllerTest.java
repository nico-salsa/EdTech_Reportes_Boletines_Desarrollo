package com.edtech.app.auth;

import com.edtech.app.common.ApiException;
import com.edtech.app.common.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link AuthController}.
 *
 * <p>Why: Controllers are the HTTP boundary — testing them ensures correct status codes,
 * request validation (@Valid), header requirement (X-Session-Token), and error response
 * formatting via the global exception handler.</p>
 */
@WebMvcTest(AuthController.class)
@Import(ApiExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── REGISTER ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("should return 201 with session on valid registration")
        void shouldRegisterSuccessfully() throws Exception {
            when(authService.register(eq("profesor1"), eq("password123")))
                    .thenReturn(new AuthService.SessionResponse(
                            new AuthService.UserResponse("t-1", "profesor1"),
                            "token-uuid"
                    ));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"profesor1\",\"password\":\"password123\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.user.id").value("t-1"))
                    .andExpect(jsonPath("$.user.username").value("profesor1"))
                    .andExpect(jsonPath("$.token").value("token-uuid"));
        }

        @Test
        @DisplayName("should return 400 when username is missing")
        void shouldRejectMissingUsername() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"password\":\"password123\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when password is missing")
        void shouldRejectMissingPassword() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"profesor1\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when body is empty")
        void shouldRejectEmptyBody() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 409 when username is duplicate")
        void shouldReturnConflictOnDuplicate() throws Exception {
            when(authService.register(anyString(), anyString()))
                    .thenThrow(new ApiException(HttpStatus.CONFLICT, "El nombre de usuario ya existe"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"profesor1\",\"password\":\"password123\"}"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("El nombre de usuario ya existe"));
        }
    }

    // ─── LOGIN ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("should return 200 with session on valid login")
        void shouldLoginSuccessfully() throws Exception {
            when(authService.login(eq("profesor1"), eq("securePass")))
                    .thenReturn(new AuthService.SessionResponse(
                            new AuthService.UserResponse("t-1", "profesor1"),
                            "session-token"
                    ));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"profesor1\",\"password\":\"securePass\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("session-token"));
        }

        @Test
        @DisplayName("should return 401 when credentials are invalid")
        void shouldReturn401OnBadCredentials() throws Exception {
            when(authService.login(anyString(), anyString()))
                    .thenThrow(new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"bad\",\"password\":\"wrong\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Usuario o contrasena incorrectos"));
        }
    }

    // ─── SESSION ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/auth/session")
    class Session {

        @Test
        @DisplayName("should return 200 with session data when token is valid")
        void shouldReturnSession() throws Exception {
            when(authService.getSession("valid-token"))
                    .thenReturn(new AuthService.SessionResponse(
                            new AuthService.UserResponse("t-1", "profesor1"),
                            "valid-token"
                    ));

            mockMvc.perform(get("/api/auth/session")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.username").value("profesor1"));
        }

        @Test
        @DisplayName("should return 401 when X-Session-Token header is missing")
        void shouldReturn401WhenHeaderMissing() throws Exception {
            mockMvc.perform(get("/api/auth/session"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Falta el token de sesion"));
        }
    }

    // ─── LOGOUT ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {

        @Test
        @DisplayName("should return 204 on successful logout")
        void shouldLogoutSuccessfully() throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 401 when logout token is invalid")
        void shouldReturn401OnInvalidLogout() throws Exception {
            doThrow(new ApiException(HttpStatus.UNAUTHORIZED, "Sesion invalida o expirada"))
                    .when(authService).logout("bad-token");

            mockMvc.perform(post("/api/auth/logout")
                            .header("X-Session-Token", "bad-token"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
