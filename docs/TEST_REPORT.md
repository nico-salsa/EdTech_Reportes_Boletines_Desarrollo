# Reporte de Pruebas Unitarias — EdTech Reportes & Boletines

**Fecha:** 8 de abril de 2026  
**Rama:** `test/unit-tests`  
**Autor:** sstelmaj  
**Stack:** Java 21 / Spring Boot 3.3.5 / React 18.3 / TypeScript / Vite 6

---

## Resumen Ejecutivo

| Ámbito   | Archivos de test | Tests | Pasados | Fallidos | Tasa |
|----------|:-------:|:-----:|:-------:|:--------:|:----:|
| Backend  | 7       | 103   | 101     | 2        | 98%  |
| Frontend | 4       | 40    | 40      | 0        | 100% |
| **Total**| **11**  | **143**| **141** | **2**    | **98.6%** |

> **Nota:** Los 2 tests fallidos son **intencionales** — documentan bugs reales
> (BUG-001 y BUG-002). Los tests validan el **comportamiento esperado**, no el
> comportamiento actual. Cuando los bugs se corrijan, los tests pasarán.

---

## 1. Backend — Qué se Prueba

### 1.1 AuthServiceTest (20 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | Registro exitoso crea profesor y sesión | ✅ |
| 2 | Registro con username nulo lanza 400 | ✅ |
| 3 | Registro con username vacío lanza 400 | ✅ |
| 4 | Registro con username en blanco lanza 400 | ✅ |
| 5 | Registro con password nulo lanza 400 | ✅ |
| 6 | Registro con password vacío lanza 400 | ✅ |
| 7 | Registro duplicado lanza 409 | ✅ |
| 8 | Login exitoso devuelve sesión | ✅ |
| 9 | Login con usuario inexistente lanza 401 | ✅ |
| 10 | Login con contraseña incorrecta lanza 401 | ✅ |
| 11 | Login con username nulo lanza 400 | ✅ |
| 12 | Login con password nulo lanza 400 | ✅ |
| 13 | getSession devuelve sesión válida | ✅ |
| 14 | getSession con token inválido lanza 401 | ✅ |
| 15 | getSession con token nulo lanza 401 | ✅ |
| 16 | Logout exitoso elimina sesión | ✅ |
| 17 | Logout con token inválido lanza 401 | ✅ |
| 18 | Logout con token nulo lanza 401 | ✅ |
| 19 | requireUser devuelve usuario autenticado | ✅ |
| 20 | requireUser con token inválido lanza 401 | ✅ |

### 1.2 CourseServiceTest (25 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | createCourse exitoso persiste curso con UUID | ✅ |
| 2 | createCourse con nombre duplicado lanza 409 | ✅ |
| 3 | createCourse con nombre nulo lanza 400 | ✅ |
| 4 | createCourse con nombre vacío lanza 400 | ✅ |
| 5 | addStudentToCourse exitoso | ✅ |
| 6 | addStudentToCourse con studentId vacío lanza 400 | ✅ |
| 7 | addStudentToCourse con nombre vacío lanza 400 | ✅ |
| 8 | addStudentToCourse con email inválido lanza 400 | ✅ |
| 9 | addStudentToCourse con estudiante duplicado lanza 409 | ✅ |
| 10 | updateActivities exitoso | ✅ |
| 11 | updateActivities con lista vacía lanza 400 | ✅ |
| 12 | updateActivities con porcentajes que no suman 100 lanza 400 | ✅ |
| 13 | updateActivities con nombres duplicados (case-insensitive) lanza 400 | ✅ |
| 14 | updateActivities con nombre de actividad vacío lanza 400 | ✅ |
| 15 | updateActivities con porcentaje ≤ 0 lanza 400 | ✅ |
| 16 | updateGrade exitoso | ✅ |
| 17 | updateGrade con nota negativa lanza 400 | ✅ |
| 18 | updateGrade con nota null (borrado) exitoso | ✅ |
| 19 | updateGrade con estudiante inexistente lanza 404 | ✅ |
| 20 | updateGrade con actividad inexistente lanza 404 | ✅ |
| 21 | deleteActivity exitoso | ✅ |
| 22 | deleteActivity cuando es la última lanza 400 | ✅ |
| 23 | loadOwnedCourse con curso inexistente lanza 404 | ✅ |
| 24 | loadOwnedCourse con curso de otro docente lanza 403 | ✅ |
| 25 | findStudentByIdentifier con ID inexistente lanza 404 | ✅ |

### 1.3 ReportServiceTest (18 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | Exportar JSON genera estructura correcta | ✅ |
| 2 | Exportar JSON incluye promedios calculados | ✅ |
| 3 | Exportar JSON trata notas nulas como 0 | ✅ |
| 4 | Exportar HTML genera tabla con actividades y promedios con punto decimal | ❌ BUG-002 |
| 5 | Exportar HTML muestra "Sin nota" para grades null | ✅ |
| 6 | Exportar HTML incluye aviso de notas sin registrar | ✅ |
| 7 | Exportar HTML incluye promedios | ✅ |
| 8 | Exportar HTML escapa caracteres especiales (XSS) | ✅ |
| 9 | Exportar PDF genera bytes no vacíos | ✅ |
| 10 | Exportar PDF tiene content-type correcto | ✅ |
| 11 | Formato nulo lanza 400 | ✅ |
| 12 | Formato vacío lanza 400 | ✅ |
| 13 | Formato no soportado lanza 400 | ✅ |
| 14 | Estudiante no encontrado en curso lanza 404 | ✅ |
| 15 | Filename sanitiza caracteres especiales | ✅ |
| 16 | Promedio simple se calcula correctamente | ✅ |
| 17 | Promedio ponderado se calcula correctamente | ✅ |
| 18 | Sin actividades devuelve promedios en 0 | ✅ |

### 1.4 AuthControllerTest (11 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | POST /api/auth/register → 201 con sesión válida | ✅ |
| 2 | POST /api/auth/register sin username → 400 | ✅ |
| 3 | POST /api/auth/register sin password → 400 | ✅ |
| 4 | POST /api/auth/register con body vacío → 400 | ✅ |
| 5 | POST /api/auth/register duplicado → 409 | ✅ |
| 6 | POST /api/auth/login exitoso → 200 | ✅ |
| 7 | POST /api/auth/login credenciales inválidas → 401 | ✅ |
| 8 | GET /api/auth/session con token válido → 200 | ✅ |
| 9 | GET /api/auth/session sin header → 401 | ✅ |
| 10 | POST /api/auth/logout → 204 | ✅ |
| 11 | POST /api/auth/logout con token inválido → 401 | ✅ |

### 1.5 CourseControllerTest (13 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | GET /api/courses → 200 con lista | ✅ |
| 2 | GET /api/courses sin token → 401 | ✅ |
| 3 | POST /api/courses → 201 curso creado | ✅ |
| 4 | POST /api/courses con nombre vacío → 400 | ✅ |
| 5 | GET /api/courses/{id} → 200 detalle | ✅ |
| 6 | GET /api/courses/{id} inexistente → 404 | ✅ |
| 7 | GET /api/students/{id} → 200 estudiante encontrado | ✅ |
| 8 | POST /api/courses/{id}/students → 200 | ✅ |
| 9 | POST /api/courses/{id}/students con studentId vacío → 400 | ✅ |
| 10 | PUT /api/courses/{id}/activities → 200 | ✅ |
| 11 | PUT /api/courses/{id}/grades → 200 | ✅ |
| 12 | DELETE /api/courses/{id}/activities/{actId} → 200 | ✅ |
| 13 | DELETE última actividad → 400 con mensaje explicativo | ✅ |

### 1.6 ReportControllerTest (7 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | GET report?format=pdf → 200 con Content-Type application/pdf | ✅ |
| 2 | GET report?format=html → 200 con Content-Type text/html | ✅ |
| 3 | GET report?format=json → 200 con Content-Type application/json | ✅ |
| 4 | GET report sin token → 401 | ✅ |
| 5 | GET report sin parámetro format → 400 Bad Request | ❌ BUG-001 |
| 6 | GET report?format=xml (no soportado) → 400 | ✅ |
| 7 | GET report con estudiante inexistente → 404 | ✅ |

### 1.7 ApiExceptionHandlerTest (8 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | ApiException devuelve status y mensaje correctos | ✅ |
| 2 | ApiException incluye mapa de detalles personalizado | ✅ |
| 3 | ApiException sin detalles devuelve mapa vacío | ✅ |
| 4 | Exception genérica devuelve 500 con mensaje genérico | ✅ |
| 5 | Header X-Session-Token faltante devuelve 401 | ✅ |
| 6 | Otro header faltante devuelve 400 | ✅ |
| 7 | Body no legible devuelve 400 "Solicitud invalida" | ✅ |
| 8 | Respuesta de error contiene los 5 campos estándar | ✅ |

---

## 2. Frontend — Qué se Prueba

### 2.1 api.test.ts (18 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | getSessionToken retorna null sin token almacenado | ✅ |
| 2 | setSessionToken/getSessionToken ciclo de escritura-lectura | ✅ |
| 3 | clearSessionToken elimina el token | ✅ |
| 4 | ApiError es instancia de Error | ✅ |
| 5 | ApiError expone status y message | ✅ |
| 6 | ApiError tiene details vacío por defecto | ✅ |
| 7 | ApiError acepta details personalizado | ✅ |
| 8 | requestJson llama fetch con URL base correcta | ✅ |
| 9 | requestJson incluye X-Session-Token si existe | ✅ |
| 10 | requestJson NO incluye X-Session-Token si no existe | ✅ |
| 11 | requestJson agrega Content-Type cuando hay body | ✅ |
| 12 | requestJson retorna JSON parseado en éxito | ✅ |
| 13 | requestJson retorna undefined para 204 | ✅ |
| 14 | requestJson lanza ApiError con mensaje del JSON de error | ✅ |
| 15 | requestJson lanza ApiError con texto para respuesta no-JSON | ✅ |
| 16 | requestBlob retorna blob y filename de Content-Disposition | ✅ |
| 17 | requestBlob retorna filename null sin Content-Disposition | ✅ |
| 18 | requestBlob lanza ApiError en respuesta no-ok | ✅ |

### 2.2 grades-logic.test.ts (12 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | calculateAverage retorna null sin actividades | ✅ |
| 2 | calculateAverage calcula promedio simple correctamente | ✅ |
| 3 | calculateAverage trata notas faltantes como 0 | ✅ |
| 4 | calculateAverage retorna 0 sin ninguna nota | ✅ |
| 5 | calculateWeightedAverage retorna null sin actividades | ✅ |
| 6 | calculateWeightedAverage calcula ponderado correctamente | ✅ |
| 7 | calculateWeightedAverage trata notas faltantes como 0 | ✅ |
| 8 | calculateWeightedAverage retorna 0 sin notas | ✅ |
| 9 | hasEmptyGrades retorna false con todas las notas | ✅ |
| 10 | hasEmptyGrades retorna true con notas faltantes | ✅ |
| 11 | hasEmptyGrades retorna true sin ninguna nota | ✅ |
| 12 | hasEmptyGrades retorna false sin actividades | ✅ |

### 2.3 Login.test.tsx (7 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | Renderiza formulario con campos usuario y contraseña | ✅ |
| 2 | Muestra error al enviar con campos vacíos | ✅ |
| 3 | Muestra error con username solo espacios en blanco | ✅ |
| 4 | Login exitoso navega a /dashboard | ✅ |
| 5 | Login fallido muestra mensaje de error | ✅ |
| 6 | Botón se deshabilita durante el envío | ✅ |
| 7 | Click en "Regístrate" navega a /register | ✅ |

### 2.4 ProtectedRoute.test.tsx (3 tests)

| # | Caso de prueba | Resultado |
|---|---------------|:---------:|
| 1 | Renderiza children cuando está autenticado | ✅ |
| 2 | Redirige a /login cuando no está autenticado | ✅ |
| 3 | No renderiza nada mientras isLoading es true | ✅ |

---

## 3. Bugs Encontrados

### BUG-001: `MissingServletRequestParameterException` retorna 500 en vez de 400

| Campo | Detalle |
|-------|---------|
| **Severidad** | Media |
| **Componente** | `ApiExceptionHandler.java` |
| **Endpoint afectado** | `GET /api/courses/{courseId}/students/{studentId}/report` (sin `?format=`) |
| **Comportamiento esperado** | Cuando falta el query parameter obligatorio `format`, la API debería retornar **HTTP 400 Bad Request** con un mensaje como `"Falta el parametro requerido: format"`. |
| **Comportamiento actual** | Retorna **HTTP 500 Internal Server Error** con mensaje genérico `"Error interno del servidor"`. |
| **Causa raíz** | Spring lanza `MissingServletRequestParameterException` cuando falta un `@RequestParam` obligatorio. Sin embargo, `ApiExceptionHandler` no tiene un `@ExceptionHandler` específico para esta excepción, por lo que es capturada por el handler genérico `handleUnexpectedException(Exception)` que retorna 500. |
| **Archivo del test** | `ReportControllerTest.java` → test `shouldReturn400WithoutFormat()` (**falla intencionalmente**: el test valida el comportamiento esperado 400, pero el código actual retorna 500) |
| **Solución propuesta** | Agregar un handler dedicado en `ApiExceptionHandler`: |

```java
@ExceptionHandler(MissingServletRequestParameterException.class)
public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
    return ResponseEntity.badRequest()
            .body(buildPayload(HttpStatus.BAD_REQUEST,
                    "Falta el parametro requerido: " + ex.getParameterName(), Map.of()));
}
```

---

### BUG-002: Promedios en HTML del boletín usan locale del sistema (coma vs punto)

| Campo | Detalle |
|-------|---------|
| **Severidad** | Media |
| **Componente** | `ReportService.java` → método `renderHtml()` |
| **Comportamiento esperado** | Los promedios en el HTML del boletín deben mostrarse con punto decimal: `"80.00"`. |
| **Comportamiento actual** | En JVMs con locale `es_UY`, `es_ES`, `fr_FR`, etc., se muestra `"80,00"` (con coma). Las notas individuales y porcentajes de actividades sí usan `Locale.US` (líneas 128-129), pero los promedios general y ponderado usan `String.formatted()` (línea 187) que hereda el locale del JVM. |
| **Causa raíz** | `String.formatted("%.2f", ...)` delega a `String.format(Locale.getDefault(), ...)`. El formato de número depende del locale de la máquina. Las líneas 128-129 usan `String.format(Locale.US, ...)` correctamente, pero el text block del template (líneas 179-180 con `%.2f`) se formatea con `""".formatted(...)` sin locale explícito. |
| **Archivo del test** | `ReportServiceTest.java` → test de promedio en HTML (**falla intencionalmente** en locales con coma: el test valida el formato esperado `"80.00"` con punto decimal) |
| **Solución propuesta** | Reemplazar `""".formatted(...)` por `String.format(Locale.US, template, ...)`: |

```java
// Antes (línea 187):
""".formatted(
    escape(payload.course()),
    ...
    payload.generalAverage(),
    payload.weightedAverage(),
    ...
);

// Después:
String.format(Locale.US, """
    ...template...
    """,
    escape(payload.course()),
    ...
    payload.generalAverage(),
    payload.weightedAverage(),
    ...
);
```

---

## 4. Oportunidades de Mejora

### 4.1 Inyección de `PasswordEncoder` en `AuthService`

| Campo | Detalle |
|-------|---------|
| **Archivo** | `AuthService.java` |
| **Situación actual** | `PasswordEncoder` se instancia inline como `new BCryptPasswordEncoder()` dentro de la clase. |
| **Problema** | Imposibilita sustituirlo en tests (por ejemplo, para usar un encoder más rápido). Viola el principio de inversión de dependencias. |
| **Mejora propuesta** | Declarar un `@Bean PasswordEncoder` en una clase `@Configuration` e inyectarlo por constructor en `AuthService`. |

```java
// SecurityConfig.java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// AuthService.java — inyectar en constructor
private final PasswordEncoder passwordEncoder;

public AuthService(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
    this.jdbc = jdbc;
    this.passwordEncoder = passwordEncoder;
}
```

---

### 4.2 Repositorios JPA sin uso — Acoplamiento directo a JdbcTemplate

| Campo | Detalle |
|-------|---------|
| **Archivos** | `AuthService.java`, `CourseService.java`, `ReportService.java` |
| **Situación actual** | Los servicios ejecutan SQL crudo con `JdbcTemplate` directamente. Existen clases de repositorio JPA en el proyecto pero no se utilizan. |
| **Problema** | Las queries SQL están acopladas dentro de la lógica de negocio, lo que dificulta testear la lógica aislada del acceso a datos. Cambiar el esquema requiere buscar SQL disperso en los services. |
| **Mejora propuesta** | Dos opciones: (a) Extraer el SQL a clases `@Repository` dedicadas e inyectarlas en los services, o (b) Migrar a los repositorios JPA que ya existen. Cualquiera de las dos opciones permite mockear la capa de datos de forma limpia. |

---

### 4.3 Métodos privados de renderizado en `ReportService`

| Campo | Detalle |
|-------|---------|
| **Archivo** | `ReportService.java` |
| **Situación actual** | `renderHtml()`, `renderPdf()` y la construcción JSON son métodos privados de `ReportService`. |
| **Problema** | No se pueden testear en aislamiento. Los tests de HTML requieren llamar al flujo completo (`exportStudentReport`) y verificar el string resultante con asserts frágiles de contenido. |
| **Mejora propuesta** | Extraer la lógica de renderizado a clases dedicadas (p. ej. `HtmlReportRenderer`, `PdfReportRenderer`) con visibilidad package-private o pública. Esto permite testear cada renderer con un `ReportPayload` de entrada sin mockear el service completo. |

---

### 4.4 Falta de handler para `MissingServletRequestParameterException`

> _(Ya documentado como BUG-001, aquí se refuerza como deuda técnica)_

El `ApiExceptionHandler` maneja 4 tipos de excepción, pero no cubre `MissingServletRequestParameterException`. Cualquier endpoint con `@RequestParam` obligatorio que reciba una petición sin ese parámetro retornará 500 en vez de 400, exponiendo un mensaje genérico que no ayuda al consumidor de la API a corregir su request.

---

## 5. Historial de Commits

| Commit | Mensaje | Tests |
|--------|---------|:-----:|
| `25f232c` | `test(auth): add unit tests for AuthService` | 20 |
| `8151a90` | `test(course): add unit tests for CourseService` | 25 |
| `b1d3091` | `test(report): add unit tests for ReportService` | 18 |
| `806650f` | `test(controllers): add MockMvc tests for Auth, Course, and Report controllers` | 31 |
| `13a3fda` | `test(common): add unit tests for ApiExceptionHandler` | 8 |
| `af4bfb3` | `test(frontend): add vitest infrastructure and 40 unit tests` | 40 |

---

## 6. Herramientas Utilizadas

| Ámbito | Herramienta | Versión |
|--------|-----------|---------|
| Backend test runner | JUnit 5 (Jupiter) | 5.10.x (via Spring Boot 3.3.5) |
| Backend mocking | Mockito | 5.x (via Spring Boot 3.3.5) |
| Backend HTTP tests | Spring MockMvc | 6.1.x |
| Frontend test runner | Vitest | 4.1.3 |
| Frontend DOM testing | @testing-library/react | 16.3.2 |
| Frontend assertions | @testing-library/jest-dom | 6.9.1 |
| Frontend user events | @testing-library/user-event | 14.6.1 |
| Frontend DOM environment | jsdom | 29.0.2 |
