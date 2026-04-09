# Architecture

## Estado actual

EdTech es un monorepo local con dos aplicaciones principales:

- `frontend/`: React + Vite + TypeScript
- `backend/`: Spring Boot + Java + SQLite

El sistema esta orientado a un MVP local para docentes con autenticacion simple, gestion de cursos, estudiantes, programa evaluativo, notas y exportacion de boletines.

## Vista logica

```text
Frontend React
  -> /api/auth
  -> /api/courses
  -> /api/students
  -> /api/courses/{courseId}/students/{studentId}/report

Backend Spring Boot
  -> AuthController / AuthService
  -> CourseController / CourseService
  -> ReportController / ReportService
  -> ApiExceptionHandler
  -> SQLite local
```

## Runtime local

- Frontend por defecto en `http://localhost:5173`
- Backend por defecto en `http://localhost:8080/api`
- Base de datos SQLite en `backend/data/edtech.db`
- Configuracion cliente por `VITE_API_BASE_URL`

## Decisiones vigentes

- La aplicacion sigue siendo local; no hay despliegue distribuido.
- La sesion es simple y basada en token local enviado por `X-Session-Token`.
- La exportacion soporta `PDF`, `HTML` y `JSON`.
- El backend prioriza estabilidad del MVP sobre una reescritura profunda.

## Deuda tecnica visible

- No existe todavia un contrato OpenAPI publicado ni suites de prueba automatizadas relevantes en el repo.
- No existe observabilidad estructurada mas alla de las respuestas HTTP y logs por defecto del framework.

## Direccion objetivo inmediata

- Mantener el runtime actual estable sobre `JdbcTemplate` + SQLite.
- Hacer que especificacion, codigo y validaciones apunten a la misma realidad.
- Seguir ampliando pruebas y contratos sin reintroducir capas estructurales no usadas.
