package com.edtech.app;

import com.edtech.app.common.ApiException;
import com.edtech.app.common.ApiExceptionHandler;
import com.edtech.app.common.DatabaseInitializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CommonComponentsTest {

    @Test
    void apiExceptionExposesStatusMessageAndDetails() {
        ApiException exception = new ApiException(HttpStatus.CONFLICT, "duplicado", Map.of("field", "username"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("duplicado", exception.getMessage());
        assertEquals("username", exception.getDetails().get("field"));
    }

    @Test
    void databaseInitializerCreatesExpectedTables() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        DatabaseInitializer initializer = new DatabaseInitializer(jdbcTemplate);

        initializer.initialize();

        verify(jdbcTemplate, times(7)).execute(org.mockito.ArgumentMatchers.anyString());
        verify(jdbcTemplate).execute(contains("CREATE TABLE IF NOT EXISTS teachers"));
        verify(jdbcTemplate).execute(contains("CREATE TABLE IF NOT EXISTS student_grades"));
    }

    @Test
    void apiExceptionHandlerBuildsConsistentPayloads() throws Exception {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<Map<String, Object>> apiResponse = handler.handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST, "fallo", Map.of("reason", "demo"))
        );
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals("fallo", apiResponse.getBody().get("message"));
        assertEquals("demo", ((Map<?, ?>) apiResponse.getBody().get("details")).get("reason"));

        Method method = CommonComponentsTest.class.getDeclaredMethod("sampleHeader", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        ResponseEntity<Map<String, Object>> missingHeaderResponse = handler.handleMissingHeader(
                new MissingRequestHeaderException("X-Debug-Header", parameter)
        );
        assertEquals(HttpStatus.BAD_REQUEST, missingHeaderResponse.getStatusCode());
        assertTrue(missingHeaderResponse.getBody().get("message").toString().contains("X-Debug-Header"));

        ResponseEntity<Map<String, Object>> malformedBodyResponse = handler.handleMessageNotReadable(
                new HttpMessageNotReadableException("json roto")
        );
        assertEquals(HttpStatus.BAD_REQUEST, malformedBodyResponse.getStatusCode());
        assertEquals("Solicitud invalida", malformedBodyResponse.getBody().get("message"));

        ResponseEntity<Map<String, Object>> unexpectedResponse = handler.handleUnexpectedException(new RuntimeException("boom"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, unexpectedResponse.getStatusCode());
        assertEquals("Error interno del servidor", unexpectedResponse.getBody().get("message"));
    }

    @SuppressWarnings("unused")
    private static void sampleHeader(String value) {
    }
}
