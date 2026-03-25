package com.edtech.app.common;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initialize() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS teachers (
                    id TEXT PRIMARY KEY,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sessions (
                    token TEXT PRIMARY KEY,
                    teacher_id TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id TEXT PRIMARY KEY,
                    student_identifier TEXT NOT NULL UNIQUE,
                    full_name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    id TEXT PRIMARY KEY,
                    teacher_id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    UNIQUE(teacher_id, name)
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS course_students (
                    id TEXT PRIMARY KEY,
                    course_id TEXT NOT NULL,
                    student_id TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    UNIQUE(course_id, student_id)
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS evaluation_activities (
                    id TEXT PRIMARY KEY,
                    course_id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    weight REAL NOT NULL,
                    position INTEGER NOT NULL,
                    UNIQUE(course_id, name)
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS student_grades (
                    id TEXT PRIMARY KEY,
                    course_id TEXT NOT NULL,
                    student_id TEXT NOT NULL,
                    activity_id TEXT NOT NULL,
                    grade REAL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    UNIQUE(course_id, student_id, activity_id)
                )
                """);
    }
}
