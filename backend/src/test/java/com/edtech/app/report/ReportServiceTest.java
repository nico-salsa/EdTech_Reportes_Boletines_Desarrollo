package com.edtech.app.report;

import com.edtech.app.auth.AuthService;
import com.edtech.app.common.ApiException;
import com.edtech.app.course.CourseService;
import com.edtech.app.course.CourseService.ActivitySummary;
import com.edtech.app.course.CourseService.CourseDetailResponse;
import com.edtech.app.course.CourseService.GradeSummary;
import com.edtech.app.course.CourseService.StudentSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ReportService}.
 *
 * <p>Why: ReportService encapsulates the critical business logic for grade calculations
 * (simple average, weighted average) and HTML template generation that feeds into the
 * PDF renderer via OpenHTMLtoPDF. Any error in averages or in the HTML structure would
 * produce incorrect academic reports.</p>
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private CourseService courseService;

    private ReportService reportService;

    private static final AuthService.UserResponse TEACHER =
            new AuthService.UserResponse("teacher-1", "profesor1");

    @BeforeEach
    void setUp() {
        reportService = new ReportService(courseService);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private CourseDetailResponse buildCourse(
            List<StudentSummary> students,
            List<ActivitySummary> activities,
            List<GradeSummary> grades
    ) {
        return new CourseDetailResponse(
                "course-1", "Matematicas", "teacher-1",
                students, activities, grades, "2026-01-01T00:00:00Z"
        );
    }

    private void stubCourse(CourseDetailResponse course) {
        when(courseService.getCourse(eq(TEACHER), eq("course-1"))).thenReturn(course);
    }

    // ─── JSON EXPORT ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("JSON export")
    class JsonExport {

        @Test
        @DisplayName("should generate valid JSON with correct averages for all grades present")
        void shouldGenerateJsonWithFullGrades() {
            // Given: 2 activities (60%+40%), student with grades 80 and 90
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(
                            new ActivitySummary("act-1", "Examen", 60.0),
                            new ActivitySummary("act-2", "Tarea", 40.0)
                    ),
                    List.of(
                            new GradeSummary("STU-001", "act-1", 80.0),
                            new GradeSummary("STU-001", "act-2", 90.0)
                    )
            );
            stubCourse(course);

            // When
            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            // Then
            String json = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(report.contentType()).isEqualTo("application/json");
            assertThat(report.filename()).contains("boletin").contains("STU-001").endsWith(".json");

            // Verify averages: simple = (80+90)/2 = 85, weighted = (80*60 + 90*40)/100 = 84
            assertThat(json).contains("\"generalAverage\": 85.0");
            assertThat(json).contains("\"weightedAverage\": 84.0");
            assertThat(json).contains("\"hasEmptyGrades\": false");
            assertThat(json).contains("\"teacher\": \"profesor1\"");
            assertThat(json).contains("\"course\": \"Matematicas\"");
            assertThat(json).contains("\"name\": \"Juan Perez\"");
        }

        @Test
        @DisplayName("should treat null grades as 0 and flag hasEmptyGrades")
        void shouldHandleEmptyGrades() {
            // Given: 2 activities, student only has a grade for 1
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(
                            new ActivitySummary("act-1", "Examen", 60.0),
                            new ActivitySummary("act-2", "Tarea", 40.0)
                    ),
                    List.of(
                            new GradeSummary("STU-001", "act-1", 80.0)
                            // act-2: no grade → null → treated as 0
                    )
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            String json = new String(report.content(), StandardCharsets.UTF_8);
            // simple avg = (80+0)/2 = 40, weighted = (80*60 + 0*40)/100 = 48
            assertThat(json).contains("\"generalAverage\": 40.0");
            assertThat(json).contains("\"weightedAverage\": 48.0");
            assertThat(json).contains("\"hasEmptyGrades\": true");
        }

        @Test
        @DisplayName("should handle zero activities gracefully")
        void shouldHandleZeroActivities() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(), // no activities
                    List.of()
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            String json = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(json).contains("\"generalAverage\": 0.0");
            assertThat(json).contains("\"weightedAverage\": 0.0");
        }

        @Test
        @DisplayName("should escape special characters in JSON output")
        void shouldEscapeSpecialChars() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan \"El Profe\" Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen <Final>", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 95.0))
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            String json = new String(report.content(), StandardCharsets.UTF_8);
            // The escape method converts " to &quot; and < > to &lt; &gt;
            assertThat(json).contains("&quot;El Profe&quot;");
            assertThat(json).contains("&lt;Final&gt;");
        }
    }

    // ─── HTML EXPORT ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("HTML export")
    class HtmlExport {

        @Test
        @DisplayName("should generate valid HTML with correct structure and data")
        void shouldGenerateHtmlWithCorrectData() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Maria Garcia", "maria@mail.com")),
                    List.of(
                            new ActivitySummary("act-1", "Parcial", 50.0),
                            new ActivitySummary("act-2", "Final", 50.0)
                    ),
                    List.of(
                            new GradeSummary("STU-001", "act-1", 70.0),
                            new GradeSummary("STU-001", "act-2", 90.0)
                    )
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "html"
            );

            String html = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(report.contentType()).isEqualTo("text/html;charset=UTF-8");
            assertThat(report.filename()).endsWith(".html");

            // Structure checks
            assertThat(html).contains("<!DOCTYPE html>");
            assertThat(html).contains("<title>Boletin academico</title>");
            assertThat(html).contains("Matematicas");
            assertThat(html).contains("profesor1");
            assertThat(html).contains("Maria Garcia");
            assertThat(html).contains("STU-001");
            assertThat(html).contains("maria@mail.com");

            // Activity rows
            assertThat(html).contains("Parcial");
            assertThat(html).contains("50.00%");
            // Note: String.format uses Locale.US in renderHtml, so values use dot separators
            assertThat(html).contains("70.00");
            assertThat(html).contains("Final");
            assertThat(html).contains("90.00");

            // Averages: simple = (70+90)/2 = 80, weighted = (70*50 + 90*50)/100 = 80
            // renderHtml uses %.2f with default locale — check for both possible formats
            assertThat(html).containsAnyOf("80.00", "80,00");
        }

        @Test
        @DisplayName("should show 'Sin nota' for null grades and warning notice")
        void shouldShowSinNotaForNullGrades() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen", 100.0)),
                    List.of() // no grades at all
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "html"
            );

            String html = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(html).contains("Sin nota");
            assertThat(html).contains("notas vacias tratadas como 0");
        }

        @Test
        @DisplayName("should NOT show warning when all grades are present")
        void shouldNotShowWarningWhenComplete() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 95.0))
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "html"
            );

            String html = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(html).doesNotContain("notas vacias");
            assertThat(html).doesNotContain("Sin nota");
        }

        @Test
        @DisplayName("should escape HTML entities in names to prevent XSS")
        void shouldEscapeHtmlEntities() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "<script>alert('xss')</script>", "xss@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 95.0))
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "html"
            );

            String html = new String(report.content(), StandardCharsets.UTF_8);
            // Must NOT contain raw script tags
            assertThat(html).doesNotContain("<script>");
            assertThat(html).contains("&lt;script&gt;");
        }
    }

    // ─── PDF EXPORT ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PDF export")
    class PdfExport {

        @Test
        @DisplayName("should produce non-empty PDF bytes with correct content type")
        void shouldGeneratePdfBytes() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 85.0))
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "pdf"
            );

            assertThat(report.contentType()).isEqualTo("application/pdf");
            assertThat(report.filename()).endsWith(".pdf");
            assertThat(report.content()).isNotEmpty();
            // PDF magic bytes: %PDF-
            assertThat(new String(report.content(), 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
        }
    }

    // ─── FORMAT VALIDATION ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Format validation")
    class FormatValidation {

        @Test
        @DisplayName("should throw BAD_REQUEST when format is null")
        void shouldRejectNullFormat() {
            assertThatThrownBy(() -> reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", null
            )).isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when format is blank")
        void shouldRejectBlankFormat() {
            assertThatThrownBy(() -> reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "  "
            )).isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST for unsupported format")
        void shouldRejectUnsupportedFormat() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(), List.of()
            );
            stubCourse(course);

            assertThatThrownBy(() -> reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "csv"
            )).isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("no soportado");
                    });
        }

        @Test
        @DisplayName("should be case-insensitive for format (e.g., 'JSON', 'Html')")
        void shouldAcceptFormatCaseInsensitive() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 90.0))
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "JSON"
            );

            assertThat(report.contentType()).isEqualTo("application/json");
        }
    }

    // ─── STUDENT NOT IN COURSE ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Student validation")
    class StudentValidation {

        @Test
        @DisplayName("should throw NOT_FOUND when student is not enrolled in the course")
        void shouldRejectStudentNotInCourse() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(), List.of()
            );
            stubCourse(course);

            assertThatThrownBy(() -> reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-999", "json"  // not enrolled
            )).isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    // ─── FILENAME GENERATION ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Filename generation")
    class FilenameGeneration {

        @Test
        @DisplayName("should sanitize course name in filename (replace non-alphanumeric chars)")
        void shouldSanitizeCourseName() {
            CourseDetailResponse course = new CourseDetailResponse(
                    "course-1", "Matematicas Nivel 2!", "teacher-1",
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Examen", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 90.0)),
                    "2026-01-01T00:00:00Z"
            );
            when(courseService.getCourse(eq(TEACHER), eq("course-1"))).thenReturn(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            // Spaces and ! should be replaced with -
            assertThat(report.filename()).isEqualTo("boletin-Matematicas-Nivel-2--STU-001.json");
        }
    }

    // ─── AVERAGE CALCULATIONS (edge cases) ──────────────────────────────────────

    @Nested
    @DisplayName("Average calculations")
    class AverageCalculations {

        @Test
        @DisplayName("should calculate correct weighted average with asymmetric weights")
        void shouldCalculateAsymmetricWeightedAverage() {
            // Given: Examen (70%) = 100, Tarea (30%) = 50
            // Weighted = (100*70 + 50*30)/100 = (7000+1500)/100 = 85.0
            // Simple = (100+50)/2 = 75.0
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(
                            new ActivitySummary("act-1", "Examen", 70.0),
                            new ActivitySummary("act-2", "Tarea", 30.0)
                    ),
                    List.of(
                            new GradeSummary("STU-001", "act-1", 100.0),
                            new GradeSummary("STU-001", "act-2", 50.0)
                    )
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            String json = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(json).contains("\"generalAverage\": 75.0");
            assertThat(json).contains("\"weightedAverage\": 85.0");
        }

        @Test
        @DisplayName("should handle single activity (weight=100%)")
        void shouldHandleSingleActivity() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(new ActivitySummary("act-1", "Unica", 100.0)),
                    List.of(new GradeSummary("STU-001", "act-1", 73.5))
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            String json = new String(report.content(), StandardCharsets.UTF_8);
            // Both averages should equal the single grade
            assertThat(json).contains("\"generalAverage\": 73.5");
            assertThat(json).contains("\"weightedAverage\": 73.5");
        }

        @Test
        @DisplayName("should return 0 averages when all grades are missing")
        void shouldReturnZeroWhenAllGradesMissing() {
            CourseDetailResponse course = buildCourse(
                    List.of(new StudentSummary("s1", "STU-001", "Juan Perez", "juan@mail.com")),
                    List.of(
                            new ActivitySummary("act-1", "Examen", 50.0),
                            new ActivitySummary("act-2", "Tarea", 50.0)
                    ),
                    List.of() // no grades
            );
            stubCourse(course);

            ReportService.ExportedReport report = reportService.exportStudentReport(
                    TEACHER, "course-1", "STU-001", "json"
            );

            String json = new String(report.content(), StandardCharsets.UTF_8);
            assertThat(json).contains("\"generalAverage\": 0.0");
            assertThat(json).contains("\"weightedAverage\": 0.0");
            assertThat(json).contains("\"hasEmptyGrades\": true");
        }
    }
}
