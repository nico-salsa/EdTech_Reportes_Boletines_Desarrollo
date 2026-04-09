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
| **Escenario Gherkin** | **Dado** que no existe un docente registrado con ese nombre de usuario **Cuando** el docente se registra proporcionando un nombre de usuario y una contraseña válidos **Entonces** el sistema debe crear la cuenta exitosamente y entregarle un token de sesión |
| **Precondiciones** | No existe un usuario con el username "docente_juan" |
| **Datos de entrada** | `{ "username": "docente_juan", "password": "Pass1234" }` |
| **Pasos de ejecución** | 1. Enviar solicitud de registro con username y password válidos |
| **Resultado esperado** | Status `201 Created` · Body contiene `token` (no nulo) y `username` = "docente_juan" |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 201` · `response.token != null` · `response.username == 'docente_juan'` |

---

## API-002 — Registro con campos vacíos

| Campo | Detalle |
|---|---|
| **ID** | API-002 |
| **Endpoint** | `POST /api/auth/register` |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un docente intenta registrarse en el sistema **Cuando** envía los datos de registro con el nombre de usuario o la contraseña vacíos **Entonces** el sistema debe rechazar el registro e indicar cuál campo obligatorio falta |
| **Precondiciones** | Ninguna |
| **Datos de entrada** | Escenario A: `{ "username": "docente_juan", "password": "" }` · Escenario B: `{ "username": "", "password": "Pass1234" }` |
| **Pasos de ejecución** | 1. Enviar solicitud de registro con al menos un campo obligatorio vacío |
| **Resultado esperado** | Status `400 Bad Request` · Body contiene `message` indicando el campo obligatorio faltante |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` · `response.message != null` |

---

## API-003 — Registro con username duplicado

| Campo | Detalle |
|---|---|
| **ID** | API-003 |
| **Endpoint** | `POST /api/auth/register` |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que ya existe un docente registrado con un nombre de usuario específico **Cuando** otro docente intenta registrarse con el mismo nombre de usuario **Entonces** el sistema debe rechazar el registro e informar que ese nombre de usuario ya está en uso |
| **Precondiciones** | Existe un usuario registrado con username "docente_juan" |
| **Datos de entrada** | `{ "username": "docente_juan", "password": "OtraClave99" }` |
| **Pasos de ejecución** | 1. Registrar un usuario con username "docente_juan" 2. Intentar registrar otro usuario con el mismo username |
| **Resultado esperado** | Status `409 Conflict` · Body contiene `message` indicando que el username ya existe |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 409` · `response.message contains 'already'` o equivalente |

---

## API-004 — Login exitoso

| Campo | Detalle |
|---|---|
| **ID** | API-004 |
| **Endpoint** | `POST /api/auth/login` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que existe un docente registrado en el sistema **Cuando** el docente inicia sesión con sus credenciales correctas **Entonces** el sistema debe autenticarlo exitosamente y entregarle un token de sesión válido |
| **Precondiciones** | Existe un usuario registrado con username "docente_juan" y password "Pass1234" |
| **Datos de entrada** | `{ "username": "docente_juan", "password": "Pass1234" }` |
| **Pasos de ejecución** | 1. Registrar usuario previamente 2. Enviar solicitud de login con credenciales correctas |
| **Resultado esperado** | Status `200 OK` · Body contiene `token` (no nulo) y `username` = "docente_juan" |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.token != null` · `response.username == 'docente_juan'` |

---

## API-005 — Login con credenciales inválidas

| Campo | Detalle |
|---|---|
| **ID** | API-005 |
| **Endpoint** | `POST /api/auth/login` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un docente intenta iniciar sesión **Cuando** proporciona credenciales que no corresponden a ningún usuario registrado **Entonces** el sistema debe rechazar el acceso con un mensaje genérico que no revele si el error fue en el usuario o la contraseña |
| **Precondiciones** | No existe un usuario con las credenciales a utilizar |
| **Datos de entrada** | `{ "username": "usuario_falso", "password": "ClaveIncorrecta" }` |
| **Pasos de ejecución** | 1. Enviar solicitud de login con credenciales inválidas |
| **Resultado esperado** | Status `401 Unauthorized` · Body contiene mensaje genérico (no revela si falló usuario o contraseña) |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 401` · `response.message !contains 'username'` · `response.message !contains 'password'` |

---

## API-006 — Login con campos vacíos

| Campo | Detalle |
|---|---|
| **ID** | API-006 |
| **Endpoint** | `POST /api/auth/login` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente intenta iniciar sesión **Cuando** envía la solicitud con el nombre de usuario o la contraseña vacíos **Entonces** el sistema debe rechazar la solicitud e indicar que los campos son obligatorios |
| **Precondiciones** | Ninguna |
| **Datos de entrada** | `{ "username": "", "password": "Pass1234" }` |
| **Pasos de ejecución** | 1. Enviar solicitud de login con al menos un campo vacío |
| **Resultado esperado** | Status `400 Bad Request` · Body indica el campo faltante |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` · `response.message != null` |

---

## API-007 — Validar sesión activa

| Campo | Detalle |
|---|---|
| **ID** | API-007 |
| **Endpoint** | `GET /api/auth/session` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente ha iniciado sesión exitosamente y posee un token válido **Cuando** consulta el estado de su sesión **Entonces** el sistema debe confirmar que la sesión está activa y devolver los datos del docente autenticado |
| **Precondiciones** | Se realizó login exitoso y se obtuvo un token válido |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Sin body |
| **Pasos de ejecución** | 1. Realizar login exitoso y obtener token 2. Consultar sesión con el token obtenido |
| **Resultado esperado** | Status `200 OK` · Body contiene `token` y `username` del usuario autenticado |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.username == 'docente_juan'` |

---

## API-008 — Validar sesión con token inválido

| Campo | Detalle |
|---|---|
| **ID** | API-008 |
| **Endpoint** | `GET /api/auth/session` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un usuario posee un token de sesión inválido o expirado **Cuando** consulta el estado de su sesión **Entonces** el sistema debe rechazar la solicitud indicando que no está autorizado |
| **Precondiciones** | Ninguna |
| **Datos de entrada** | Header: `X-Session-Token: token_invalido_123` · Sin body |
| **Pasos de ejecución** | 1. Enviar solicitud de consulta de sesión con un token inválido |
| **Resultado esperado** | Status `401 Unauthorized` |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 401` |

---

## API-009 — Logout exitoso

| Campo | Detalle |
|---|---|
| **ID** | API-009 |
| **Endpoint** | `POST /api/auth/logout` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente tiene una sesión activa **Cuando** cierra su sesión **Entonces** el sistema debe invalidar su token de sesión y cualquier intento posterior de usar ese token debe ser rechazado |
| **Precondiciones** | Se realizó login exitoso y se obtuvo un token válido |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Sin body |
| **Pasos de ejecución** | 1. Realizar login exitoso y obtener token 2. Enviar solicitud de logout con el token 3. Intentar consultar sesión con el mismo token |
| **Resultado esperado** | Status `204 No Content` · El token queda invalidado para futuras peticiones |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 204` · Petición posterior con el mismo token retorna `401` |

---

## API-010 — Logout con token inválido

| Campo | Detalle |
|---|---|
| **ID** | API-010 |
| **Endpoint** | `POST /api/auth/logout` |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Media |
| **Escenario Gherkin** | **Dado** que un usuario posee un token de sesión inválido o expirado **Cuando** intenta cerrar sesión con ese token **Entonces** el sistema debe rechazar la solicitud indicando que no está autorizado |
| **Precondiciones** | Ninguna |
| **Datos de entrada** | Header: `X-Session-Token: token_invalido_123` · Sin body |
| **Pasos de ejecución** | 1. Enviar solicitud de logout con un token inválido |
| **Resultado esperado** | Status `401 Unauthorized` |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 401` |

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
| **Escenario Gherkin** | **Dado** que un docente está autenticado y tiene cursos creados **Cuando** consulta su listado de cursos **Entonces** el sistema debe devolver todos los cursos que le pertenecen con su identificador y nombre |
| **Precondiciones** | El docente está autenticado. Existe al menos un curso creado por el docente |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Sin body |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Crear al menos un curso 3. Consultar el listado de cursos |
| **Resultado esperado** | Status `200 OK` · Body contiene un array con los cursos del docente (cada item tiene `id` y `name`) |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response[0].id != null` · `response[0].name != null` |

---

## API-012 — Crear curso exitosamente

| Campo | Detalle |
|---|---|
| **ID** | API-012 |
| **Endpoint** | `POST /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un docente está autenticado y no tiene un curso con ese nombre **Cuando** crea un nuevo curso proporcionando un nombre válido **Entonces** el sistema debe crear el curso exitosamente y devolver sus datos con un identificador único |
| **Precondiciones** | El docente está autenticado. No existe un curso con el nombre "Matemáticas 101" para este docente |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "name": "Matemáticas 101" }` |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Enviar solicitud de creación de curso con nombre válido |
| **Resultado esperado** | Status `201 Created` · Body contiene el curso creado con `id` y `name` = "Matemáticas 101" |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 201` · `response.id != null` · `response.name == 'Matemáticas 101'` |

---

## API-013 — Crear curso con título duplicado

| Campo | Detalle |
|---|---|
| **ID** | API-013 |
| **Endpoint** | `POST /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente ya tiene un curso con un nombre específico **Cuando** intenta crear otro curso con el mismo nombre **Entonces** el sistema debe rechazar la creación e informar que ya existe un curso con ese nombre |
| **Precondiciones** | El docente está autenticado. Ya existe un curso con nombre "Matemáticas 101" para este docente |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "name": "Matemáticas 101" }` |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Crear curso "Matemáticas 101" 3. Intentar crear otro curso con el mismo nombre |
| **Resultado esperado** | Status `409 Conflict` · Body contiene `message` indicando que el curso ya existe |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 409` |

---

## API-014 — Crear curso con título vacío

| Campo | Detalle |
|---|---|
| **ID** | API-014 |
| **Endpoint** | `POST /api/courses` |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente está autenticado **Cuando** intenta crear un curso sin proporcionar un nombre **Entonces** el sistema debe rechazar la creación e indicar que el nombre del curso es obligatorio |
| **Precondiciones** | El docente está autenticado |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "name": "" }` |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Enviar solicitud de creación de curso con nombre vacío |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que el nombre es obligatorio |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-015 — Obtener detalle de curso existente

| Campo | Detalle |
|---|---|
| **ID** | API-015 |
| **Endpoint** | `GET /api/courses/{courseId}` |
| **HU asociada** | HDU_4 — Consultar curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un docente está autenticado y tiene un curso con estudiantes, programa y notas **Cuando** consulta el detalle de ese curso **Entonces** el sistema debe devolver la información completa incluyendo estudiantes, actividades evaluativas y promedios |
| **Precondiciones** | El docente está autenticado. Existe un curso creado con estudiantes, programa y notas |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Parámetro: `courseId` válido · Sin body |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Crear curso con estudiantes, programa y notas 3. Consultar detalle del curso |
| **Resultado esperado** | Status `200 OK` · Body contiene `id`, `name`, `students[]`, `activities[]` y promedios por estudiante |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.id != null` · `response.students != null` · `response.activities != null` |

---

## API-016 — Obtener detalle de curso inexistente

| Campo | Detalle |
|---|---|
| **ID** | API-016 |
| **Endpoint** | `GET /api/courses/{courseId}` |
| **HU asociada** | HDU_4 — Consultar curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente está autenticado **Cuando** intenta consultar el detalle de un curso que no existe **Entonces** el sistema debe informar que el curso no fue encontrado |
| **Precondiciones** | El docente está autenticado. No existe un curso con el ID proporcionado |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Parámetro: `courseId` = "curso_inexistente" · Sin body |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Consultar detalle de un curso con ID inexistente |
| **Resultado esperado** | Status `404 Not Found` |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 404` |

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
| **Escenario Gherkin** | **Dado** que existe un estudiante registrado en el sistema **Cuando** el docente busca al estudiante por su identificador **Entonces** el sistema debe devolver los datos del estudiante incluyendo su nombre y correo electrónico |
| **Precondiciones** | El docente está autenticado. Existe un estudiante con ID "1098765432" registrado en el sistema |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Parámetro: `studentId` = "1098765432" · Sin body |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Crear un estudiante previamente 3. Buscar al estudiante por su ID |
| **Resultado esperado** | Status `200 OK` · Body contiene `studentId`, `name` y `email` del estudiante |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.studentId == '1098765432'` · `response.name != null` · `response.email != null` |

---

## API-018 — Buscar estudiante inexistente

| Campo | Detalle |
|---|---|
| **ID** | API-018 |
| **Endpoint** | `GET /api/students/{studentId}` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que no existe un estudiante con el identificador proporcionado **Cuando** el docente busca al estudiante por ese identificador **Entonces** el sistema debe informar que el estudiante no fue encontrado |
| **Precondiciones** | El docente está autenticado. No existe un estudiante con el ID proporcionado |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Parámetro: `studentId` = "0000000000" · Sin body |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Buscar un estudiante con un ID que no existe |
| **Resultado esperado** | Status `404 Not Found` |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 404` |

---

## API-019 — Inscribir estudiante nuevo al curso (alta on-the-fly)

| Campo | Detalle |
|---|---|
| **ID** | API-019 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un docente tiene un curso y el estudiante no existe en el sistema **Cuando** inscribe al estudiante proporcionando su identificador, nombre y correo **Entonces** el sistema debe registrar al estudiante en el sistema e inscribirlo en el curso simultáneamente |
| **Precondiciones** | El docente está autenticado. Existe un curso. No existe un estudiante con ID "1098765432" |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "name": "María López García", "email": "maria.lopez@correo.com" }` |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Crear un curso 3. Inscribir estudiante nuevo con todos los datos completos |
| **Resultado esperado** | Status `200 OK` · Body contiene el detalle del curso actualizado con el nuevo estudiante en la lista |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.students[*].studentId contains '1098765432'` |

---

## API-020 — Inscribir estudiante con campos obligatorios vacíos

| Campo | Detalle |
|---|---|
| **ID** | API-020 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente tiene un curso **Cuando** intenta inscribir un estudiante con campos obligatorios vacíos **Entonces** el sistema debe rechazar la inscripción, indicar el campo faltante y no crear al estudiante parcialmente en el sistema |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "name": "", "email": "maria.lopez@correo.com" }` |
| **Pasos de ejecución** | 1. Autenticarse y crear un curso 2. Enviar solicitud de inscripción con nombre vacío 3. Verificar que el estudiante no fue creado en el sistema |
| **Resultado esperado** | Status `400 Bad Request` · Body indica el campo obligatorio faltante. El estudiante NO se crea en el sistema |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` · Verificar con `GET /api/students/1098765432` que retorna `404` (no se creó parcialmente) |

---

## API-021 — Inscribir estudiante ya inscrito en el curso

| Campo | Detalle |
|---|---|
| **ID** | API-021 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un estudiante ya se encuentra inscrito en un curso **Cuando** el docente intenta inscribirlo nuevamente en el mismo curso **Entonces** el sistema debe rechazar la inscripción e informar que el estudiante ya se encuentra en el curso |
| **Precondiciones** | El docente está autenticado. Existe un curso. El estudiante "1098765432" ya está inscrito en el curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "name": "María López García", "email": "maria.lopez@correo.com" }` |
| **Pasos de ejecución** | 1. Autenticarse y crear un curso 2. Inscribir al estudiante exitosamente 3. Intentar inscribir al mismo estudiante nuevamente |
| **Resultado esperado** | Status `409 Conflict` · Body indica que el estudiante ya se encuentra inscrito |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 409` |

---

## API-022 — Inscribir estudiante existente en el sistema (autocomplete)

| Campo | Detalle |
|---|---|
| **ID** | API-022 |
| **Endpoint** | `POST /api/courses/{courseId}/students` |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un estudiante ya está registrado en el sistema pero no está inscrito en un curso específico **Cuando** el docente lo inscribe en ese curso **Entonces** el sistema debe inscribirlo exitosamente sin duplicar su registro global en el sistema |
| **Precondiciones** | El docente está autenticado. Existe un curso. El estudiante "1098765432" ya existe en el sistema pero NO está inscrito en este curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "name": "María López García", "email": "maria.lopez@correo.com" }` |
| **Pasos de ejecución** | 1. Autenticarse y crear dos cursos 2. Inscribir al estudiante en el primer curso (queda registrado globalmente) 3. Inscribir al mismo estudiante en el segundo curso |
| **Resultado esperado** | Status `200 OK` · Body contiene el curso actualizado con el estudiante existente inscrito. No se duplica el registro global del estudiante |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.students[*].studentId contains '1098765432'` |

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
| **Escenario Gherkin** | **Dado** que un docente tiene un curso sin programa evaluativo definido **Cuando** define las instancias de evaluación con ponderaciones que suman exactamente el cien por ciento **Entonces** el sistema debe guardar el programa exitosamente y mostrar las instancias creadas con sus ponderaciones |
| **Precondiciones** | El docente está autenticado. Existe un curso sin programa definido |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `[{ "name": "Parcial 1", "percentage": 30 }, { "name": "Parcial 2", "percentage": 30 }, { "name": "Examen Final", "percentage": 40 }]` |
| **Pasos de ejecución** | 1. Autenticarse como docente 2. Crear un curso 3. Definir el programa evaluativo con 3 instancias que sumen 100 % |
| **Resultado esperado** | Status `200 OK` · Body contiene el curso con 3 instancias evaluatorias definidas (30 % + 30 % + 40 %) |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.activities.length == 3` · Cada actividad tiene `id`, `name` y `percentage` |

---

## API-024 — Definir programa con suma ≠ 100 %

| Campo | Detalle |
|---|---|
| **ID** | API-024 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un docente tiene un curso **Cuando** intenta definir un programa evaluativo con ponderaciones que no suman cien por ciento **Entonces** el sistema debe rechazar el programa e indicar que la suma de ponderaciones es incorrecta |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `[{ "name": "Parcial 1", "percentage": 40 }, { "name": "Parcial 2", "percentage": 40 }]` (suma = 80 %) |
| **Pasos de ejecución** | 1. Autenticarse como docente y crear un curso 2. Definir programa con ponderaciones que sumen 80 % |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que la suma de ponderaciones debe ser exactamente 100 % |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` · `response.message contains '100'` |

---

## API-025 — Definir programa con nombre de instancia vacío

| Campo | Detalle |
|---|---|
| **ID** | API-025 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente tiene un curso **Cuando** intenta definir un programa evaluativo con una instancia que no tiene nombre **Entonces** el sistema debe rechazar el programa e indicar que todas las instancias deben tener un nombre |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `[{ "name": "", "percentage": 50 }, { "name": "Examen Final", "percentage": 50 }]` |
| **Pasos de ejecución** | 1. Autenticarse como docente y crear un curso 2. Definir programa con una instancia sin nombre |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que no se permiten instancias sin nombre |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-026 — Definir programa con nombres de instancias duplicados

| Campo | Detalle |
|---|---|
| **ID** | API-026 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente tiene un curso **Cuando** intenta definir un programa evaluativo con dos instancias que tienen el mismo nombre **Entonces** el sistema debe rechazar el programa e indicar que los nombres de las instancias deben ser únicos |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `[{ "name": "Parcial", "percentage": 50 }, { "name": "Parcial", "percentage": 50 }]` |
| **Pasos de ejecución** | 1. Autenticarse como docente y crear un curso 2. Definir programa con dos instancias llamadas igual |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que no se permiten nombres duplicados |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-027 — Definir programa con ponderación ≤ 0

| Campo | Detalle |
|---|---|
| **ID** | API-027 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente tiene un curso **Cuando** intenta definir un programa evaluativo con una instancia cuya ponderación es cero o negativa **Entonces** el sistema debe rechazar el programa e indicar que cada instancia debe tener una ponderación mayor a cero |
| **Precondiciones** | El docente está autenticado. Existe un curso |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Escenario A: `[{ "name": "Parcial 1", "percentage": 0 }, { "name": "Final", "percentage": 100 }]` · Escenario B: `[{ "name": "Parcial 1", "percentage": -10 }, { "name": "Final", "percentage": 110 }]` |
| **Pasos de ejecución** | 1. Autenticarse como docente y crear un curso 2. Intentar definir programa con ponderación 0 (Escenario A) 3. Intentar definir programa con ponderación negativa (Escenario B) |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que cada instancia debe tener ponderación mayor al 0 % |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-028 — Actualizar ponderación de instancias existentes (suma = 100 %)

| Campo | Detalle |
|---|---|
| **ID** | API-028 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_13 — Actualizar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un curso tiene un programa evaluativo definido con ponderaciones vigentes **Cuando** el docente modifica las ponderaciones de las instancias manteniendo la suma en cien por ciento **Entonces** el sistema debe actualizar las ponderaciones exitosamente y recalcular los promedios si existen notas registradas |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa: Parcial 1 (30 %), Parcial 2 (30 %), Examen Final (40 %) |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `[{ "id": "<id_parcial1>", "name": "Parcial 1", "percentage": 20 }, { "id": "<id_parcial2>", "name": "Parcial 2", "percentage": 40 }, { "id": "<id_final>", "name": "Examen Final", "percentage": 40 }]` |
| **Pasos de ejecución** | 1. Autenticarse y crear un curso 2. Definir programa original (30/30/40) 3. Actualizar ponderaciones a (20/40/40) |
| **Resultado esperado** | Status `200 OK` · Body refleja las nuevas ponderaciones (20 % + 40 % + 40 %). Si existían notas, los promedios fueron recalculados |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · Verificar que las ponderaciones coinciden con las enviadas |

---

## API-029 — Eliminar instancia con redistribución válida (suma = 100 %)

| Campo | Detalle |
|---|---|
| **ID** | API-029 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` |
| **HU asociada** | HDU_12 — Eliminar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un curso tiene un programa evaluativo con tres instancias **Cuando** el docente elimina una instancia y redistribuye las ponderaciones de las restantes para que sumen cien por ciento **Entonces** el sistema debe guardar el programa con solo dos instancias y la instancia eliminada ya no debe existir |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa de 3 instancias: Parcial 1 (30 %), Parcial 2 (30 %), Examen Final (40 %) |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `[{ "id": "<id_parcial1>", "name": "Parcial 1", "percentage": 40 }, { "id": "<id_final>", "name": "Examen Final", "percentage": 60 }]` (se omite Parcial 2) |
| **Pasos de ejecución** | 1. Autenticarse y crear un curso 2. Definir programa con 3 instancias (30/30/40) 3. Enviar programa actualizado con solo 2 instancias (40/60), omitiendo Parcial 2 |
| **Resultado esperado** | Status `200 OK` · Body contiene solo 2 instancias (40 % + 60 %). "Parcial 2" ya no existe |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.activities.length == 2` · Ninguna actividad se llama "Parcial 2" |

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
| **Escenario Gherkin** | **Dado** que un curso tiene un programa evaluativo definido y un estudiante inscrito **Cuando** el docente registra una nota válida para el estudiante en una instancia de evaluación **Entonces** el sistema debe guardar la nota y recalcular automáticamente los promedios del estudiante |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido (Parcial 1: 30 %, Parcial 2: 30 %, Examen Final: 40 %). Existe un estudiante inscrito |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "activityId": "<id_parcial1>", "grade": 4.5 }` |
| **Pasos de ejecución** | 1. Autenticarse, crear curso, definir programa, inscribir estudiante 2. Registrar nota 4.5 en Parcial 1 para el estudiante |
| **Resultado esperado** | Status `200 OK` · Body contiene el curso actualizado con la nota 4.5 registrada y los promedios recalculados |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · La nota del estudiante en la actividad = 4.5 · Los promedios del estudiante reflejan el nuevo cálculo |

---

## API-031 — Registrar nota negativa

| Campo | Detalle |
|---|---|
| **ID** | API-031 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un curso tiene un programa evaluativo y un estudiante inscrito **Cuando** el docente intenta registrar una nota con valor negativo **Entonces** el sistema debe rechazar la nota e indicar que no se permiten valores negativos |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante inscrito |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "activityId": "<id_parcial1>", "grade": -2 }` |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa y estudiante inscrito 2. Intentar registrar nota -2 en Parcial 1 |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que no se permiten notas negativas |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-032 — Registrar nota con caracteres no numéricos

| Campo | Detalle |
|---|---|
| **ID** | API-032 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un curso tiene un programa evaluativo y un estudiante inscrito **Cuando** el docente intenta registrar una nota con caracteres no numéricos **Entonces** el sistema debe rechazar la nota e indicar que el formato del valor es inválido |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante inscrito |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "activityId": "<id_parcial1>", "grade": "abc#!" }` |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa y estudiante inscrito 2. Intentar registrar nota "abc#!" en Parcial 1 |
| **Resultado esperado** | Status `400 Bad Request` · Body indica error de formato (valor no numérico) |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-033 — Registrar nota nula (vacía = 0 en promedio)

| Campo | Detalle |
|---|---|
| **ID** | API-033 |
| **Endpoint** | `PUT /api/courses/{courseId}/grades` |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un estudiante tiene una nota registrada en una instancia y otra instancia sin calificar **Cuando** el docente registra la segunda instancia como nota vacía **Entonces** el sistema debe tratar la nota vacía como cero en el cálculo de promedios y mostrar los promedios recalculados |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa. Existe un estudiante con una nota previa en otra instancia (ej: Parcial 1 = 4.5) |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Body: `{ "studentId": "1098765432", "activityId": "<id_parcial2>", "grade": null }` |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa, inscribir estudiante 2. Registrar nota 4.5 en Parcial 1 3. Registrar nota nula en Parcial 2 4. Verificar que el promedio trata la nota nula como 0 |
| **Resultado esperado** | Status `200 OK` · Body contiene el curso actualizado. La nota de Parcial 2 queda marcada como vacía. Los promedios se calculan tratando la nota nula como 0 |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · El promedio ponderado del estudiante refleja 0 en la ponderación de Parcial 2 |

---

## API-034 — Recálculo de promedios tras cambio de ponderaciones

| Campo | Detalle |
|---|---|
| **ID** | API-034 |
| **Endpoint** | `PUT /api/courses/{courseId}/activities` → `GET /api/courses/{courseId}` |
| **HU asociada** | HDU_13 × HDU_14 — Actualización de programa con notas existentes |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un estudiante tiene notas registradas en todas las instancias de evaluación y se conoce su promedio ponderado actual **Cuando** el docente modifica las ponderaciones del programa **Entonces** el sistema debe recalcular automáticamente los promedios del estudiante de acuerdo a las nuevas ponderaciones |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa (Parcial 1: 30 %, Parcial 2: 30 %, Final: 40 %). Existe un estudiante con notas: Parcial 1 = 4.0, Parcial 2 = 3.0, Final = 5.0. Promedio ponderado previo = (4.0×0.30 + 3.0×0.30 + 5.0×0.40) = 4.10 |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Paso 1 Body: `[{ "id": "<p1>", "name": "Parcial 1", "percentage": 20 }, { "id": "<p2>", "name": "Parcial 2", "percentage": 20 }, { "id": "<f>", "name": "Examen Final", "percentage": 60 }]` · Paso 2: Sin body (GET) |
| **Pasos de ejecución** | 1. Autenticarse, crear curso, definir programa (30/30/40), inscribir estudiante 2. Registrar notas: P1=4.0, P2=3.0, Final=5.0 3. Actualizar ponderaciones a (20/20/60) 4. Consultar el curso y verificar nuevo promedio |
| **Resultado esperado** | Nuevo promedio ponderado = (4.0×0.20 + 3.0×0.20 + 5.0×0.60) = 4.40. Los promedios reflejan las nuevas ponderaciones |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` en ambos pasos · Promedio ponderado del estudiante = 4.40 (o equivalente según precisión decimal) |

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
| **Escenario Gherkin** | **Dado** que un estudiante tiene todas sus notas registradas en un curso **Cuando** el docente solicita generar el boletín del estudiante en formato PDF **Entonces** el sistema debe generar y entregar un archivo PDF válido con los datos académicos del estudiante |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido. Existe un estudiante con todas las notas registradas |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Query param: `format=pdf` · Sin body |
| **Pasos de ejecución** | 1. Autenticarse, crear curso, definir programa, inscribir estudiante, registrar notas completas 2. Solicitar boletín en formato PDF |
| **Resultado esperado** | Status `200 OK` · Header `Content-Disposition: attachment` · Header `Content-Type: application/pdf` · Body binario no vacío |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `responseHeaders['Content-Type'] contains 'pdf'` · `responseBytes.length > 0` |

---

## API-036 — Exportar boletín en formato HTML

| Campo | Detalle |
|---|---|
| **ID** | API-036 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=html` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un estudiante tiene todas sus notas registradas en un curso **Cuando** el docente solicita generar el boletín en formato HTML **Entonces** el sistema debe generar una página HTML válida que contenga los datos académicos del estudiante |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante con notas completas |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Query param: `format=html` · Sin body |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa, inscribir estudiante, registrar notas completas 2. Solicitar boletín en formato HTML |
| **Resultado esperado** | Status `200 OK` · Header `Content-Type: text/html` · Body contiene HTML válido con datos del estudiante |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `responseHeaders['Content-Type'] contains 'html'` · `response contains '<html'` o equivalente |

---

## API-037 — Exportar boletín en formato JSON

| Campo | Detalle |
|---|---|
| **ID** | API-037 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=json` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un estudiante tiene todas sus notas registradas en un curso **Cuando** el docente solicita generar el boletín en formato JSON **Entonces** el sistema debe devolver un documento JSON con los datos del estudiante, sus notas por instancia y los promedios calculados |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante con notas completas |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Query param: `format=json` · Sin body |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa, inscribir estudiante, registrar notas completas 2. Solicitar boletín en formato JSON |
| **Resultado esperado** | Status `200 OK` · Header `Content-Type: application/json` · Body contiene JSON con datos del estudiante, notas por instancia y promedios |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · `response.studentId != null` · `response.grades != null` · `response.averages != null` |

---

## API-038 — Exportar boletín con formato no soportado

| Campo | Detalle |
|---|---|
| **ID** | API-038 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=xml` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente quiere generar el boletín de un estudiante **Cuando** solicita el boletín en un formato que el sistema no soporta **Entonces** el sistema debe rechazar la solicitud e informar que el formato no es válido |
| **Precondiciones** | El docente está autenticado. Existe un curso y un estudiante |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Query param: `format=xml` · Sin body |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa, inscribir estudiante 2. Solicitar boletín con formato "xml" (no soportado) |
| **Resultado esperado** | Status `400 Bad Request` · Body indica que el formato no es soportado |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 400` |

---

## API-039 — Exportar boletín de estudiante con notas vacías

| Campo | Detalle |
|---|---|
| **ID** | API-039 |
| **Endpoint** | `GET /api/courses/{courseId}/students/{studentId}/report?format=json` |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que un estudiante tiene al menos una nota vacía en su programa evaluativo **Cuando** el docente solicita generar su boletín **Entonces** el sistema debe generar el boletín mostrando las notas vacías y calculando los promedios tratando las notas sin registrar como cero |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa. Existe un estudiante con al menos una nota vacía (nula) |
| **Datos de entrada** | Header: `X-Session-Token: <token_válido>` · Query param: `format=json` · Sin body |
| **Pasos de ejecución** | 1. Autenticarse, crear curso con programa, inscribir estudiante 2. Registrar nota solo en una instancia, dejando otra vacía 3. Solicitar boletín en formato JSON |
| **Resultado esperado** | Status `200 OK` · Body contiene el boletín. Las notas vacías se reflejan como `null` o `0` según diseño. Los promedios tratan nulas como 0 |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | `status 200` · Los promedios son coherentes con la "Regla de Oro" (nulas = 0) |

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
| **Escenario Gherkin** | **Dado** que un usuario no ha iniciado sesión o no proporciona sus credenciales de sesión **Cuando** intenta acceder a cualquier funcionalidad protegida del sistema **Entonces** el sistema debe denegar el acceso e informar que se requiere autenticación |
| **Precondiciones** | No se envía header `X-Session-Token` (o se envía vacío) |
| **Datos de entrada** | Sin header `X-Session-Token` · Body varía según endpoint |
| **Pasos de ejecución** | 1. Sin autenticarse, intentar acceder a cada endpoint protegido 2. Verificar que todos responden con error de autenticación |
| **Resultado esperado** | Status `401 Unauthorized` en **todos** los endpoints protegidos |
| **Estado** | Sin ejecutar |
| **Validaciones Karate** | Iterar sobre cada endpoint protegido y verificar `status 401`. Usar `Scenario Outline` con tabla de endpoints |

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
