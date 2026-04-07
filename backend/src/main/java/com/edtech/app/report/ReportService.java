package com.edtech.app.report;

import com.edtech.app.auth.AuthService;
import com.edtech.app.common.ApiException;
import com.edtech.app.course.CourseService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ReportService {

    private final CourseService courseService;

    public ReportService(CourseService courseService) {
        this.courseService = courseService;
    }

    public ExportedReport exportStudentReport(AuthService.UserResponse teacher, String courseId, String studentIdentifier, String format) {
        String normalizedFormat = normalizeFormat(format);
        CourseService.CourseDetailResponse course = courseService.getCourse(teacher, courseId);
        CourseService.StudentSummary student = course.students().stream()
                .filter(item -> item.studentId().equals(studentIdentifier))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Estudiante no encontrado en el curso"));

        List<GradeLine> grades = new ArrayList<>();
        double totalForAverage = 0.0;
        double weightedTotal = 0.0;
        boolean hasEmptyGrades = false;

        for (CourseService.ActivitySummary activity : course.activities()) {
            Double grade = course.grades().stream()
                    .filter(item -> item.studentId().equals(studentIdentifier) && item.activityId().equals(activity.id()))
                    .map(CourseService.GradeSummary::grade)
                    .findFirst()
                    .orElse(null);

            double gradeForAverage = grade == null ? 0.0 : grade;
            if (grade == null) {
                hasEmptyGrades = true;
            }

            totalForAverage += gradeForAverage;
            weightedTotal += gradeForAverage * activity.percentage();
            grades.add(new GradeLine(activity.name(), activity.percentage(), grade));
        }

        double generalAverage = course.activities().isEmpty()
                ? 0.0
                : totalForAverage / course.activities().size();
        double weightedAverage = course.activities().isEmpty()
                ? 0.0
                : weightedTotal / 100.0;

        ReportPayload payload = new ReportPayload(
                teacher.username(),
                course.name(),
                new StudentPayload(student.studentId(), student.name(), student.email()),
                grades,
                generalAverage,
                weightedAverage,
                hasEmptyGrades,
                Instant.now().toString(),
                normalizedFormat
        );

        return switch (normalizedFormat) {
            case "json" -> new ExportedReport(renderJson(payload).getBytes(StandardCharsets.UTF_8), "application/json", filename("json", course.name(), student.studentId()));
            case "html" -> new ExportedReport(renderHtml(payload).getBytes(StandardCharsets.UTF_8), "text/html;charset=UTF-8", filename("html", course.name(), student.studentId()));
            case "pdf" -> new ExportedReport(renderPdf(payload), "application/pdf", filename("pdf", course.name(), student.studentId()));
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "Formato no soportado");
        };
    }

    private String renderJson(ReportPayload payload) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"teacher\": \"").append(escape(payload.teacher())).append("\",\n");
        builder.append("  \"course\": \"").append(escape(payload.course())).append("\",\n");
        builder.append("  \"student\": {\n");
        builder.append("    \"id\": \"").append(escape(payload.student().id())).append("\",\n");
        builder.append("    \"name\": \"").append(escape(payload.student().name())).append("\",\n");
        builder.append("    \"email\": \"").append(escape(payload.student().email())).append("\"\n");
        builder.append("  },\n");
        builder.append("  \"grades\": [\n");
        for (int index = 0; index < payload.grades().size(); index++) {
            GradeLine grade = payload.grades().get(index);
            builder.append("    {\n");
            builder.append("      \"activity\": \"").append(escape(grade.activity())).append("\",\n");
            builder.append("      \"percentage\": ").append(grade.percentage());
            if (grade.grade() != null) {
                builder.append(",\n");
                builder.append("      \"grade\": ").append(grade.grade()).append("\n");
            } else {
                builder.append("\n");
            }
            builder.append("    }");
            if (index < payload.grades().size() - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("  ],\n");
        builder.append("  \"generalAverage\": ").append(payload.generalAverage()).append(",\n");
        builder.append("  \"weightedAverage\": ").append(payload.weightedAverage()).append(",\n");
        builder.append("  \"hasEmptyGrades\": ").append(payload.hasEmptyGrades()).append(",\n");
        builder.append("  \"exportDate\": \"").append(payload.exportDate()).append("\",\n");
        builder.append("  \"format\": \"").append(payload.format()).append("\"\n");
        builder.append("}\n");
        return builder.toString();
    }

    private String renderHtml(ReportPayload payload) {
        StringBuilder rows = new StringBuilder();
        for (GradeLine grade : payload.grades()) {
            rows.append("<tr>")
                    .append("<td>").append(escape(grade.activity())).append("</td>")
                    .append("<td>").append(String.format(Locale.US, "%.2f", grade.percentage())).append("%</td>")
                    .append("<td>").append(grade.grade() == null ? "Sin nota" : String.format(Locale.US, "%.2f", grade.grade())).append("</td>")
                    .append("</tr>");
        }

        String incompleteNotice = payload.hasEmptyGrades()
                ? "<p class=\"warning\">Este boletin contiene notas vacias tratadas como 0 para el calculo.</p>"
                : "";

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8" />
                  <title>Boletin academico</title>
                  <style>
                    body { font-family: Arial, sans-serif; margin: 32px; color: #1f2937; }
                    h1 { color: #1e3a8a; margin-bottom: 4px; }
                    h2 { margin-top: 0; color: #475569; font-size: 18px; }
                    .meta { margin-bottom: 24px; }
                    .meta p { margin: 4px 0; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 16px; }
                    th, td { border: 1px solid #cbd5e1; padding: 10px; text-align: left; }
                    th { background: #dbeafe; }
                    .summary { margin-top: 24px; padding: 12px; background: #eff6ff; border: 1px solid #bfdbfe; }
                    .summary p { margin: 6px 0; }
                    .warning { margin-top: 16px; padding: 12px; background: #fff7ed; border: 1px solid #fdba74; color: #9a3412; }
                    .footer { margin-top: 24px; font-size: 12px; color: #64748b; }
                  </style>
                </head>
                <body>
                  <h1>Boletin academico</h1>
                  <h2>%s</h2>
                  <div class="meta">
                    <p><strong>Docente:</strong> %s</p>
                    <p><strong>Estudiante:</strong> %s</p>
                    <p><strong>ID:</strong> %s</p>
                    <p><strong>Correo:</strong> %s</p>
                  </div>
                  <table>
                    <thead>
                      <tr>
                        <th>Actividad</th>
                        <th>Ponderacion</th>
                        <th>Nota</th>
                      </tr>
                    </thead>
                    <tbody>%s</tbody>
                  </table>
                  %s
                  <div class="summary">
                    <p><strong>Promedio general:</strong> %.2f</p>
                    <p><strong>Promedio ponderado:</strong> %.2f</p>
                  </div>
                  <div class="footer">
                    Generado el %s por EdTech.
                  </div>
                </body>
                </html>
                """.formatted(
                escape(payload.course()),
                escape(payload.teacher()),
                escape(payload.student().name()),
                escape(payload.student().id()),
                escape(payload.student().email()),
                rows,
                incompleteNotice,
                payload.generalAverage(),
                payload.weightedAverage(),
                LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        );
    }

    private byte[] renderPdf(ReportPayload payload) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(renderHtml(payload), null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible generar el PDF");
        }
    }

    private String normalizeFormat(String format) {
        if (format == null || format.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El formato es obligatorio");
        }
        return format.trim().toLowerCase(Locale.ROOT);
    }

    private String filename(String extension, String courseName, String studentId) {
        String safeCourseName = courseName.replaceAll("[^a-zA-Z0-9_-]", "-");
        return "boletin-" + safeCourseName + "-" + studentId + "." + extension;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public record ExportedReport(byte[] content, String contentType, String filename) {
    }

    private record ReportPayload(
            String teacher,
            String course,
            StudentPayload student,
            List<GradeLine> grades,
            double generalAverage,
            double weightedAverage,
            boolean hasEmptyGrades,
            String exportDate,
            String format
    ) {
    }

    private record StudentPayload(String id, String name, String email) {
    }

    private record GradeLine(String activity, double percentage, Double grade) {
    }
}
