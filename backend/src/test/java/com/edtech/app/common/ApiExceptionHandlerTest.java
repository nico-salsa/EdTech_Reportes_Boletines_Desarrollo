package com.edtech.app.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ApiExceptionHandler}.
 *
 * <p>Why: The handler is the single exit-point for ALL error responses.
 * Testing it directly (without MockMvc overhead) verifies payload structure,
 * status codes, and field-level details for every exception branch.</p>
 */
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    // ─── ApiException ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("handleApiException should return correct status and message")
    void shouldHandleApiException() {
        var ex = new ApiException(HttpStatus.CONFLICT, "Duplicado");

        ResponseEntity<Map<String, Object>> response = handler.handleApiException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(409);
        assertThat(response.getBody().get("error")).isEqualTo("Conflict");
        assertThat(response.getBody().get("message")).isEqualTo("Duplicado");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("handleApiException should include details map when provided")
    void shouldIncludeDetailsInApiException() {
        var details = Map.<String, Object>of("field", "nombre");
        var ex = new ApiException(HttpStatus.BAD_REQUEST, "Invalido", details);

        ResponseEntity<Map<String, Object>> response = handler.handleApiException(ex);

        assertThat(response.getBody().get("details")).isEqualTo(details);
    }

    @Test
    @DisplayName("handleApiException should include empty details when none provided")
    void shouldIncludeEmptyDetailsWhenNone() {
        var ex = new ApiException(HttpStatus.NOT_FOUND, "No encontrado");

        ResponseEntity<Map<String, Object>> response = handler.handleApiException(ex);

        assertThat(response.getBody().get("details")).isEqualTo(Map.of());
    }

    // ─── Generic Exception ──────────────────────────────────────────────────────

    @Test
    @DisplayName("handleUnexpectedException should return 500 with generic message")
    void shouldHandleGenericException() {
        var ex = new RuntimeException("Something went wrong");

        ResponseEntity<Map<String, Object>> response = handler.handleUnexpectedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("message")).isEqualTo("Error interno del servidor");
    }

    // ─── MissingRequestHeaderException ──────────────────────────────────────────

    @Test
    @DisplayName("handleMissingHeader should return 401 for X-Session-Token")
    void shouldReturn401ForMissingSessionToken() throws Exception {
        var ex = buildMissingHeaderException("X-Session-Token");

        ResponseEntity<Map<String, Object>> response = handler.handleMissingHeader(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("message")).isEqualTo("Falta el token de sesion");
    }

    @Test
    @DisplayName("handleMissingHeader should return 400 for other headers")
    void shouldReturn400ForOtherMissingHeaders() throws Exception {
        var ex = buildMissingHeaderException("X-Custom-Header");

        ResponseEntity<Map<String, Object>> response = handler.handleMissingHeader(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString())
                .contains("X-Custom-Header");
    }

    // ─── HttpMessageNotReadableException ────────────────────────────────────────

    @Test
    @DisplayName("handleMessageNotReadable should return 400 with generic message")
    void shouldHandleUnreadableBody() {
        var ex = new org.springframework.http.converter.HttpMessageNotReadableException("bad json");

        ResponseEntity<Map<String, Object>> response = handler.handleMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Solicitud invalida");
    }

    // ─── Payload Structure ──────────────────────────────────────────────────────

    @Test
    @DisplayName("all error responses should contain the five standard fields")
    void shouldContainStandardFields() {
        var ex = new ApiException(HttpStatus.BAD_REQUEST, "test");

        ResponseEntity<Map<String, Object>> response = handler.handleApiException(ex);

        assertThat(response.getBody()).containsKeys("timestamp", "status", "error", "message", "details");
    }

    // ─── Helper ─────────────────────────────────────────────────────────────────

    private org.springframework.web.bind.MissingRequestHeaderException buildMissingHeaderException(String header)
            throws Exception {
        var param = new org.springframework.core.MethodParameter(
                ApiExceptionHandler.class.getDeclaredMethod(
                        "handleMissingHeader",
                        org.springframework.web.bind.MissingRequestHeaderException.class
                ),
                0
        );
        return new org.springframework.web.bind.MissingRequestHeaderException(header, param);
    }
}
