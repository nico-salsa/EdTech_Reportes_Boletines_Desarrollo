package com.edtech.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:file:smoke-tests?mode=memory&cache=shared",
        "spring.jpa.hibernate.ddl-auto=none"
})
@AutoConfigureMockMvc
class AuthAndCourseSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    void sessionEndpointRequiresToken() throws Exception {
        mockMvc.perform(get("/api/auth/session"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Falta el token de sesion"));
    }

    @Test
    void teacherCanRegisterLoginAndCreateCourse() throws Exception {
        String registerBody = """
                {
                  "username": "docente_smoke",
                  "password": "Clave123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value("docente_smoke"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        String loginBody = """
                {
                  "username": "docente_smoke",
                  "password": "Clave123"
                }
                """;

        String token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("docente_smoke"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/courses")
                        .header("X-Session-Token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Matematicas Smoke\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Matematicas Smoke"))
                .andExpect(jsonPath("$.students").isArray())
                .andExpect(jsonPath("$.activities").isArray())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }
}
