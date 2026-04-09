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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:file:report-export-tests?mode=memory&cache=shared"
})
@AutoConfigureMockMvc
class ReportExportIntegrationTest {

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
    void jsonReportIncludesAcademicDataAndEmptyGradeWarningFlag() throws Exception {
        Fixture fixture = createFixture();

        MvcResult result = mockMvc.perform(get("/api/courses/%s/students/%s/report".formatted(fixture.courseId(), fixture.studentIdentifier()))
                        .header("X-Session-Token", fixture.token())
                        .queryParam("format", "json"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(header().string("Content-Disposition", containsString("boletin-")))
                .andReturn();

        JsonNode report = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode grades = report.get("grades");

        org.junit.jupiter.api.Assertions.assertEquals("docente_reportes", report.get("teacher").asText());
        org.junit.jupiter.api.Assertions.assertEquals("Biologia", report.get("course").asText());
        org.junit.jupiter.api.Assertions.assertEquals("EST-9", report.get("student").get("id").asText());
        org.junit.jupiter.api.Assertions.assertEquals(2, grades.size());
        org.junit.jupiter.api.Assertions.assertEquals(2.0, report.get("generalAverage").asDouble(), 0.001);
        org.junit.jupiter.api.Assertions.assertEquals(2.0, report.get("weightedAverage").asDouble(), 0.001);
        org.junit.jupiter.api.Assertions.assertTrue(report.get("hasEmptyGrades").asBoolean());
    }

    @Test
    void htmlAndPdfReportsAreGenerated() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(get("/api/courses/%s/students/%s/report".formatted(fixture.courseId(), fixture.studentIdentifier()))
                        .header("X-Session-Token", fixture.token())
                        .queryParam("format", "html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Boletin academico")))
                .andExpect(content().string(containsString("Sin nota")))
                .andExpect(content().string(containsString("tratadas como 0")));

        mockMvc.perform(get("/api/courses/%s/students/%s/report".formatted(fixture.courseId(), fixture.studentIdentifier()))
                        .header("X-Session-Token", fixture.token())
                        .queryParam("format", "pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));
    }

    @Test
    void reportRejectsUnsupportedFormatMissingFormatAndUnknownStudent() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(get("/api/courses/%s/students/%s/report".formatted(fixture.courseId(), fixture.studentIdentifier()))
                        .header("X-Session-Token", fixture.token())
                        .queryParam("format", "csv"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Formato no soportado"));

        mockMvc.perform(get("/api/courses/%s/students/%s/report".formatted(fixture.courseId(), fixture.studentIdentifier()))
                        .header("X-Session-Token", fixture.token()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Falta el parametro requerido: format"));

        mockMvc.perform(get("/api/courses/%s/students/%s/report".formatted(fixture.courseId(), "EST-404"))
                        .header("X-Session-Token", fixture.token())
                        .queryParam("format", "json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Estudiante no encontrado en el curso"));
    }

    private Fixture createFixture() throws Exception {
        String token = register("docente_reportes", "Clave123");
        JsonNode course = createCourse(token, "Biologia");
        String courseId = course.get("id").asText();

        mockMvc.perform(post("/api/courses/%s/students".formatted(courseId))
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "EST-9",
                                  "name": "Laura Mesa",
                                  "email": "laura@correo.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(1)));

        MvcResult courseWithActivitiesResult = mockMvc.perform(put("/api/courses/%s/activities".formatted(courseId))
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"id":"act-report-1","name":"Quiz","percentage":50},
                                  {"id":"act-report-2","name":"Proyecto","percentage":50}
                                ]
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode courseWithActivities = objectMapper.readTree(courseWithActivitiesResult.getResponse().getContentAsString());
        String firstActivityId = courseWithActivities.get("activities").get(0).get("id").asText();
        String secondActivityId = courseWithActivities.get("activities").get(1).get("id").asText();

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-9",
                                  "activityId":"%s",
                                  "grade":4.0
                                }
                                """.formatted(firstActivityId)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/courses/%s/grades".formatted(courseId))
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId":"EST-9",
                                  "activityId":"%s",
                                  "grade":null
                                }
                                """.formatted(secondActivityId)))
                .andExpect(status().isOk());

        return new Fixture(token, courseId, "EST-9");
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

    private record Fixture(String token, String courseId, String studentIdentifier) {
    }
}
