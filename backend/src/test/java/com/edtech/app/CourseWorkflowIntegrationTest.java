package com.edtech.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:file:course-workflow-tests?mode=memory&cache=shared"
})
@AutoConfigureMockMvc
class CourseWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM student_grades");
        jdbcTemplate.update("DELETE FROM evaluation_activities");
        jdbcTemplate.update("DELETE FROM course_students");
        jdbcTemplate.update("DELETE FROM courses");
        jdbcTemplate.update("DELETE FROM students");
        jdbcTemplate.update("DELETE FROM sessions");
        jdbcTemplate.update("DELETE FROM teachers");
    }

    @Test
    void teacherOnlySeesOwnedCoursesAndDuplicateNamesAreRejected() throws Exception {
        String teacherOneToken = register("docente_uno", "Clave123");
        String teacherTwoToken = register("docente_dos", "Clave123");

        createCourse(teacherOneToken, "Matematicas");
        createCourse(teacherOneToken, "Fisica");
        createCourse(teacherTwoToken, "Historia");

        mockMvc.perform(get("/api/courses")
                        .header("X-Session-Token", teacherOneToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].studentCount").exists())
                .andExpect(jsonPath("$[0].activityCount").exists());

        mockMvc.perform(post("/api/courses")
                        .header("X-Session-Token", teacherOneToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Matematicas\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe un curso con este nombre"));
    }

    @Test
    void enrollmentLookupAndOwnershipRulesAreEnforced() throws Exception {
        String teacherToken = register("docente_curso", "Clave123");
        String otherTeacherToken = register("docente_otro", "Clave123");
        JsonNode course = createCourse(teacherToken, "Lenguaje");
        String courseId = course.get("id").asText();

        mockMvc.perform(get("/api/students/EST-1")
                        .header("X-Session-Token", teacherToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Estudiante no encontrado"));

        mockMvc.perform(post("/api/courses/%s/students".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "EST-1",
                                  "name": "Ana Torres",
                                  "email": "ana@correo.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(1)))
                .andExpect(jsonPath("$.students[0].studentId").value("EST-1"));

        mockMvc.perform(get("/api/students/EST-1")
                        .header("X-Session-Token", teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Torres"));

        mockMvc.perform(post("/api/courses/%s/students".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "EST-1",
                                  "name": "Ana Torres",
                                  "email": "ana@correo.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El estudiante ya esta inscrito en este curso"));

        mockMvc.perform(post("/api/courses/%s/students".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "EST-2",
                                  "name": "Bruno Diaz",
                                  "email": "correo-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El correo electronico no es valido"));

        mockMvc.perform(get("/api/courses/%s".formatted(courseId))
                        .header("X-Session-Token", otherTeacherToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Curso no encontrado"));
    }

    @Test
    void teacherCanManageEvaluationProgramAndGradesWithValidation() throws Exception {
        String teacherToken = register("docente_notas", "Clave123");
        JsonNode course = createCourse(teacherToken, "Quimica");
        String courseId = course.get("id").asText();

        addStudent(teacherToken, courseId, "EST-3", "Carlos Ruiz", "carlos@correo.com");

        JsonNode courseWithActivities = updateActivities(teacherToken, courseId, """
                [
                  {"id":"act-1","name":"Parcial 1","percentage":60},
                  {"id":"act-2","name":"Proyecto Final","percentage":40}
                ]
                """);
        String firstActivityId = courseWithActivities.get("activities").get(0).get("id").asText();
        String secondActivityId = courseWithActivities.get("activities").get(1).get("id").asText();

        mockMvc.perform(put("/api/courses/%s/activities".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"id":"act-1","name":"Parcial 1","percentage":50},
                                  {"id":"act-2","name":"Proyecto Final","percentage":20}
                                ]
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La suma de ponderaciones debe ser exactamente 100%"));

        mockMvc.perform(put("/api/courses/%s/activities".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"id":"act-1","name":"Parcial","percentage":50},
                                  {"id":"act-2","name":"Parcial","percentage":50}
                                ]
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Las actividades no pueden tener nombres duplicados"));

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-3",
                                  "activityId":"%s",
                                  "grade":4.5
                                }
                                """.formatted(firstActivityId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grades", hasSize(1)))
                .andExpect(jsonPath("$.grades[0].grade").value(4.5));

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-3",
                                  "activityId":"%s",
                                  "grade":null
                                }
                                """.formatted(secondActivityId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grades", hasSize(2)));

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-3",
                                  "activityId":"%s",
                                  "grade":-1
                                }
                                """.formatted(firstActivityId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La nota no puede ser negativa"));

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-404",
                                  "activityId":"%s",
                                  "grade":3.0
                                }
                                """.formatted(firstActivityId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El estudiante no pertenece al curso"));

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-3",
                                  "activityId":"actividad-inexistente",
                                  "grade":3.0
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("La actividad evaluativa no existe en el curso"));

        mockMvc.perform(delete("/api/courses/%s/activities/%s".formatted(courseId, firstActivityId))
                        .header("X-Session-Token", teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activities", hasSize(1)))
                .andExpect(jsonPath("$.grades", hasSize(1)));

        mockMvc.perform(delete("/api/courses/%s/activities/%s".formatted(courseId, secondActivityId))
                        .header("X-Session-Token", teacherToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Debe existir al menos una actividad evaluativa"));
    }

    private String register(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private JsonNode createCourse(String token, String courseName) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/courses")
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"%s\"}".formatted(courseName)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private void addStudent(String token, String courseId, String studentId, String name, String email) throws Exception {
        mockMvc.perform(post("/api/courses/%s/students".formatted(courseId))
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "%s",
                                  "name": "%s",
                                  "email": "%s"
                                }
                                """.formatted(studentId, name, email)))
                .andExpect(status().isOk());
    }

    private JsonNode updateActivities(String token, String courseId, String payload) throws Exception {
        MvcResult result = mockMvc.perform(put("/api/courses/%s/activities".formatted(courseId))
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
