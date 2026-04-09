package com.edtech.app.course;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link CourseController}.
 *
 * <p>Why: Verifies HTTP routing, request body validation, header requirements,
 * and correct delegation to the service layer with proper status codes.</p>
 */
@WebMvcTest(CourseController.class)
@Import(ApiExceptionHandler.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private CourseService courseService;

    private static final AuthService.UserResponse TEACHER =
            new AuthService.UserResponse("t-1", "profesor1");

    private void stubAuth() {
        when(authService.requireUser("valid-token")).thenReturn(TEACHER);
    }

    private CourseService.CourseDetailResponse sampleCourse() {
        return new CourseService.CourseDetailResponse(
                "c-1", "Matematicas", "t-1",
                List.of(), List.of(), List.of(),
                "2026-01-01T00:00:00Z"
        );
    }

    // ─── LIST COURSES ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/courses")
    class ListCourses {

        @Test
        @DisplayName("should return 200 with list of courses")
        void shouldListCourses() throws Exception {
            stubAuth();
            when(courseService.listCourses(TEACHER))
                    .thenReturn(List.of(new CourseService.CourseListItem(
                            "c-1", "Matematicas", "t-1", 5, 3, "2026-01-01"
                    )));

            mockMvc.perform(get("/api/courses")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("c-1"))
                    .andExpect(jsonPath("$[0].name").value("Matematicas"))
                    .andExpect(jsonPath("$[0].studentCount").value(5));
        }

        @Test
        @DisplayName("should return 401 when token header is missing")
        void shouldReturn401WithoutToken() throws Exception {
            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── CREATE COURSE ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/courses")
    class CreateCourse {

        @Test
        @DisplayName("should return 201 when course is created")
        void shouldCreateCourse() throws Exception {
            stubAuth();
            when(courseService.createCourse(TEACHER, "Fisica")).thenReturn(sampleCourse());

            mockMvc.perform(post("/api/courses")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Fisica\"}"))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldRejectBlankName() throws Exception {
            stubAuth();

            mockMvc.perform(post("/api/courses")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── GET COURSE ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/courses/{courseId}")
    class GetCourse {

        @Test
        @DisplayName("should return 200 with course detail")
        void shouldGetCourse() throws Exception {
            stubAuth();
            when(courseService.getCourse(TEACHER, "c-1")).thenReturn(sampleCourse());

            mockMvc.perform(get("/api/courses/c-1")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("c-1"))
                    .andExpect(jsonPath("$.name").value("Matematicas"));
        }

        @Test
        @DisplayName("should return 404 when course does not exist")
        void shouldReturn404ForMissingCourse() throws Exception {
            stubAuth();
            when(courseService.getCourse(TEACHER, "nonexistent"))
                    .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

            mockMvc.perform(get("/api/courses/nonexistent")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isNotFound());
        }
    }

    // ─── FIND STUDENT ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/students/{studentId}")
    class FindStudent {

        @Test
        @DisplayName("should return 200 with student data")
        void shouldFindStudent() throws Exception {
            stubAuth();
            when(courseService.findStudentByIdentifier("STU-001"))
                    .thenReturn(new CourseService.StudentSummary("s-1", "STU-001", "Juan Perez", "juan@mail.com"));

            mockMvc.perform(get("/api/students/STU-001")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Juan Perez"));
        }

        @Test
        @DisplayName("should return 404 when student does not exist")
        void shouldReturn404WhenStudentNotFound() throws Exception {
            stubAuth();
            when(courseService.findStudentByIdentifier("UNKNOWN"))
                    .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

            mockMvc.perform(get("/api/students/UNKNOWN")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Estudiante no encontrado"));
        }
    }

    // ─── ADD STUDENT TO COURSE ──────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/courses/{courseId}/students")
    class AddStudent {

        @Test
        @DisplayName("should return 200 when student is added")
        void shouldAddStudent() throws Exception {
            stubAuth();
            when(courseService.addStudentToCourse(eq(TEACHER), eq("c-1"), any()))
                    .thenReturn(sampleCourse());

            mockMvc.perform(post("/api/courses/c-1/students")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"STU-001\",\"name\":\"Juan\",\"email\":\"j@m.com\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 400 when studentId is blank")
        void shouldRejectBlankStudentId() throws Exception {
            stubAuth();

            mockMvc.perform(post("/api/courses/c-1/students")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"\",\"name\":\"Juan\",\"email\":\"j@m.com\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when student name is blank")
        void shouldRejectBlankName() throws Exception {
            stubAuth();

            mockMvc.perform(post("/api/courses/c-1/students")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"STU-001\",\"name\":\"\",\"email\":\"j@m.com\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when student email is blank")
        void shouldRejectBlankEmail() throws Exception {
            stubAuth();

            mockMvc.perform(post("/api/courses/c-1/students")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"STU-001\",\"name\":\"Juan\",\"email\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── UPDATE ACTIVITIES ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/courses/{courseId}/activities")
    class UpdateActivities {

        @Test
        @DisplayName("should return 200 when activities are updated")
        void shouldUpdateActivities() throws Exception {
            stubAuth();
            when(courseService.updateActivities(eq(TEACHER), eq("c-1"), any()))
                    .thenReturn(sampleCourse());

            mockMvc.perform(put("/api/courses/c-1/activities")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[{\"name\":\"Examen\",\"percentage\":100}]"))
                    .andExpect(status().isOk());
        }
    }

    // ─── UPDATE GRADE ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/courses/{courseId}/grades")
    class UpdateGrade {

        @Test
        @DisplayName("should return 200 when grade is updated")
        void shouldUpdateGrade() throws Exception {
            stubAuth();
            when(courseService.updateGrade(eq(TEACHER), eq("c-1"), any()))
                    .thenReturn(sampleCourse());

            mockMvc.perform(put("/api/courses/c-1/grades")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"STU-001\",\"activityId\":\"act-1\",\"grade\":85}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 400 when studentId is blank")
        void shouldRejectBlankStudentId() throws Exception {
            stubAuth();

            mockMvc.perform(put("/api/courses/c-1/grades")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"\",\"activityId\":\"act-1\",\"grade\":85}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when activityId is blank")
        void shouldRejectBlankActivityId() throws Exception {
            stubAuth();

            mockMvc.perform(put("/api/courses/c-1/grades")
                            .header("X-Session-Token", "valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"studentId\":\"STU-001\",\"activityId\":\"\",\"grade\":85}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── DELETE ACTIVITY ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/courses/{courseId}/activities/{activityId}")
    class DeleteActivity {

        @Test
        @DisplayName("should return 200 when activity is deleted")
        void shouldDeleteActivity() throws Exception {
            stubAuth();
            when(courseService.deleteActivity(TEACHER, "c-1", "act-1"))
                    .thenReturn(sampleCourse());

            mockMvc.perform(delete("/api/courses/c-1/activities/act-1")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 400 when trying to delete last activity")
        void shouldReturn400WhenDeletingLastActivity() throws Exception {
            stubAuth();
            when(courseService.deleteActivity(TEACHER, "c-1", "act-1"))
                    .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "Debe existir al menos una actividad evaluativa"));

            mockMvc.perform(delete("/api/courses/c-1/activities/act-1")
                            .header("X-Session-Token", "valid-token"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Debe existir al menos una actividad evaluativa"));
        }
    }
}
