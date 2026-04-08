package com.edtech.app.course;

import com.edtech.app.auth.AuthService;
import com.edtech.app.common.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Unit tests for {@link CourseService}.
 *
 * <p>Why: CourseService contains the main business rules — course creation with uniqueness
 * constraints, student enrollment, activity weight validation (sum=100%), and grade management.
 * Each branch must be tested to ensure data integrity.</p>
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private CourseService courseService;

    private static final AuthService.UserResponse TEACHER =
            new AuthService.UserResponse("teacher-1", "profesor1");

    @BeforeEach
    void setUp() {
        courseService = new CourseService(jdbcTemplate);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void stubOwnedCourse(String courseId, String teacherId) {
        when(jdbcTemplate.query(
                eq("SELECT id, teacher_id, name, created_at FROM courses WHERE id = ? AND teacher_id = ?"),
                any(RowMapper.class),
                eq(courseId),
                eq(teacherId)
        )).thenAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
            when(rs.getString("id")).thenReturn(courseId);
            when(rs.getString("teacher_id")).thenReturn(teacherId);
            when(rs.getString("name")).thenReturn("Matematicas");
            when(rs.getString("created_at")).thenReturn("2026-01-01T00:00:00Z");
            return List.of(mapper.mapRow(rs, 0));
        });
    }

    @SuppressWarnings("unchecked")
    private void stubEmptyCourseDetails(String courseId) {
        // Students query
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(courseId)
        )).thenReturn(List.of());
    }

    // ─── CREATE COURSE ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createCourse()")
    class CreateCourse {

        @Test
        @DisplayName("should throw CONFLICT when course name already exists for teacher")
        void shouldRejectDuplicateCourseName() {
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM courses WHERE teacher_id = ? AND lower(name) = lower(?)"),
                    eq(Integer.class),
                    eq("teacher-1"),
                    anyString()
            )).thenReturn(1);

            assertThatThrownBy(() -> courseService.createCourse(TEACHER, "Matematicas"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT);
                        assertThat(ex.getMessage()).contains("Ya existe un curso");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when course name is blank")
        void shouldRejectBlankCourseName() {
            assertThatThrownBy(() -> courseService.createCourse(TEACHER, "  "))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when course name is null")
        void shouldRejectNullCourseName() {
            assertThatThrownBy(() -> courseService.createCourse(TEACHER, null))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    // ─── FIND STUDENT BY IDENTIFIER ─────────────────────────────────────────────

    @Nested
    @DisplayName("findStudentByIdentifier()")
    class FindStudent {

        @Test
        @DisplayName("should return student when identifier matches")
        @SuppressWarnings("unchecked")
        void shouldFindExistingStudent() {
            when(jdbcTemplate.query(
                    eq("SELECT id, student_identifier, full_name, email FROM students WHERE student_identifier = ?"),
                    any(RowMapper.class),
                    eq("STU-001")
            )).thenAnswer(invocation -> {
                RowMapper<?> mapper = invocation.getArgument(1);
                var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                when(rs.getString("id")).thenReturn("s-uuid-1");
                when(rs.getString("student_identifier")).thenReturn("STU-001");
                when(rs.getString("full_name")).thenReturn("Juan Perez");
                when(rs.getString("email")).thenReturn("juan@mail.com");
                return List.of(mapper.mapRow(rs, 0));
            });

            CourseService.StudentSummary student = courseService.findStudentByIdentifier("STU-001");

            assertThat(student.id()).isEqualTo("s-uuid-1");
            assertThat(student.name()).isEqualTo("Juan Perez");
        }

        @Test
        @DisplayName("should throw NOT_FOUND when student does not exist")
        @SuppressWarnings("unchecked")
        void shouldRejectMissingStudent() {
            when(jdbcTemplate.query(
                    anyString(),
                    any(RowMapper.class),
                    anyString()
            )).thenReturn(List.of());

            assertThatThrownBy(() -> courseService.findStudentByIdentifier("UNKNOWN"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when identifier is blank")
        void shouldRejectBlankIdentifier() {
            assertThatThrownBy(() -> courseService.findStudentByIdentifier("  "))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    // ─── VALIDATE ACTIVITIES ────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateActivities() – validation")
    class ValidateActivities {

        @Test
        @DisplayName("should throw BAD_REQUEST when activities list is empty")
        void shouldRejectEmptyActivitiesList() {
            stubOwnedCourse("course-1", "teacher-1");

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", List.of()))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("al menos una actividad");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when activities list is null")
        void shouldRejectNullActivitiesList() {
            stubOwnedCourse("course-1", "teacher-1");

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", null))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when activity names are duplicated (case-insensitive)")
        void shouldRejectDuplicateNames() {
            stubOwnedCourse("course-1", "teacher-1");

            List<CourseService.ActivityInput> activities = List.of(
                    new CourseService.ActivityInput(null, "Examen", 50.0),
                    new CourseService.ActivityInput(null, "examen", 50.0)  // duplicate, case-insensitive
            );

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", activities))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("duplicados");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when a weight is zero")
        void shouldRejectZeroWeight() {
            stubOwnedCourse("course-1", "teacher-1");

            List<CourseService.ActivityInput> activities = List.of(
                    new CourseService.ActivityInput(null, "Examen", 0.0),
                    new CourseService.ActivityInput(null, "Tarea", 100.0)
            );

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", activities))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("mayor a 0");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when a weight is negative")
        void shouldRejectNegativeWeight() {
            stubOwnedCourse("course-1", "teacher-1");

            List<CourseService.ActivityInput> activities = List.of(
                    new CourseService.ActivityInput(null, "Examen", -10.0),
                    new CourseService.ActivityInput(null, "Tarea", 110.0)
            );

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", activities))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("mayor a 0");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when a weight is null")
        void shouldRejectNullWeight() {
            stubOwnedCourse("course-1", "teacher-1");

            List<CourseService.ActivityInput> activities = List.of(
                    new CourseService.ActivityInput(null, "Examen", null),
                    new CourseService.ActivityInput(null, "Tarea", 100.0)
            );

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", activities))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("mayor a 0");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when total percentage does not equal 100")
        void shouldRejectNon100Percentage() {
            stubOwnedCourse("course-1", "teacher-1");

            List<CourseService.ActivityInput> activities = List.of(
                    new CourseService.ActivityInput(null, "Examen", 50.0),
                    new CourseService.ActivityInput(null, "Tarea", 40.0) // total = 90
            );

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", activities))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("100%");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when an activity name is blank")
        void shouldRejectBlankActivityName() {
            stubOwnedCourse("course-1", "teacher-1");

            List<CourseService.ActivityInput> activities = List.of(
                    new CourseService.ActivityInput(null, "  ", 100.0)
            );

            assertThatThrownBy(() -> courseService.updateActivities(TEACHER, "course-1", activities))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    // ─── VALIDATE GRADE ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateGrade() – validation")
    class ValidateGrade {

        @Test
        @DisplayName("should throw BAD_REQUEST when grade is negative")
        void shouldRejectNegativeGrade() {
            stubOwnedCourse("course-1", "teacher-1");

            CourseService.GradeInput input = new CourseService.GradeInput("STU-001", "activity-1", -5.0);

            assertThatThrownBy(() -> courseService.updateGrade(TEACHER, "course-1", input))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("negativa");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when studentId is blank")
        void shouldRejectBlankStudentId() {
            stubOwnedCourse("course-1", "teacher-1");

            CourseService.GradeInput input = new CourseService.GradeInput("  ", "activity-1", 5.0);

            assertThatThrownBy(() -> courseService.updateGrade(TEACHER, "course-1", input))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when activityId is blank")
        void shouldRejectBlankActivityId() {
            stubOwnedCourse("course-1", "teacher-1");

            CourseService.GradeInput input = new CourseService.GradeInput("STU-001", "  ", 5.0);

            assertThatThrownBy(() -> courseService.updateGrade(TEACHER, "course-1", input))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    // ─── ADD STUDENT TO COURSE ──────────────────────────────────────────────────

    @Nested
    @DisplayName("addStudentToCourse() – validation")
    class AddStudentValidation {

        @Test
        @DisplayName("should throw BAD_REQUEST when email format is invalid")
        void shouldRejectInvalidEmail() {
            stubOwnedCourse("course-1", "teacher-1");

            CourseService.AddStudentRequest request = new CourseService.AddStudentRequest(
                    "STU-001", "Juan Perez", "not-an-email"
            );

            assertThatThrownBy(() -> courseService.addStudentToCourse(TEACHER, "course-1", request))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("correo");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when student name is blank")
        void shouldRejectBlankName() {
            stubOwnedCourse("course-1", "teacher-1");

            CourseService.AddStudentRequest request = new CourseService.AddStudentRequest(
                    "STU-001", "  ", "juan@mail.com"
            );

            assertThatThrownBy(() -> courseService.addStudentToCourse(TEACHER, "course-1", request))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when student identifier is blank")
        void shouldRejectBlankStudentIdentifier() {
            stubOwnedCourse("course-1", "teacher-1");

            CourseService.AddStudentRequest request = new CourseService.AddStudentRequest(
                    "  ", "Juan Perez", "juan@mail.com"
            );

            assertThatThrownBy(() -> courseService.addStudentToCourse(TEACHER, "course-1", request))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }

        @Test
        @DisplayName("should throw CONFLICT when student is already enrolled")
        @SuppressWarnings("unchecked")
        void shouldRejectDuplicateEnrollment() {
            stubOwnedCourse("course-1", "teacher-1");

            // Stub: student exists
            when(jdbcTemplate.query(
                    eq("SELECT id, student_identifier, full_name, email FROM students WHERE student_identifier = ?"),
                    any(RowMapper.class),
                    eq("STU-001")
            )).thenAnswer(invocation -> {
                RowMapper<?> mapper = invocation.getArgument(1);
                var rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                when(rs.getString("id")).thenReturn("s-uuid-1");
                when(rs.getString("student_identifier")).thenReturn("STU-001");
                when(rs.getString("full_name")).thenReturn("Juan Perez");
                when(rs.getString("email")).thenReturn("juan@mail.com");
                return List.of(mapper.mapRow(rs, 0));
            });

            // Stub: already enrolled
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM course_students WHERE course_id = ? AND student_id = ?"),
                    eq(Integer.class),
                    eq("course-1"),
                    eq("s-uuid-1")
            )).thenReturn(1);

            CourseService.AddStudentRequest request = new CourseService.AddStudentRequest(
                    "STU-001", "Juan Perez", "juan@mail.com"
            );

            assertThatThrownBy(() -> courseService.addStudentToCourse(TEACHER, "course-1", request))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT);
                        assertThat(ex.getMessage()).contains("ya esta inscrito");
                    });
        }
    }

    // ─── DELETE ACTIVITY ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteActivity()")
    class DeleteActivity {

        @Test
        @DisplayName("should throw BAD_REQUEST when deleting the last activity")
        void shouldPreventDeletingLastActivity() {
            stubOwnedCourse("course-1", "teacher-1");

            // Stub: activity belongs to course
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM evaluation_activities WHERE id = ? AND course_id = ?"),
                    eq(Integer.class),
                    eq("act-1"),
                    eq("course-1")
            )).thenReturn(1);

            // Stub: only 1 activity remaining
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM evaluation_activities WHERE course_id = ?"),
                    eq(Integer.class),
                    eq("course-1")
            )).thenReturn(1);

            assertThatThrownBy(() -> courseService.deleteActivity(TEACHER, "course-1", "act-1"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(ex.getMessage()).contains("al menos una actividad");
                    });

            // Verify no DELETE happened
            verify(jdbcTemplate, never()).update(
                    eq("DELETE FROM evaluation_activities WHERE course_id = ? AND id = ?"),
                    anyString(), anyString()
            );
        }

        @Test
        @DisplayName("should throw NOT_FOUND when activity does not belong to course")
        void shouldRejectNonExistentActivity() {
            stubOwnedCourse("course-1", "teacher-1");

            // Stub: activity NOT found
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM evaluation_activities WHERE id = ? AND course_id = ?"),
                    eq(Integer.class),
                    eq("fake-act"),
                    eq("course-1")
            )).thenReturn(0);

            assertThatThrownBy(() -> courseService.deleteActivity(TEACHER, "course-1", "fake-act"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when activityId is blank")
        void shouldRejectBlankActivityId() {
            stubOwnedCourse("course-1", "teacher-1");

            assertThatThrownBy(() -> courseService.deleteActivity(TEACHER, "course-1", "  "))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    // ─── LOAD OWNED COURSE ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("loadOwnedCourse() – via getCourse()")
    class LoadOwnedCourse {

        @Test
        @DisplayName("should throw NOT_FOUND when course does not exist for teacher")
        @SuppressWarnings("unchecked")
        void shouldRejectNonOwnedCourse() {
            when(jdbcTemplate.query(
                    eq("SELECT id, teacher_id, name, created_at FROM courses WHERE id = ? AND teacher_id = ?"),
                    any(RowMapper.class),
                    eq("nonexistent"),
                    eq("teacher-1")
            )).thenReturn(List.of());

            assertThatThrownBy(() -> courseService.getCourse(TEACHER, "nonexistent"))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(ex.getMessage()).contains("Curso no encontrado");
                    });
        }

        @Test
        @DisplayName("should throw BAD_REQUEST when courseId is blank")
        void shouldRejectBlankCourseId() {
            assertThatThrownBy(() -> courseService.getCourse(TEACHER, "  "))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }
}
