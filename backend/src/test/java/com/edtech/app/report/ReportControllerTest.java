package com.edtech.app.report;

import com.edtech.app.auth.AuthService;
import com.edtech.app.common.ApiException;
import com.edtech.app.common.ApiExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link ReportController}.
 *
 * <p>Why: Verifies correct content-type headers, Content-Disposition,
 * and delegation to the report service for all supported formats.</p>
 */
@WebMvcTest(ReportController.class)
@Import(ApiExceptionHandler.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private ReportService reportService;

    private static final AuthService.UserResponse TEACHER =
            new AuthService.UserResponse("t-1", "profesor1");

    private void stubAuth() {
        when(authService.requireUser("valid-token")).thenReturn(TEACHER);
    }

    // ─── EXPORT REPORT ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/courses/{courseId}/students/{studentId}/report")
    class ExportReport {

        @Test
        @DisplayName("should return PDF with correct headers")
        void shouldExportPdf() throws Exception {
            stubAuth();
            byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46}; // %PDF
            when(reportService.exportStudentReport(TEACHER, "c-1", "s-1", "pdf"))
                    .thenReturn(new ReportService.ExportedReport(pdfBytes, "application/pdf", "boletin_Juan.pdf"));

            mockMvc.perform(get("/api/courses/c-1/students/s-1/report")
                            .header("X-Session-Token", "valid-token")
                            .param("format", "pdf"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "application/pdf"))
                    .andExpect(header().string("Content-Disposition", "attachment; filename=\"boletin_Juan.pdf\""))
                    .andExpect(content().bytes(pdfBytes));
        }

        @Test
        @DisplayName("should return HTML with correct content type")
        void shouldExportHtml() throws Exception {
            stubAuth();
            byte[] htmlBytes = "<html>report</html>".getBytes();
            when(reportService.exportStudentReport(TEACHER, "c-1", "s-1", "html"))
                    .thenReturn(new ReportService.ExportedReport(htmlBytes, "text/html", "boletin_Juan.html"));

            mockMvc.perform(get("/api/courses/c-1/students/s-1/report")
                            .header("X-Session-Token", "valid-token")
                            .param("format", "html"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "text/html"));
        }

        @Test
        @DisplayName("should return JSON with correct content type")
        void shouldExportJson() throws Exception {
            stubAuth();
            byte[] jsonBytes = "{\"course\":\"Math\"}".getBytes();
            when(reportService.exportStudentReport(TEACHER, "c-1", "s-1", "json"))
                    .thenReturn(new ReportService.ExportedReport(jsonBytes, "application/json", "boletin_Juan.json"));

            mockMvc.perform(get("/api/courses/c-1/students/s-1/report")
                            .header("X-Session-Token", "valid-token")
                            .param("format", "json"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "application/json"));
        }

        @Test
        @DisplayName("should return 401 when token is missing")
        void shouldReturn401WithoutToken() throws Exception {
            mockMvc.perform(get("/api/courses/c-1/students/s-1/report")
                            .param("format", "pdf"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("should return 500 when format param is absent (handler gap — see refactoring note)")
        void shouldReturn500WithoutFormat() throws Exception {
            stubAuth();

            // NOTE: Spring throws MissingServletRequestParameterException for absent
            // @RequestParam, which would normally be 400. However, ApiExceptionHandler's
            // generic Exception handler catches it first and returns 500.
            // Recommendation: add a dedicated handler for MissingServletRequestParameterException.
            mockMvc.perform(get("/api/courses/c-1/students/s-1/report")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("should return 400 when format is unsupported")
        void shouldReturn400ForUnsupportedFormat() throws Exception {
            stubAuth();
            when(reportService.exportStudentReport(TEACHER, "c-1", "s-1", "xml"))
                    .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "Formato no soportado: xml"));

            mockMvc.perform(get("/api/courses/c-1/students/s-1/report")
                            .header("X-Session-Token", "valid-token")
                            .param("format", "xml"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Formato no soportado: xml"));
        }

        @Test
        @DisplayName("should return 404 when student not found in course")
        void shouldReturn404WhenStudentNotFound() throws Exception {
            stubAuth();
            when(reportService.exportStudentReport(TEACHER, "c-1", "nonexistent", "json"))
                    .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Estudiante no encontrado en este curso"));

            mockMvc.perform(get("/api/courses/c-1/students/nonexistent/report")
                            .header("X-Session-Token", "valid-token")
                            .param("format", "json"))
                    .andExpect(status().isNotFound());
        }
    }
}
