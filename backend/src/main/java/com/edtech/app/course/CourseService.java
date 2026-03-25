package com.edtech.app.course;

import com.edtech.app.auth.AuthService;
import com.edtech.app.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

@Service
public class CourseService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final JdbcTemplate jdbcTemplate;

    public CourseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CourseListItem> listCourses(AuthService.UserResponse teacher) {
        return jdbcTemplate.query(
                """
                SELECT c.id,
                       c.name,
                       c.teacher_id,
                       c.created_at,
                       COALESCE(student_counts.total_students, 0) AS total_students,
                       COALESCE(activity_counts.total_activities, 0) AS total_activities
                FROM courses c
                LEFT JOIN (
                    SELECT course_id, COUNT(*) AS total_students
                    FROM course_students
                    GROUP BY course_id
                ) student_counts ON student_counts.course_id = c.id
                LEFT JOIN (
                    SELECT course_id, COUNT(*) AS total_activities
                    FROM evaluation_activities
                    GROUP BY course_id
                ) activity_counts ON activity_counts.course_id = c.id
                WHERE c.teacher_id = ?
                ORDER BY c.created_at DESC
                """,
                (rs, rowNum) -> new CourseListItem(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("teacher_id"),
                        rs.getInt("total_students"),
                        rs.getInt("total_activities"),
                        rs.getString("created_at")
                ),
                teacher.id()
        );
    }

    @Transactional
    public CourseDetailResponse createCourse(AuthService.UserResponse teacher, String name) {
        String normalizedName = normalizeCourseName(name);
        Integer duplicates = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM courses WHERE teacher_id = ? AND lower(name) = lower(?)",
                Integer.class,
                teacher.id(),
                normalizedName
        );

        if (duplicates != null && duplicates > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un curso con este nombre");
        }

        String courseId = UUID.randomUUID().toString();
        String createdAt = Instant.now().toString();
        jdbcTemplate.update(
                "INSERT INTO courses(id, teacher_id, name, created_at) VALUES (?, ?, ?, ?)",
                courseId,
                teacher.id(),
                normalizedName,
                createdAt
        );

        return getCourse(teacher, courseId);
    }

    public CourseDetailResponse getCourse(AuthService.UserResponse teacher, String courseId) {
        CourseRecord course = loadOwnedCourse(teacher, courseId);
        List<StudentSummary> students = jdbcTemplate.query(
                """
                SELECT s.id, s.student_identifier, s.full_name, s.email
                FROM course_students cs
                JOIN students s ON s.id = cs.student_id
                WHERE cs.course_id = ?
                ORDER BY s.full_name ASC
                """,
                (rs, rowNum) -> new StudentSummary(
                        rs.getString("id"),
                        rs.getString("student_identifier"),
                        rs.getString("full_name"),
                        rs.getString("email")
                ),
                course.id()
        );

        List<ActivitySummary> activities = jdbcTemplate.query(
                "SELECT id, name, weight, position FROM evaluation_activities WHERE course_id = ? ORDER BY position ASC",
                (rs, rowNum) -> new ActivitySummary(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("weight")
                ),
                course.id()
        );

        List<GradeSummary> grades = jdbcTemplate.query(
                """
                SELECT sg.student_id, s.student_identifier, sg.activity_id, sg.grade
                FROM student_grades sg
                JOIN students s ON s.id = sg.student_id
                WHERE sg.course_id = ?
                ORDER BY s.full_name ASC
                """,
                (rs, rowNum) -> new GradeSummary(
                        rs.getString("student_identifier"),
                        rs.getString("activity_id"),
                        (Double) rs.getObject("grade")
                ),
                course.id()
        );

        return new CourseDetailResponse(course.id(), course.name(), course.teacherId(), students, activities, grades, course.createdAt());
    }

    public StudentSummary findStudentByIdentifier(String studentIdentifier) {
        String normalizedIdentifier = requireStudentIdentifier(studentIdentifier);
        List<StudentSummary> students = jdbcTemplate.query(
                "SELECT id, student_identifier, full_name, email FROM students WHERE student_identifier = ?",
                (rs, rowNum) -> new StudentSummary(
                        rs.getString("id"),
                        rs.getString("student_identifier"),
                        rs.getString("full_name"),
                        rs.getString("email")
                ),
                normalizedIdentifier
        );

        if (students.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Estudiante no encontrado");
        }
        return students.getFirst();
    }

    @Transactional
    public CourseDetailResponse addStudentToCourse(AuthService.UserResponse teacher, String courseId, AddStudentRequest request) {
        CourseRecord course = loadOwnedCourse(teacher, courseId);
        String normalizedIdentifier = requireStudentIdentifier(request.studentId());
        String normalizedName = requireValue(request.name(), "El nombre completo es obligatorio");
        String normalizedEmail = requireEmail(request.email());

        List<StudentSummary> existingStudents = jdbcTemplate.query(
                "SELECT id, student_identifier, full_name, email FROM students WHERE student_identifier = ?",
                (rs, rowNum) -> new StudentSummary(
                        rs.getString("id"),
                        rs.getString("student_identifier"),
                        rs.getString("full_name"),
                        rs.getString("email")
                ),
                normalizedIdentifier
        );

        StudentSummary student;
        if (existingStudents.isEmpty()) {
            String studentId = UUID.randomUUID().toString();
            jdbcTemplate.update(
                    "INSERT INTO students(id, student_identifier, full_name, email, created_at) VALUES (?, ?, ?, ?, ?)",
                    studentId,
                    normalizedIdentifier,
                    normalizedName,
                    normalizedEmail,
                    Instant.now().toString()
            );
            student = new StudentSummary(studentId, normalizedIdentifier, normalizedName, normalizedEmail);
        } else {
            student = existingStudents.getFirst();
        }

        Integer enrollmentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM course_students WHERE course_id = ? AND student_id = ?",
                Integer.class,
                course.id(),
                student.id()
        );

        if (enrollmentCount != null && enrollmentCount > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "El estudiante ya esta inscrito en este curso");
        }

        jdbcTemplate.update(
                "INSERT INTO course_students(id, course_id, student_id, created_at) VALUES (?, ?, ?, ?)",
                UUID.randomUUID().toString(),
                course.id(),
                student.id(),
                Instant.now().toString()
        );

        return getCourse(teacher, course.id());
    }

    @Transactional
    public CourseDetailResponse updateActivities(AuthService.UserResponse teacher, String courseId, List<ActivityInput> activities) {
        CourseRecord course = loadOwnedCourse(teacher, courseId);
        validateActivities(activities);

        List<String> keptIds = new ArrayList<>();
        int position = 0;
        for (ActivityInput activity : activities) {
            String activityId = activity.id() == null || activity.id().isBlank()
                    ? UUID.randomUUID().toString()
                    : activity.id().trim();
            keptIds.add(activityId);
            Integer existing = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM evaluation_activities WHERE id = ? AND course_id = ?",
                    Integer.class,
                    activityId,
                    course.id()
            );

            if (existing != null && existing > 0) {
                jdbcTemplate.update(
                        "UPDATE evaluation_activities SET name = ?, weight = ?, position = ? WHERE id = ? AND course_id = ?",
                        activity.name().trim(),
                        activity.percentage(),
                        position,
                        activityId,
                        course.id()
                );
            } else {
                jdbcTemplate.update(
                        "INSERT INTO evaluation_activities(id, course_id, name, weight, position) VALUES (?, ?, ?, ?, ?)",
                        activityId,
                        course.id(),
                        activity.name().trim(),
                        activity.percentage(),
                        position
                );
            }
            position++;
        }

        List<String> existingIds = jdbcTemplate.query(
                "SELECT id FROM evaluation_activities WHERE course_id = ?",
                (rs, rowNum) -> rs.getString("id"),
                course.id()
        );

        for (String existingId : existingIds) {
            if (!keptIds.contains(existingId)) {
                jdbcTemplate.update("DELETE FROM student_grades WHERE course_id = ? AND activity_id = ?", course.id(), existingId);
                jdbcTemplate.update("DELETE FROM evaluation_activities WHERE course_id = ? AND id = ?", course.id(), existingId);
            }
        }

        return getCourse(teacher, course.id());
    }

    @Transactional
    public CourseDetailResponse updateGrade(AuthService.UserResponse teacher, String courseId, GradeInput input) {
        CourseRecord course = loadOwnedCourse(teacher, courseId);
        String studentIdentifier = requireStudentIdentifier(input.studentId());
        String activityId = requireValue(input.activityId(), "La actividad es obligatoria");
        Double grade = validateGrade(input.grade());

        String studentInternalId = resolveStudentInternalId(course.id(), studentIdentifier);
        ensureActivityBelongsToCourse(course.id(), activityId);

        Integer existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_grades WHERE course_id = ? AND student_id = ? AND activity_id = ?",
                Integer.class,
                course.id(),
                studentInternalId,
                activityId
        );

        if (existing != null && existing > 0) {
            jdbcTemplate.update(
                    "UPDATE student_grades SET grade = ?, updated_at = ? WHERE course_id = ? AND student_id = ? AND activity_id = ?",
                    grade,
                    Instant.now().toString(),
                    course.id(),
                    studentInternalId,
                    activityId
            );
        } else {
            jdbcTemplate.update(
                    "INSERT INTO student_grades(id, course_id, student_id, activity_id, grade, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    UUID.randomUUID().toString(),
                    course.id(),
                    studentInternalId,
                    activityId,
                    grade,
                    Instant.now().toString(),
                    Instant.now().toString()
            );
        }

        return getCourse(teacher, course.id());
    }

    private CourseRecord loadOwnedCourse(AuthService.UserResponse teacher, String courseId) {
        String normalizedCourseId = requireValue(courseId, "El curso es obligatorio");
        List<CourseRecord> courses = jdbcTemplate.query(
                "SELECT id, teacher_id, name, created_at FROM courses WHERE id = ? AND teacher_id = ?",
                (rs, rowNum) -> new CourseRecord(
                        rs.getString("id"),
                        rs.getString("teacher_id"),
                        rs.getString("name"),
                        rs.getString("created_at")
                ),
                normalizedCourseId,
                teacher.id()
        );

        if (courses.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }
        return courses.getFirst();
    }

    private String resolveStudentInternalId(String courseId, String studentIdentifier) {
        List<String> students = jdbcTemplate.query(
                """
                SELECT s.id
                FROM course_students cs
                JOIN students s ON s.id = cs.student_id
                WHERE cs.course_id = ? AND s.student_identifier = ?
                """,
                (rs, rowNum) -> rs.getString("id"),
                courseId,
                studentIdentifier
        );

        if (students.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El estudiante no pertenece al curso");
        }
        return students.getFirst();
    }

    private void ensureActivityBelongsToCourse(String courseId, String activityId) {
        Integer existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM evaluation_activities WHERE id = ? AND course_id = ?",
                Integer.class,
                activityId,
                courseId
        );
        if (existing == null || existing == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "La actividad evaluativa no existe en el curso");
        }
    }

    private void validateActivities(List<ActivityInput> activities) {
        if (activities == null || activities.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Debe existir al menos una actividad evaluativa");
        }

        Set<String> uniqueNames = new LinkedHashSet<>();
        double total = 0.0;
        for (ActivityInput activity : activities) {
            String normalizedName = requireValue(activity.name(), "Cada actividad debe tener nombre").toLowerCase(Locale.ROOT);
            if (!uniqueNames.add(normalizedName)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Las actividades no pueden tener nombres duplicados");
            }
            if (activity.percentage() == null || activity.percentage() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Cada actividad debe tener una ponderacion mayor a 0");
            }
            total += activity.percentage();
        }

        if (Math.abs(total - 100.0) > 0.01) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La suma de ponderaciones debe ser exactamente 100%");
        }
    }

    private Double validateGrade(Double grade) {
        if (grade == null) {
            return null;
        }
        if (grade < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La nota no puede ser negativa");
        }
        return grade;
    }

    private String normalizeCourseName(String name) {
        return requireValue(name, "El nombre del curso es obligatorio");
    }

    private String requireStudentIdentifier(String studentId) {
        return requireValue(studentId, "El ID del estudiante es obligatorio");
    }

    private String requireEmail(String email) {
        String normalizedEmail = requireValue(email, "El correo electronico es obligatorio");
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El correo electronico no es valido");
        }
        return normalizedEmail;
    }

    private String requireValue(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, message);
        }
        return value.trim();
    }

    private record CourseRecord(String id, String teacherId, String name, String createdAt) {
    }

    public record CourseListItem(String id, String name, String teacherId, int studentCount, int activityCount, String createdAt) {
    }

    public record StudentSummary(String id, String studentId, String name, String email) {
    }

    public record ActivitySummary(String id, String name, double percentage) {
    }

    public record GradeSummary(String studentId, String activityId, Double grade) {
    }

    public record CourseDetailResponse(
            String id,
            String name,
            String teacherId,
            List<StudentSummary> students,
            List<ActivitySummary> activities,
            List<GradeSummary> grades,
            String createdAt
    ) {
    }

    public record AddStudentRequest(String studentId, String name, String email) {
    }

    public record ActivityInput(String id, String name, Double percentage) {
    }

    public record GradeInput(String studentId, String activityId, Double grade) {
    }
}
