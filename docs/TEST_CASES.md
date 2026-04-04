# TEST_CASES.md — Matriz de Casos de Prueba (v2.0)

## EdTech: Reportes y Boletines Académicos

| Campo | Detalle |
|---|---|
| **Proyecto** | EdTech — Reportes y Boletines Académicos |
| **Versión** | 2.0 |
| **Fecha** | 4 de abril de 2026 |
| **Autor** | QA — Sebastián Stelmaj |
| **Referencia** | TEST_PLAN.md v1.0 |

---

## Convenciones

| Símbolo | Significado |
|---|---|
| **Estado** | `Sin ejecutar` — Todos los casos quedan documentados sin ejecución en esta versión |
| **Prioridad** | `Crítica` · `Alta` · `Media` · `Baja` |
| **Prefijo API-** | Caso de prueba de integración de API (automatizar con **Karate**) |
| **Prefijo E2E-** | Caso de prueba de extremo a extremo (automatizar con **SerenityBDD + Cucumber**) |
| **Prefijo API-SEC-** | Caso de prueba de seguridad transversal sobre la API |
| **Acción complementaria** | Cada TC debe registrarse como sub-tarea dentro de su HU correspondiente en GitHub Projects |

---

## Pirámide de Pruebas — Criterio de distribución

| Nivel | % del total | Responsable | Herramienta | Documentado aquí |
|---|---|---|---|---|
| **Unitarias** | ~85 % | DEV | JUnit / framework del proyecto | No — responsabilidad del DEV (cobertura ≥ 80 %) |
| **Integración (API)** | ~15 % | QA | **Karate** | **Sí** — 40 casos (API-001 a API-SEC-001) |
| **E2E (flujos críticos)** | ~5 % | QA | **SerenityBDD + Cucumber** | **Sí** — 4 casos (E2E-001 a E2E-004) |

> Las pruebas unitarias **no se documentan** en este archivo. Son responsabilidad del equipo de desarrollo y se validan mediante el criterio de cobertura mínima del 80 % definido en el DoD.
>
> Este documento contiene **exclusivamente** los casos de prueba de Integración (API) y los flujos E2E críticos que son responsabilidad del QA.

---

## Superficie de API bajo prueba

| # | Método | Endpoint | Controller | HU principal |
|---|---|---|---|---|
| 1 | POST | `/api/auth/register` | AuthController | HDU_1 |
| 2 | POST | `/api/auth/login` | AuthController | HDU_2 |
| 3 | GET | `/api/auth/session` | AuthController | HDU_2 |
| 4 | POST | `/api/auth/logout` | AuthController | HDU_2 |
| 5 | GET | `/api/courses` | CourseController | HDU_3 |
| 6 | POST | `/api/courses` | CourseController | HDU_3 |
| 7 | GET | `/api/courses/{courseId}` | CourseController | HDU_4 |
| 8 | GET | `/api/students/{studentId}` | CourseController | HDU_5 |
| 9 | POST | `/api/courses/{courseId}/students` | CourseController | HDU_5 |
| 10 | PUT | `/api/courses/{courseId}/activities` | CourseController | HDU_11 / HDU_12 / HDU_13 |
| 11 | PUT | `/api/courses/{courseId}/grades` | CourseController | HDU_14 |
| 12 | GET | `/api/courses/{courseId}/students/{studentId}/report?format=` | ReportController | HDU_15 |

> **Base URL**: `http://localhost:8080/api` · **Header de sesión**: `X-Session-Token`

---

## Índice de Casos de Prueba

### Parte 1 — Integración API (Karate)

| # | ID | Endpoint | Escenario | HU |
|---|---|---|---|---|
| 1 | API-001 | `POST /auth/register` | Registro exitoso | HDU_1 |
| 2 | API-002 | `POST /auth/register` | Campos vacíos | HDU_1 |
| 3 | API-003 | `POST /auth/register` | Username duplicado | HDU_1 |
| 4 | API-004 | `POST /auth/login` | Login exitoso | HDU_2 |
| 5 | API-005 | `POST /auth/login` | Credenciales inválidas | HDU_2 |
| 6 | API-006 | `POST /auth/login` | Campos vacíos | HDU_2 |
| 7 | API-007 | `GET /auth/session` | Sesión válida | HDU_2 |
| 8 | API-008 | `GET /auth/session` | Token inválido | HDU_2 |
| 9 | API-009 | `POST /auth/logout` | Logout exitoso | HDU_2 |
| 10 | API-010 | `POST /auth/logout` | Token inválido | HDU_2 |
| 11 | API-011 | `GET /courses` | Listar cursos del docente | HDU_3 |
| 12 | API-012 | `POST /courses` | Crear curso exitosamente | HDU_3 |
| 13 | API-013 | `POST /courses` | Título duplicado | HDU_3 |
| 14 | API-014 | `POST /courses` | Título vacío | HDU_3 |
| 15 | API-015 | `GET /courses/{courseId}` | Detalle de curso existente | HDU_4 |
| 16 | API-016 | `GET /courses/{courseId}` | Curso inexistente | HDU_4 |
| 17 | API-017 | `GET /students/{studentId}` | Estudiante existente | HDU_5 |
| 18 | API-018 | `GET /students/{studentId}` | Estudiante inexistente | HDU_5 |
| 19 | API-019 | `POST /courses/{id}/students` | Inscribir estudiante nuevo | HDU_5 |
| 20 | API-020 | `POST /courses/{id}/students` | Campos obligatorios vacíos | HDU_5 |
| 21 | API-021 | `POST /courses/{id}/students` | Estudiante ya inscrito | HDU_5 |
| 22 | API-022 | `POST /courses/{id}/students` | Estudiante existente (autocomplete) | HDU_5 |
| 23 | API-023 | `PUT /courses/{id}/activities` | Definir programa válido (100 %) | HDU_11 |
| 24 | API-024 | `PUT /courses/{id}/activities` | Suma ≠ 100 % | HDU_11 |
| 25 | API-025 | `PUT /courses/{id}/activities` | Nombre de instancia vacío | HDU_11 |
| 26 | API-026 | `PUT /courses/{id}/activities` | Nombres duplicados | HDU_11 |
| 27 | API-027 | `PUT /courses/{id}/activities` | Ponderación ≤ 0 | HDU_11 |
| 28 | API-028 | `PUT /courses/{id}/activities` | Actualizar ponderación válida | HDU_13 |
| 29 | API-029 | `PUT /courses/{id}/activities` | Eliminar instancia con redistribución | HDU_12 |
| 30 | API-030 | `PUT /courses/{id}/grades` | Nota válida (≥ 0) | HDU_14 |
| 31 | API-031 | `PUT /courses/{id}/grades` | Nota negativa | HDU_14 |
| 32 | API-032 | `PUT /courses/{id}/grades` | Nota con caracteres no numéricos | HDU_14 |
| 33 | API-033 | `PUT /courses/{id}/grades` | Nota nula (vacía = 0 en promedio) | HDU_14 |
| 34 | API-034 | `PUT /activities` + `GET /courses/{id}` | Recálculo de promedios tras cambio de pesos | HDU_13 × HDU_14 |
| 35 | API-035 | `GET /.../report?format=pdf` | Exportar boletín PDF | HDU_15 |
| 36 | API-036 | `GET /.../report?format=html` | Exportar boletín HTML | HDU_15 |
| 37 | API-037 | `GET /.../report?format=json` | Exportar boletín JSON | HDU_15 |
| 38 | API-038 | `GET /.../report?format=xml` | Formato no soportado | HDU_15 |
| 39 | API-039 | `GET /.../report` | Boletín con notas vacías (advertencia) | HDU_15 |
| 40 | API-SEC-001 | Todos los protegidos | Acceso sin token a endpoints protegidos | Transversal |

### Parte 2 — E2E Flujos Críticos (SerenityBDD)

| # | ID | Flujo | HU cubiertas |
|---|---|---|---|
| 41 | E2E-001 | Flujo crítico completo del MVP | HDU_1 → HDU_2 → HDU_3 → HDU_5 → HDU_11 → HDU_14 → HDU_15 |
| 42 | E2E-002 | Autenticación y protección de rutas | HDU_1 → HDU_2 |
| 43 | E2E-003 | Gestión de programa evaluativo con recálculo | HDU_11 → HDU_13 → HDU_12 → HDU_14 |
| 44 | E2E-004 | Calificación parcial y reporte con advertencia | HDU_5 → HDU_14 → HDU_15 |

**Total de casos de prueba documentados: 44** (40 integración API + 4 E2E)

---

# PARTE 1 — Pruebas de Integración API (Karate)

---

# 1.1 Autenticación y Sesión (`/api/auth`)

---

## API-001 — Registro exitoso de docente

| Campo | Detalle |
|---|---|
| **ID** | API-001 |
| **Endpoint** | `POST /api/auth/register` |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Precondiciones** | No existe un usuario con el username "docente_juan" |
| **Request body** | `{ "username": "docente_juan", "password": "Pass1234" }` |
| **Response esperado** | Status `201 Created` · Body contiene `token` (no nulo) y `username` = "docente_juan" |
| **Validaciones Karate** | `status 201` · `response.token != null` · `response.username == 'docente_juan'` |
| **Estado** | Sin ejecutar |

---

## API-002 — Registro con campos vacíos

| Campo | Detalle |
|---|---|
| **ID** | API-002 |
| **Endpoint** | `POST /api/auth/register` |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Precondiciones** | Ninguna |
| **Request body** | Escenario A: `{ "username": "docente_juan", "password": "" }` · Escenario B: `{ "username": "", "password": "Pass1234" }` |
| **Response esperado** | Status `400 Bad Request` · Body contiene `message` indicando el campo obligatorio faltante |
| **Validaciones Karate** | `status 400` · `response.message != null` |
| **Estado** | Sin ejecutar |

---

## API-003 — Registro con username duplicado

| Campo | Detalle |
|---|---|
| **ID** | API-003 |
| **Endpoint** | `POST /api/auth/register` |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Precondiciones** | Existe un usuario registrado con username "docente_juan" |
| **Request body** | `{ "username": "docente_juan", "password": "OtraClave99" }` |
| **Response esperado** | Status `409 Conflict` · Body contiene `message` indicando que el username ya existe |
| **Validaciones Karate** | `status 409` · `response.message contains 'already'` o equivalente |
| **Estado** | Sin ejecutar |

---

## API-004 — Login exitoso

| Campo | Detalle |
|---|---|
| **ID** | API-004 |
| **Endpoint** | `POST /api/auth/login` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Crítica |
| **Precondiciones** | Existe un usuario registrado con username "docente_juan" y password "Pass1234" |
| **Request body** | `{ "username": "docente_juan", "password": "Pass1234" }` |
| **Response esperado** | Status `200 OK` · Body contiene `token` (no nulo) y `username` = "docente_juan" |
| **Validaciones Karate** | `status 200` · `response.token != null` · `response.username == 'docente_juan'` |
| **Estado** | Sin ejecutar |

---

## API-005 — Login con credenciales inválidas

| Campo | Detalle |
|---|---|
| **ID** | API-005 |
| **Endpoint** | `POST /api/auth/login` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Crítica |
| **Precondiciones** | No existe un usuario con las credenciales a utilizar |
| **Request body** | `{ "username": "usuario_falso", "password": "ClaveIncorrecta" }` |
| **Response esperado** | Status `401 Unauthorized` · Body contiene mensaje genérico (no revela si falló usuario o contraseña) |
| **Validaciones Karate** | `status 401` · `response.message !contains 'username'` · `response.message !contains 'password'` |
| **Estado** | Sin ejecutar |

---

## API-006 — Login con campos vacíos

| Campo | Detalle |
|---|---|
| **ID** | API-006 |
| **Endpoint** | `POST /api/auth/login` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Request body** | `{ "username": "", "password": "Pass1234" }` |
| **Response esperado** | Status `400 Bad Request` · Body indica el campo faltante |
| **Validaciones Karate** | `status 400` · `response.message != null` |
| **Estado** | Sin ejecutar |

---

## API-007 — Validar sesión activa

| Campo | Detalle |
|---|---|
| **ID** | API-007 |
| **Endpoint** | `GET /api/auth/session` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Precondiciones** | Se realizó login exitoso y se obtuvo un token válido |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `200 OK` · Body contiene `token` y `username` del usuario autenticado |
| **Validaciones Karate** | `status 200` · `response.username == 'docente_juan'` |
| **Estado** | Sin ejecutar |

---

## API-008 — Validar sesión con token inválido

| Campo | Detalle |
|---|---|
| **ID** | API-008 |
| **Endpoint** | `GET /api/auth/session` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Headers** | `X-Session-Token: token_invalido_123` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `401 Unauthorized` |
| **Validaciones Karate** | `status 401` |
| **Estado** | Sin ejecutar |

---

## API-009 — Logout exitoso

| Campo | Detalle |
|---|---|
| **ID** | API-009 |
| **Endpoint** | `POST /api/auth/logout` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Precondiciones** | Se realizó login exitoso y se obtuvo un token válido |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `204 No Content` · El token queda invalidado para futuras peticiones |
| **Validaciones Karate** | `status 204` · Petición posterior con el mismo token retorna `401` |
| **Estado** | Sin ejecutar |

---

## API-010 — Logout con token inválido

| Campo | Detalle |
|---|---|
| **ID** | API-010 |
| **Endpoint** | `POST /api/auth/logout` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Media |
| **Precondiciones** | Ninguna |
| **Headers** | `X-Session-Token: token_invalido_123` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `401 Unauthorized` |
| **Validaciones Karate** | `status 401` |
| **Estado** | Sin ejecutar |

---

# 1.2 Gestión de Cursos (`/api/courses`)

---

## API-011 — Listar cursos del docente autenticado

| Campo | Detalle |
|---|---|
| **ID** | API-011 |
| **Endpoint** | `GET /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe al menos un curso creado por el docente |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `200 OK` · Body contiene un array con los cursos del docente (cada item tiene `id` y `name`) |
| **Validaciones Karate** | `status 200` · `response[0].id != null` · `response[0].name != null` |
| **Estado** | Sin ejecutar |

---

## API-012 — Crear curso exitosamente

| Campo | Detalle |
|---|---|
| **ID** | API-012 |
| **Endpoint** | `POST /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. No existe un curso con el nombre "Matemáticas 101" para este docente |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "name": "Matemáticas 101" }` |
| **Response esperado** | Status `201 Created` · Body contiene el curso creado con `id` y `name` = "Matemáticas 101" |
| **Validaciones Karate** | `status 201` · `response.id != null` · `response.name == 'Matemáticas 101'` |
| **Estado** | Sin ejecutar |

---

## API-013 — Crear curso con título duplicado

| Campo | Detalle |
|---|---|
| **ID** | API-013 |
| **Endpoint** | `POST /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Ya existe un curso con nombre "Matemáticas 101" para este docente |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "name": "Matemáticas 101" }` |
| **Response esperado** | Status `409 Conflict` · Body contiene `message` indicando que el curso ya existe |
| **Validaciones Karate** | `status 409` |
| **Estado** | Sin ejecutar |

---

## API-014 — Crear curso con título vacío

| Campo | Detalle |
|---|---|
| **ID** | API-014 |
| **Endpoint** | `POST /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "name": "" }` |
| **Response esperado** | Status `400 Bad Request` · Body indica que el nombre es obligatorio |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-015 — Obtener detalle de curso existente

| Campo | Detalle |
|---|---|
| **ID** | API-015 |
| **Endpoint** | `GET /api/courses/{courseId}` |
| **HU asociada** | HDU_4 — Consultar curso |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso creado con estudiantes, programa y notas |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `200 OK` · Body contiene `id`, `name`, `students[]`, `activities[]` y promedios por estudiante |
| **Validaciones Karate** | `status 200` · `response.id != null` · `response.students != null` · `response.activities != null` |
| **Estado** | Sin ejecutar |

---

## API-016 — Obtener detalle de curso inexistente

| Campo | Detalle |
|---|---|
| **ID** | API-016 |
| **Endpoint** | `GET /api/courses/{courseId}` |
| **HU asociada** | HDU_4 — Consultar curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. No existe un curso con el ID proporcionado |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, `courseId` = "curso_inexistente") |
| **Response esperado** | Status `404 Not Found` |
| **Validaciones Karate** | `status 404` |
| **Estado** | Sin ejecutar |

---

# 1.3 Gestión de Estudiantes (`/api/students`, `/api/courses/{id}/students`)

---

## API-017 — Buscar estudiante existente por ID

| Campo | Detalle |
|---|---|
| **ID** | API-017 |
| **Endpoint** | `GET /api/students/{studentId}` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un estudiante con ID "1098765432" registrado en el sistema |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body) |
| **Response esperado** | Status `200 OK` · Body contiene `studentId`, `name` y `email` del estudiante |
| **Validaciones Karate** | `status 200` · `response.studentId == '1098765432'` · `response.name != null` · `response.email != null` |
| **Estado** | Sin ejecutar |

---

## API-018 — Buscar estudiante inexistente

| Campo | Detalle |
|---|---|
| **ID** | API-018 |
| **Endpoint** | `GET /api/students/{studentId}` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. No existe un estudiante con el ID proporcionado |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, `studentId` = "0000000000") |
| **Response esperado** | Status `404 Not Found` |
| **Validaciones Karate** | `status 404` |
| **Estado** | Sin ejecutar |

---

## API-019 — Inscribir estudiante nuevo al curso (alta on-the-fly)

| Campo | Detalle |
|---|---|
| **ID** | API-019 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso. No existe un estudiante con ID "1098765432" |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "name": "María López García", "email": "maria.lopez@correo.com" }` |
| **Response esperado** | Status `200 OK` · Body contiene el detalle del curso actualizado con el nuevo estudiante en la lista |
| **Validaciones Karate** | `status 200` · `response.students[*].studentId contains '1098765432'` |
| **Estado** | Sin ejecutar |

---

## API-020 — Inscribir estudiante con campos obligatorios vacíos

| Campo | Detalle |
|---|---|
| **ID** | API-020 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "name": "", "email": "maria.lopez@correo.com" }` |
| **Response esperado** | Status `400 Bad Request` · Body indica el campo obligatorio faltante. El estudiante NO se crea en el sistema |
| **Validaciones Karate** | `status 400` · Verificar con `GET /api/students/1098765432` que retorna `404` (no se creó parcialmente) |
| **Estado** | Sin ejecutar |

---

## API-021 — Inscribir estudiante ya inscrito en el curso

| Campo | Detalle |
|---|---|
| **ID** | API-021 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso. El estudiante "1098765432" ya está inscrito en el curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "name": "María López García", "email": "maria.lopez@correo.com" }` |
| **Response esperado** | Status `409 Conflict` · Body indica que el estudiante ya se encuentra inscrito |
| **Validaciones Karate** | `status 409` |
| **Estado** | Sin ejecutar |

---

## API-022 — Inscribir estudiante existente en el sistema (autocomplete)

| Campo | Detalle |
|---|---|
| **ID** | API-022 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso. El estudiante "1098765432" ya existe en el sistema pero NO está inscrito en este curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "name": "María López García", "email": "maria.lopez@correo.com" }` |
| **Response esperado** | Status `200 OK` · Body contiene el curso actualizado con el estudiante existente inscrito. No se duplica el registro global del estudiante |
| **Validaciones Karate** | `status 200` · `response.students[*].studentId contains '1098765432'` |
| **Estado** | Sin ejecutar |

---

# 1.4 Programa Evaluativo (`/api/courses/{courseId}/activities`)

---

## API-023 — Definir programa válido (suma = 100 %)

| Campo | Detalle |
|---|---|
| **ID** | API-023 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso sin programa definido |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `[{ "name": "Parcial 1", "percentage": 30 }, { "name": "Parcial 2", "percentage": 30 }, { "name": "Examen Final", "percentage": 40 }]` |
| **Response esperado** | Status `200 OK` · Body contiene el curso con 3 instancias evaluatorias definidas (30 % + 30 % + 40 %) |
| **Validaciones Karate** | `status 200` · `response.activities.length == 3` · Cada actividad tiene `id`, `name` y `percentage` |
| **Estado** | Sin ejecutar |

---

## API-024 — Definir programa con suma ≠ 100 %

| Campo | Detalle |
|---|---|
| **ID** | API-024 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `[{ "name": "Parcial 1", "percentage": 40 }, { "name": "Parcial 2", "percentage": 40 }]` (suma = 80 %) |
| **Response esperado** | Status `400 Bad Request` · Body indica que la suma de ponderaciones debe ser exactamente 100 % |
| **Validaciones Karate** | `status 400` · `response.message contains '100'` |
| **Estado** | Sin ejecutar |

---

## API-025 — Definir programa con nombre de instancia vacío

| Campo | Detalle |
|---|---|
| **ID** | API-025 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `[{ "name": "", "percentage": 50 }, { "name": "Examen Final", "percentage": 50 }]` |
| **Response esperado** | Status `400 Bad Request` · Body indica que no se permiten instancias sin nombre |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-026 — Definir programa con nombres de instancias duplicados

| Campo | Detalle |
|---|---|
| **ID** | API-026 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `[{ "name": "Parcial", "percentage": 50 }, { "name": "Parcial", "percentage": 50 }]` |
| **Response esperado** | Status `400 Bad Request` · Body indica que no se permiten nombres duplicados |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-027 — Definir programa con ponderación ≤ 0

| Campo | Detalle |
|---|---|
| **ID** | API-027 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | Escenario A: `[{ "name": "Parcial 1", "percentage": 0 }, { "name": "Final", "percentage": 100 }]` · Escenario B: `[{ "name": "Parcial 1", "percentage": -10 }, { "name": "Final", "percentage": 110 }]` |
| **Response esperado** | Status `400 Bad Request` · Body indica que cada instancia debe tener ponderación mayor al 0 % |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-028 — Actualizar ponderación de instancias existentes (suma = 100 %)

| Campo | Detalle |
|---|---|
| **ID** | API-028 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_13 — Actualizar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa: Parcial 1 (30 %), Parcial 2 (30 %), Examen Final (40 %) |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `[{ "id": "<id_parcial1>", "name": "Parcial 1", "percentage": 20 }, { "id": "<id_parcial2>", "name": "Parcial 2", "percentage": 40 }, { "id": "<id_final>", "name": "Examen Final", "percentage": 40 }]` |
| **Response esperado** | Status `200 OK` · Body refleja las nuevas ponderaciones (20 % + 40 % + 40 %). Si existían notas, los promedios fueron recalculados |
| **Validaciones Karate** | `status 200` · Verificar que las ponderaciones coinciden con las enviadas |
| **Estado** | Sin ejecutar |

---

## API-029 — Eliminar instancia con redistribución válida (suma = 100 %)

| Campo | Detalle |
|---|---|
| **ID** | API-029 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_12 — Eliminar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa de 3 instancias: Parcial 1 (30 %), Parcial 2 (30 %), Examen Final (40 %) |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `[{ "id": "<id_parcial1>", "name": "Parcial 1", "percentage": 40 }, { "id": "<id_final>", "name": "Examen Final", "percentage": 60 }]` (se omite Parcial 2) |
| **Response esperado** | Status `200 OK` · Body contiene solo 2 instancias (40 % + 60 %). "Parcial 2" ya no existe |
| **Validaciones Karate** | `status 200` · `response.activities.length == 2` · Ninguna actividad se llama "Parcial 2" |
| **Estado** | Sin ejecutar |

---

# 1.5 Calificaciones (`/api/courses/{courseId}/grades`)

---

## API-030 — Registrar nota válida (≥ 0)

| Campo | Detalle |
|---|---|
| **ID** | API-030 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido (Parcial 1: 30 %, Parcial 2: 30 %, Examen Final: 40 %). Existe un estudiante inscrito |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "activityId": "<id_parcial1>", "grade": 4.5 }` |
| **Response esperado** | Status `200 OK` · Body contiene el curso actualizado con la nota 4.5 registrada y los promedios recalculados |
| **Validaciones Karate** | `status 200` · La nota del estudiante en la actividad = 4.5 · Los promedios del estudiante reflejan el nuevo cálculo |
| **Estado** | Sin ejecutar |

---

## API-031 — Registrar nota negativa

| Campo | Detalle |
|---|---|
| **ID** | API-031 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante inscrito |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "activityId": "<id_parcial1>", "grade": -2 }` |
| **Response esperado** | Status `400 Bad Request` · Body indica que no se permiten notas negativas |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-032 — Registrar nota con caracteres no numéricos

| Campo | Detalle |
|---|---|
| **ID** | API-032 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante inscrito |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "activityId": "<id_parcial1>", "grade": "abc#!" }` |
| **Response esperado** | Status `400 Bad Request` · Body indica error de formato (valor no numérico) |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-033 — Registrar nota nula (vacía = 0 en promedio)

| Campo | Detalle |
|---|---|
| **ID** | API-033 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa. Existe un estudiante con una nota previa en otra instancia (ej: Parcial 1 = 4.5) |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | `{ "studentId": "1098765432", "activityId": "<id_parcial2>", "grade": null }` |
| **Response esperado** | Status `200 OK` · Body contiene el curso actualizado. La nota de Parcial 2 queda marcada como vacía. Los promedios se calculan tratando la nota nula como 0 |
| **Validaciones Karate** | `status 200` · El promedio ponderado del estudiante refleja 0 en la ponderación de Parcial 2 |
| **Estado** | Sin ejecutar |

---

## API-034 — Recálculo de promedios tras cambio de ponderaciones

| Campo | Detalle |
|---|---|
| **ID** | API-034 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` → `GET /api/courses/{courseId}` |
| **HU asociada** | HDU_13 × HDU_14 — Actualización de programa con notas existentes |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa (Parcial 1: 30 %, Parcial 2: 30 %, Final: 40 %). Existe un estudiante con notas: Parcial 1 = 4.0, Parcial 2 = 3.0, Final = 5.0. Promedio ponderado previo = (4.0×0.30 + 3.0×0.30 + 5.0×0.40) = 4.10 |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Paso 1 — Request** | `PUT /api/courses/{courseId}/activities` con body: `[{ "id": "<p1>", "name": "Parcial 1", "percentage": 20 }, { "id": "<p2>", "name": "Parcial 2", "percentage": 20 }, { "id": "<f>", "name": "Examen Final", "percentage": 60 }]` |
| **Paso 2 — Verificación** | `GET /api/courses/{courseId}` |
| **Response esperado** | Nuevo promedio ponderado = (4.0×0.20 + 3.0×0.20 + 5.0×0.60) = 4.40. Los promedios reflejan las nuevas ponderaciones |
| **Validaciones Karate** | `status 200` en ambos pasos · Promedio ponderado del estudiante = 4.40 (o equivalente según precisión decimal) |
| **Estado** | Sin ejecutar |

---

# 1.6 Exportación de Reportes (`/api/courses/{courseId}/students/{studentId}/report`)

---

## API-035 — Exportar boletín en formato PDF

| Campo | Detalle |
|---|---|
| **ID** | API-035 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=pdf` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido. Existe un estudiante con todas las notas registradas |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, query param `format=pdf`) |
| **Response esperado** | Status `200 OK` · Header `Content-Disposition: attachment` · Header `Content-Type: application/pdf` · Body binario no vacío |
| **Validaciones Karate** | `status 200` · `responseHeaders['Content-Type'] contains 'pdf'` · `responseBytes.length > 0` |
| **Estado** | Sin ejecutar |

---

## API-036 — Exportar boletín en formato HTML

| Campo | Detalle |
|---|---|
| **ID** | API-036 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=html` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante con notas completas |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, query param `format=html`) |
| **Response esperado** | Status `200 OK` · Header `Content-Type: text/html` · Body contiene HTML válido con datos del estudiante |
| **Validaciones Karate** | `status 200` · `responseHeaders['Content-Type'] contains 'html'` · `response contains '<html'` o equivalente |
| **Estado** | Sin ejecutar |

---

## API-037 — Exportar boletín en formato JSON

| Campo | Detalle |
|---|---|
| **ID** | API-037 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=json` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante con notas completas |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, query param `format=json`) |
| **Response esperado** | Status `200 OK` · Header `Content-Type: application/json` · Body contiene JSON con datos del estudiante, notas por instancia y promedios |
| **Validaciones Karate** | `status 200` · `response.studentId != null` · `response.grades != null` · `response.averages != null` |
| **Estado** | Sin ejecutar |

---

## API-038 — Exportar boletín con formato no soportado

| Campo | Detalle |
|---|---|
| **ID** | API-038 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=xml` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Precondiciones** | El docente está autenticado. Existe un curso y un estudiante |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, query param `format=xml`) |
| **Response esperado** | Status `400 Bad Request` · Body indica que el formato no es soportado |
| **Validaciones Karate** | `status 400` |
| **Estado** | Sin ejecutar |

---

## API-039 — Exportar boletín de estudiante con notas vacías

| Campo | Detalle |
|---|---|
| **ID** | API-039 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=json` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Crítica |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa. Existe un estudiante con al menos una nota vacía (nula) |
| **Headers** | `X-Session-Token: <token_válido>` |
| **Request body** | — (sin body, query param `format=json`) |
| **Response esperado** | Status `200 OK` · Body contiene el boletín. Las notas vacías se reflejan como `null` o `0` según diseño. Los promedios tratan nulas como 0 |
| **Validaciones Karate** | `status 200` · Los promedios son coherentes con la "Regla de Oro" (nulas = 0) |
| **Estado** | Sin ejecutar |

---

# 1.7 Seguridad Transversal

---

## API-SEC-001 — Acceso a endpoints protegidos sin token de sesión

| Campo | Detalle |
|---|---|
| **ID** | API-SEC-001 |
| **Endpoints** | Todos los endpoints protegidos: `GET /api/courses`, `POST /api/courses`, `GET /api/courses/{id}`, `GET /api/students/{id}`, `POST /api/courses/{id}/students`, `PUT /api/courses/{id}/activities`, `PUT /api/courses/{id}/grades`, `GET /api/.../report` |
| **HU asociada** | Transversal — Seguridad de sesión |
| **Prioridad** | Crítica |
| **Precondiciones** | No se envía header `X-Session-Token` (o se envía vacío) |
| **Headers** | Sin `X-Session-Token` |
| **Request body** | Varía según endpoint |
| **Response esperado** | Status `401 Unauthorized` en **todos** los endpoints protegidos |
| **Validaciones Karate** | Iterar sobre cada endpoint protegido y verificar `status 401`. Usar `Scenario Outline` con tabla de endpoints |
| **Estado** | Sin ejecutar |

---

# PARTE 2 — Pruebas E2E — Flujos Críticos (SerenityBDD + Cucumber)

> Solo se documentan flujos de extremo a extremo que atraviesan múltiples historias de usuario y representan los caminos críticos del negocio. Las validaciones individuales por endpoint o pantalla se cubren en las pruebas de integración API (Parte 1).

---

## E2E-001 — Flujo crítico completo del MVP

| Campo | Detalle |
|---|---|
| **ID** | E2E-001 |
| **Tipo** | Flujo de extremo a extremo |
| **HU involucradas** | HDU_1 → HDU_2 → HDU_3 → HDU_5 → HDU_11 → HDU_14 → HDU_15 |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de registro **Y** me registro con credenciales válidas **Y** inicio sesión exitosamente **Cuando** creo un curso nuevo **Y** inscribo un estudiante en el curso **Y** defino un programa evaluativo con ponderaciones que suman 100 % **Y** registro las notas del estudiante en todas las instancias **Y** selecciono generar el boletín del estudiante en formato PDF **Entonces** el sistema descarga un boletín PDF que contiene el nombre del estudiante, sus notas, el promedio ponderado y el promedio general, todos coherentes con los datos ingresados |
| **Precondiciones** | Base de datos limpia. La aplicación está corriendo |
| **Datos de entrada** | `usuario`: "docente_test" · `contraseña`: "Pass1234" · `curso`: "Física 201" · `estudiante`: ID "5551234567", "Carlos Gómez", "carlos@correo.com" · `programa`: Parcial 1 (30 %), Parcial 2 (30 %), Final (40 %) · `notas`: Parcial 1 = 4.0, Parcial 2 = 3.5, Final = 4.8 · `formato boletín`: PDF |
| **Pasos de ejecución** | 1. Navegar a la pantalla de registro 2. Registrarse con "docente_test" / "Pass1234" 3. Iniciar sesión con las mismas credenciales 4. Verificar que se muestra el Dashboard 5. Crear el curso "Física 201" 6. Entrar al detalle del curso 7. Inscribir al estudiante "Carlos Gómez" (alta on-the-fly) 8. Definir el programa: Parcial 1 (30 %), Parcial 2 (30 %), Final (40 %) 9. Registrar notas: P1 = 4.0, P2 = 3.5, Final = 4.8 10. Verificar promedios: ponderado = 4.0×0.30 + 3.5×0.30 + 4.8×0.40 = 4.17 11. Generar boletín en formato PDF 12. Verificar que el archivo PDF se descarga correctamente |
| **Resultado esperado** | El flujo completo se ejecuta sin errores. Se descarga un boletín PDF con datos coherentes. El promedio ponderado del estudiante es correcto |
| **Estado** | Sin ejecutar |

---

## E2E-002 — Flujo de autenticación y protección de rutas

| Campo | Detalle |
|---|---|
| **ID** | E2E-002 |
| **Tipo** | Flujo de extremo a extremo |
| **HU involucradas** | HDU_1 → HDU_2 |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de registro **Cuando** me registro exitosamente **Y** inicio sesión **Entonces** debo ver el Dashboard **Cuando** cierro la sesión **Entonces** debo ser redirigido a la pantalla de login **Cuando** intento acceder directamente al Dashboard sin sesión activa **Entonces** el sistema debe redirigirme a la pantalla de login sin permitir el acceso |
| **Precondiciones** | Base de datos limpia. La aplicación está corriendo |
| **Datos de entrada** | `usuario`: "docente_seguridad" · `contraseña`: "Segura456" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de registro 2. Registrarse con "docente_seguridad" / "Segura456" 3. Iniciar sesión con las mismas credenciales 4. Verificar que se muestra el Dashboard 5. Cerrar sesión (logout) 6. Verificar que se redirige a la pantalla de login 7. Navegar directamente a la URL del Dashboard 8. Verificar que el sistema redirige a la pantalla de login |
| **Resultado esperado** | El ciclo completo de autenticación funciona: registro → login → acceso → logout → protección de ruta. No se permite el acceso al Dashboard sin sesión activa |
| **Estado** | Sin ejecutar |

---

## E2E-003 — Flujo de gestión de programa evaluativo con recálculo de promedios

| Campo | Detalle |
|---|---|
| **ID** | E2E-003 |
| **Tipo** | Flujo de extremo a extremo |
| **HU involucradas** | HDU_11 → HDU_14 → HDU_13 → HDU_12 |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que soy un docente autenticado con un curso que tiene un estudiante inscrito **Cuando** defino el programa con 3 instancias que suman 100 % **Y** registro notas en todas las instancias **Y** verifico los promedios **Y** edito las ponderaciones del programa (manteniendo suma = 100 %) **Entonces** los promedios del estudiante deben recalcularse automáticamente con las nuevas ponderaciones **Cuando** elimino una instancia evaluatoria y redistribuyo los pesos **Entonces** los promedios se actualizan correctamente y la instancia eliminada ya no aparece |
| **Precondiciones** | Docente autenticado. Curso creado con 1 estudiante inscrito. Sin programa definido |
| **Datos de entrada** | `programa inicial`: Parcial 1 (30 %), Parcial 2 (30 %), Final (40 %) · `notas`: P1 = 5.0, P2 = 3.0, Final = 4.0 · `programa editado`: Parcial 1 (20 %), Parcial 2 (20 %), Final (60 %) · `programa tras eliminación`: Parcial 1 (40 %), Final (60 %) |
| **Pasos de ejecución** | 1. Definir programa: P1 (30 %), P2 (30 %), Final (40 %) 2. Registrar notas: P1 = 5.0, P2 = 3.0, Final = 4.0 3. Verificar promedio ponderado = 5.0×0.30 + 3.0×0.30 + 4.0×0.40 = 4.00 4. Editar ponderaciones: P1 (20 %), P2 (20 %), Final (60 %) 5. Verificar nuevo promedio ponderado = 5.0×0.20 + 3.0×0.20 + 4.0×0.60 = 4.00 6. Eliminar "Parcial 2" y redistribuir: P1 (40 %), Final (60 %) 7. Verificar que "Parcial 2" ya no aparece 8. Verificar nuevo promedio ponderado = 5.0×0.40 + 4.0×0.60 = 4.40 |
| **Resultado esperado** | Los promedios se recalculan correctamente en cada cambio de ponderación. La instancia eliminada desaparece del programa. Todos los cambios se reflejan en tiempo real en la tabla de notas |
| **Estado** | Sin ejecutar |

---

## E2E-004 — Flujo de calificación parcial y reporte con advertencia de notas vacías

| Campo | Detalle |
|---|---|
| **ID** | E2E-004 |
| **Tipo** | Flujo de extremo a extremo |
| **HU involucradas** | HDU_5 → HDU_14 → HDU_15 |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que soy un docente autenticado con un curso, un programa definido y un estudiante inscrito **Cuando** registro notas en algunas instancias pero dejo al menos una vacía **Entonces** el promedio debe calcularse tratando las notas vacías como 0 **Y** las notas vacías deben resaltarse visualmente **Cuando** intento generar el boletín del estudiante **Entonces** el sistema debe mostrar una advertencia indicando que existen notas vacías **Cuando** confirmo la generación a pesar de la advertencia **Entonces** el boletín debe descargarse correctamente con los promedios coherentes |
| **Precondiciones** | Docente autenticado. Curso con programa: P1 (30 %), P2 (30 %), Final (40 %). Estudiante inscrito |
| **Datos de entrada** | `notas`: P1 = 4.5, P2 = nula (vacía), Final = 3.8 · `formato boletín`: JSON |
| **Pasos de ejecución** | 1. Registrar nota P1 = 4.5 2. Dejar P2 vacía (nula) 3. Registrar nota Final = 3.8 4. Verificar que P2 se resalta visualmente como vacía 5. Verificar promedio ponderado = 4.5×0.30 + 0×0.30 + 3.8×0.40 = 2.87 6. Seleccionar generar boletín para el estudiante 7. Verificar que aparece advertencia de notas vacías 8. Confirmar la generación 9. Seleccionar formato JSON 10. Verificar que el archivo JSON se descarga y contiene datos coherentes |
| **Resultado esperado** | Las notas vacías se tratan como 0 para promedios. La advertencia previa funciona. El boletín se genera correctamente tras la confirmación. El JSON contiene promedios coherentes con la "Regla de Oro" |
| **Estado** | Sin ejecutar |

---

# Resumen de cobertura

## Distribución por tipo de prueba

| Nivel | Herramienta | Casos documentados | % documentado | Responsable |
|---|---|---|---|---|
| Unitarias | JUnit | No documentados aquí | ~85 % del total | **DEV** |
| **Integración API** | **Karate** | **40** | ~15 % del total | **QA** |
| **E2E flujos críticos** | **SerenityBDD** | **4** | ~5 % del total | **QA** |
| | | **44 documentados** | | |

## Cobertura por HU (integración + E2E)

| HU | Casos API | Casos E2E | Total | Prioridad predominante |
|---|---|---|---|---|
| HDU_1 — Registrar usuarios | API-001, API-002, API-003 | E2E-001, E2E-002 | **5** | Crítica |
| HDU_2 — Iniciar sesión | API-004 a API-010 | E2E-001, E2E-002 | **9** | Crítica |
| HDU_3 — Crear un nuevo curso | API-011 a API-014 | E2E-001 | **5** | Crítica / Alta |
| HDU_4 — Consultar curso | API-015, API-016 | E2E-001 | **3** | Crítica / Alta |
| HDU_5 — Agregar estudiantes | API-017 a API-022 | E2E-001, E2E-004 | **8** | Crítica / Alta |
| HDU_11 — Definir programa | API-023 a API-027 | E2E-001, E2E-003 | **7** | Crítica / Alta |
| HDU_12 — Eliminar instancia | API-029 | E2E-003 | **2** | Crítica |
| HDU_13 — Actualizar instancia | API-028, API-034 | E2E-003 | **3** | Crítica |
| HDU_14 — Registrar nota | API-030 a API-033 | E2E-001, E2E-003, E2E-004 | **7** | Crítica / Alta |
| HDU_15 — Generar boletín | API-035 a API-039 | E2E-001, E2E-004 | **7** | Crítica / Alta |
| Transversal — Seguridad | API-SEC-001 | E2E-002 | **2** | Crítica |

## Cobertura de endpoints

| # | Endpoint | Casos API | Cobertura |
|---|---|---|---|
| 1 | `POST /api/auth/register` | API-001, API-002, API-003 | Happy path + validación + duplicado |
| 2 | `POST /api/auth/login` | API-004, API-005, API-006 | Happy path + credenciales inválidas + vacío |
| 3 | `GET /api/auth/session` | API-007, API-008 | Sesión válida + token inválido |
| 4 | `POST /api/auth/logout` | API-009, API-010 | Logout exitoso + token inválido |
| 5 | `GET /api/courses` | API-011 | Listar cursos |
| 6 | `POST /api/courses` | API-012, API-013, API-014 | Creación + duplicado + vacío |
| 7 | `GET /api/courses/{courseId}` | API-015, API-016 | Existente + inexistente |
| 8 | `GET /api/students/{studentId}` | API-017, API-018 | Existente + inexistente |
| 9 | `POST /api/courses/{id}/students` | API-019, API-020, API-021, API-022 | Nuevo + vacío + ya inscrito + autocomplete |
| 10 | `PUT /api/courses/{id}/activities` | API-023 a API-029 | Definir + validaciones + actualizar + eliminar |
| 11 | `PUT /api/courses/{id}/grades` | API-030 a API-034 | Nota válida + negativa + no numérica + nula + recálculo |
| 12 | `GET /.../report?format=` | API-035 a API-039 | PDF + HTML + JSON + formato inválido + notas vacías |
| — | Todos los protegidos | API-SEC-001 | Seguridad transversal sin token |

**12/12 endpoints cubiertos al 100 %**

---

## Notas finales

1. **Pirámide de pruebas**: este documento sigue la distribución ~85 % unitarias (DEV), ~15 % integración API (QA), ~5 % E2E (QA). Las pruebas unitarias son responsabilidad del DEV con cobertura ≥ 80 % y **no se documentan aquí**.

2. **E2E = flujos críticos únicamente**: los 4 casos E2E cubren los caminos de negocio completos del MVP. Las validaciones puntuales (campos vacíos, duplicados, formatos inválidos) se resuelven al nivel de integración API, evitando pruebas E2E frágiles y costosas de mantener.

3. **Karate como herramienta de integración**: los 40 casos API están diseñados para mapearse directamente a feature files de Karate. Los `API-SEC-001` se implementa con `Scenario Outline` para iterar sobre todos los endpoints protegidos.

4. **SerenityBDD como herramienta E2E**: los 4 flujos están escritos en estilo Gherkin para implementarse como escenarios Cucumber dentro de SerenityBDD.

5. **Estado uniforme**: todos los casos quedan en estado `Sin ejecutar` como corresponde a esta fase documental.

6. **Registro en GitHub Projects**: cada caso de prueba (API-001 a E2E-004) debe registrarse como sub-tarea dentro de su HU correspondiente en el tablero de GitHub Projects.
