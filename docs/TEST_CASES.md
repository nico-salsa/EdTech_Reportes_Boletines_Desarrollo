# TEST_CASES.md — Matriz de Casos de Prueba

## EdTech: Reportes y Boletines Académicos

| Campo | Detalle |
|---|---|
| **Proyecto** | EdTech — Reportes y Boletines Académicos |
| **Versión** | 1.0 |
| **Fecha** | 26 de marzo de 2026 |
| **Autor** | QA — Sebastián Stelmaj |
| **Referencia** | TEST_PLAN.md v1.0 |

---

## Convenciones

| Símbolo | Significado |
|---|---|
| **Estado** | `Sin ejecutar` — Todos los casos quedan documentados sin ejecución en esta versión |
| **Prioridad** | `Crítica` · `Alta` · `Media` · `Baja` |
| **Acción complementaria** | Cada TC debe registrarse como sub-tarea dentro de su HU correspondiente en GitHub Projects |

---

## Índice de Historias de Usuario en alcance (MVP)

| # | HU | Nombre | Casos |
|---|---|---|---|
| 1 | HDU_1 | Registrar usuarios | TC-001 a TC-004 |
| 2 | HDU_2 | Iniciar sesión | TC-005 a TC-008 |
| 3 | HDU_3 | Crear un nuevo curso | TC-009 a TC-011 |
| 4 | HDU_5 | Agregar estudiantes a un curso | TC-012 a TC-014 |
| 5 | HDU_11 | Definir programa del curso | TC-015 a TC-020 |
| 6 | HDU_12 | Eliminar instancia de evaluación del programa | TC-021 a TC-022 |
| 7 | HDU_13 | Actualizar instancia de evaluación del programa | TC-023 a TC-026 |
| 8 | HDU_14 | Registrar nota de una instancia de evaluación | TC-027 a TC-030 |
| 9 | HDU_15 | Generar boletín del estudiante | TC-031 a TC-032 |

**Total de casos de prueba: 32**

---

# HDU_1: Registrar usuarios

## TC-001 — Registro exitoso de docente

| Campo | Detalle |
|---|---|
| **ID** | TC-001 |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de registro **Cuando** ingreso un nombre de usuario válido y una contraseña válida y confirmo la información **Entonces** el sistema debe registrarme exitosamente |
| **Precondiciones** | La pantalla de registro está disponible. No existe un usuario con el nombre de usuario a utilizar |
| **Datos de entrada** | `nombre de usuario`: "docente_juan" · `contraseña`: "Pass1234" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de registro 2. Ingresar "docente_juan" en el campo nombre de usuario 3. Ingresar "Pass1234" en el campo contraseña 4. Confirmar la información |
| **Resultado esperado** | El sistema registra al docente. Se recibe respuesta `201 Created`. El usuario queda creado en el sistema |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-002 — Registro fallido por campos vacíos

| Campo | Detalle |
|---|---|
| **ID** | TC-002 |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de registro **Cuando** dejo el campo de contraseña vacío y confirmo la información **Entonces** el sistema debe mostrar un mensaje de error indicando que el campo contraseña es obligatorio y no debe finalizar el registro |
| **Precondiciones** | La pantalla de registro está disponible |
| **Datos de entrada** | `nombre de usuario`: "docente_juan" · `contraseña`: "" (vacío) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de registro 2. Ingresar "docente_juan" en el campo nombre de usuario 3. Dejar vacío el campo contraseña 4. Confirmar la información |
| **Resultado esperado** | El sistema NO registra al docente. Se recibe respuesta `400 Bad Request`. Se muestra mensaje indicando que el campo contraseña es obligatorio |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-003 — Registro fallido por nombre de usuario duplicado

| Campo | Detalle |
|---|---|
| **ID** | TC-003 |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de registro **Cuando** ingreso un nombre de usuario que ya se encuentra registrado en el sistema y confirmo la información **Entonces** el sistema debe mostrar un mensaje de error indicando que el nombre de usuario ya existe y no debe finalizar el registro |
| **Precondiciones** | Existe un usuario registrado con nombre "docente_juan" |
| **Datos de entrada** | `nombre de usuario`: "docente_juan" · `contraseña`: "OtraClave99" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de registro 2. Ingresar "docente_juan" en el campo nombre de usuario 3. Ingresar "OtraClave99" en el campo contraseña 4. Confirmar la información |
| **Resultado esperado** | El sistema NO registra al docente. Se recibe respuesta `409 Conflict`. Se muestra mensaje indicando que el nombre de usuario ya se encuentra registrado |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-004 — (NF) Contraseña almacenada con hashing

| Campo | Detalle |
|---|---|
| **ID** | TC-004 |
| **HU asociada** | HDU_1 — Registrar usuarios |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que un docente se registró exitosamente en el sistema **Cuando** se consulta directamente la base de datos **Entonces** la contraseña del usuario NO debe estar almacenada en texto plano |
| **Precondiciones** | Un usuario fue registrado exitosamente (TC-001 ejecutado) |
| **Datos de entrada** | `nombre de usuario`: "docente_juan" |
| **Pasos de ejecución** | 1. Registrar un usuario exitosamente 2. Consultar la tabla de usuarios en la base de datos 3. Verificar el campo de contraseña del registro |
| **Resultado esperado** | El campo de contraseña contiene un hash (no el texto plano "Pass1234"). Se reconoce un formato de hashing estándar (ej: bcrypt, SHA-256) |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_2: Iniciar sesión

## TC-005 — Inicio de sesión exitoso

| Campo | Detalle |
|---|---|
| **ID** | TC-005 |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de inicio de sesión **Cuando** ingreso un nombre de usuario y contraseña válidos y confirmo la información **Entonces** el sistema debe permitir el acceso a la página principal |
| **Precondiciones** | Existe un usuario registrado con nombre "docente_juan" y contraseña "Pass1234" |
| **Datos de entrada** | `nombre de usuario`: "docente_juan" · `contraseña`: "Pass1234" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de login 2. Ingresar "docente_juan" en el campo nombre de usuario 3. Ingresar "Pass1234" en el campo contraseña 4. Confirmar la información |
| **Resultado esperado** | El sistema permite el acceso. Se recibe respuesta `200 OK` con token de sesión. Se redirige a la página principal (Dashboard) |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-006 — Inicio de sesión con credenciales inválidas

| Campo | Detalle |
|---|---|
| **ID** | TC-006 |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de inicio de sesión **Cuando** ingreso un nombre de usuario y contraseña que no coinciden con ningún usuario registrado y confirmo la información **Entonces** el sistema debe denegar el acceso |
| **Precondiciones** | No existe un usuario registrado con las credenciales a utilizar |
| **Datos de entrada** | `nombre de usuario`: "usuario_falso" · `contraseña`: "ClaveIncorrecta" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de login 2. Ingresar "usuario_falso" en el campo nombre de usuario 3. Ingresar "ClaveIncorrecta" en el campo contraseña 4. Confirmar la información |
| **Resultado esperado** | El sistema deniega el acceso. Se recibe respuesta `401 Unauthorized`. No se redirige a la página principal |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-007 — Inicio de sesión con campo vacío

| Campo | Detalle |
|---|---|
| **ID** | TC-007 |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de inicio de sesión **Cuando** dejo el campo nombre de usuario vacío y confirmo la información **Entonces** el sistema debe resaltar el campo faltante y no procesar el inicio de sesión |
| **Precondiciones** | La pantalla de login está disponible |
| **Datos de entrada** | `nombre de usuario`: "" (vacío) · `contraseña`: "Pass1234" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de login 2. Dejar vacío el campo nombre de usuario 3. Ingresar "Pass1234" en el campo contraseña 4. Confirmar la información |
| **Resultado esperado** | El sistema NO procesa el login. Se recibe respuesta `400 Bad Request`. El campo nombre de usuario se resalta visualmente como obligatorio |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-008 — (NF) Mensaje de error genérico por seguridad

| Campo | Detalle |
|---|---|
| **ID** | TC-008 |
| **HU asociada** | HDU_2 — Iniciar sesión |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de inicio de sesión **Cuando** ingreso credenciales inválidas y confirmo la información **Entonces** el mensaje de error debe ser genérico (ej: "Usuario o contraseña incorrectos") sin revelar cuál dato falló específicamente |
| **Precondiciones** | Existe un usuario registrado en el sistema |
| **Datos de entrada** | `nombre de usuario`: "docente_juan" · `contraseña`: "ClaveIncorrecta" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de login 2. Ingresar nombre de usuario válido con contraseña incorrecta 3. Confirmar la información 4. Observar el mensaje de error mostrado |
| **Resultado esperado** | El mensaje de error es genérico (ej: "Usuario o contraseña incorrectos"). NO se indica si el error es del usuario o de la contraseña por separado |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_3: Crear un nuevo curso

## TC-009 — Crear curso exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-009 |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que soy un docente autenticado en el sistema **Cuando** ingreso el título del curso y confirmo la información y el nombre del curso no existe en el sistema **Entonces** el sistema debe crear el curso y este queda visible en el sistema |
| **Precondiciones** | El docente está autenticado. No existe un curso con el título a utilizar |
| **Datos de entrada** | `título del curso`: "Matemáticas 101" |
| **Pasos de ejecución** | 1. Iniciar sesión como docente 2. Seleccionar la opción de crear curso 3. Ingresar "Matemáticas 101" como título 4. Confirmar la información |
| **Resultado esperado** | El sistema crea el curso. Se recibe respuesta `201 Created`. El curso "Matemáticas 101" aparece en el listado de cursos del docente |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-010 — Crear curso con nombre duplicado

| Campo | Detalle |
|---|---|
| **ID** | TC-010 |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que soy un docente autenticado en el sistema **Cuando** ingreso el título del curso y confirmo la información y el nombre del curso ya existe en el sistema **Entonces** el sistema debe mostrar un mensaje de error indicando que ese curso ya existe y no debe crear uno nuevo |
| **Precondiciones** | El docente está autenticado. Existe un curso con título "Matemáticas 101" |
| **Datos de entrada** | `título del curso`: "Matemáticas 101" |
| **Pasos de ejecución** | 1. Iniciar sesión como docente 2. Seleccionar la opción de crear curso 3. Ingresar "Matemáticas 101" como título (ya existente) 4. Confirmar la información |
| **Resultado esperado** | El sistema NO crea el curso. Se recibe respuesta `409 Conflict`. Se muestra mensaje indicando que el curso ya existe |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-011 — Modificar título del curso exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-011 |
| **HU asociada** | HDU_3 — Crear un nuevo curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que soy un docente autenticado y existe un curso creado **Cuando** edito el título del curso por uno que no coincide con un curso ya creado y confirmo la información **Entonces** el sistema debe cambiar el título del curso |
| **Precondiciones** | El docente está autenticado. Existe un curso "Matemáticas 101". No existe un curso con el nuevo título |
| **Datos de entrada** | `título actual`: "Matemáticas 101" · `título nuevo`: "Álgebra Lineal" |
| **Pasos de ejecución** | 1. Iniciar sesión como docente 2. Seleccionar el curso "Matemáticas 101" para editar 3. Cambiar el título a "Álgebra Lineal" 4. Confirmar la información |
| **Resultado esperado** | El curso ahora se muestra con el título "Álgebra Lineal". Se recibe respuesta `200 OK`. El listado refleja el nuevo nombre |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_5: Agregar estudiantes a la lista de estudiantes de un curso

## TC-012 — Agregar estudiante nuevo al curso (alta on-the-fly)

| Campo | Detalle |
|---|---|
| **ID** | TC-012 |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de gestión de estudiantes de un curso **Cuando** ingreso un estudiante con un ID nuevo y relleno la información obligatoria (ID, Nombre completo, Correo) **Entonces** debo ver al estudiante en la lista de estudiantes del curso y el estudiante debe quedar registrado en el sistema |
| **Precondiciones** | El docente está autenticado. Existe un curso creado. No existe un estudiante con el ID a utilizar |
| **Datos de entrada** | `ID`: "1098765432" · `Nombre completo`: "María López García" · `Correo`: "maria.lopez@correo.com" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de gestión de estudiantes del curso 2. Ingresar "1098765432" en el campo ID 3. Ingresar "María López García" en Nombre completo 4. Ingresar "maria.lopez@correo.com" en Correo 5. Confirmar la información |
| **Resultado esperado** | El estudiante aparece en la lista de estudiantes del curso. Se recibe respuesta `201 Created`. El estudiante queda registrado globalmente en el sistema |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-013 — Agregar estudiante nuevo con campos obligatorios vacíos

| Campo | Detalle |
|---|---|
| **ID** | TC-013 |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de gestión de estudiantes de un curso **Cuando** ingreso un estudiante con un ID nuevo y dejo el campo Nombre completo vacío **Entonces** el estudiante no se debe agregar a la lista ni registrarse en el sistema |
| **Precondiciones** | El docente está autenticado. Existe un curso creado |
| **Datos de entrada** | `ID`: "1098765432" · `Nombre completo`: "" (vacío) · `Correo`: "maria.lopez@correo.com" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de gestión de estudiantes del curso 2. Ingresar "1098765432" en el campo ID 3. Dejar vacío el campo Nombre completo 4. Ingresar "maria.lopez@correo.com" en Correo 5. Confirmar la información |
| **Resultado esperado** | El estudiante NO se agrega a la lista del curso. Se recibe respuesta `400 Bad Request`. Se indica que el campo Nombre completo es obligatorio. El estudiante NO se registra en el sistema |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-014 — Agregar estudiante existente al curso (autocompletado)

| Campo | Detalle |
|---|---|
| **ID** | TC-014 |
| **HU asociada** | HDU_5 — Agregar estudiantes a un curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de gestión de estudiantes de un curso **Cuando** ingreso un estudiante con un ID que ya existe en el sistema **Entonces** debo ver al estudiante en la lista del curso con su información personal autocompletada |
| **Precondiciones** | El docente está autenticado. Existe un curso creado. Existe un estudiante registrado con ID "1098765432" |
| **Datos de entrada** | `ID`: "1098765432" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de gestión de estudiantes del curso 2. Ingresar "1098765432" en el campo ID 3. Verificar que los campos Nombre completo y Correo se autocompleten 4. Confirmar la información |
| **Resultado esperado** | Los campos Nombre completo y Correo se autocompletan con la información registrada. El estudiante aparece en la lista de estudiantes del curso |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_11: Definir programa del curso

## TC-015 — Definir programa exitosamente (suma = 100%)

| Campo | Detalle |
|---|---|
| **ID** | TC-015 |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono definir un programa y agrego instancias evaluatorias con ponderaciones que suman exactamente 100% y confirmo **Entonces** el sistema debe guardar el programa y las instancias deben verse en pantalla |
| **Precondiciones** | El docente está autenticado. Existe un curso creado sin programa definido |
| **Datos de entrada** | Instancia 1: `nombre`: "Parcial 1", `ponderación`: 30% · Instancia 2: `nombre`: "Parcial 2", `ponderación`: 30% · Instancia 3: `nombre`: "Examen Final", `ponderación`: 40% |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de definir programa 3. Agregar 3 instancias evaluatorias con los datos indicados 4. Confirmar la definición del programa |
| **Resultado esperado** | El programa se guarda exitosamente. Las 3 instancias evaluatorias (Parcial 1 — 30%, Parcial 2 — 30%, Examen Final — 40%) se muestran en la pantalla de detalle del curso |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-016 — Definir programa con instancia sin nombre

| Campo | Detalle |
|---|---|
| **ID** | TC-016 |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono definir un programa y al menos una instancia evaluatoria no tiene nombre y confirmo **Entonces** la definición del programa no se debe procesar y el sistema debe notificar que no se permiten instancias sin nombre |
| **Precondiciones** | El docente está autenticado. Existe un curso creado sin programa definido |
| **Datos de entrada** | Instancia 1: `nombre`: "" (vacío), `ponderación`: 50% · Instancia 2: `nombre`: "Examen Final", `ponderación`: 50% |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de definir programa 3. Agregar instancia 1 sin nombre con ponderación 50% 4. Agregar instancia 2 "Examen Final" con ponderación 50% 5. Confirmar la definición |
| **Resultado esperado** | El programa NO se guarda. Se notifica que no se permiten instancias sin nombre. Se permite al usuario completar el campo vacío |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-017 — Definir programa con suma de ponderaciones diferente al 100%

| Campo | Detalle |
|---|---|
| **ID** | TC-017 |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono definir un programa y la suma de los porcentajes de ponderación no coincide con el 100% y confirmo **Entonces** la definición no se debe procesar y el sistema debe notificar que la suma debe ser exactamente 100% |
| **Precondiciones** | El docente está autenticado. Existe un curso creado sin programa definido |
| **Datos de entrada** | Instancia 1: `nombre`: "Parcial 1", `ponderación`: 40% · Instancia 2: `nombre`: "Parcial 2", `ponderación`: 40% (suma = 80%) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de definir programa 3. Agregar 2 instancias con ponderaciones que sumen 80% 4. Confirmar la definición |
| **Resultado esperado** | El programa NO se guarda. Se notifica que la suma de ponderaciones debe ser exactamente 100%. Se permite ajustar los porcentajes |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-018 — Definir programa con ponderación equitativa

| Campo | Detalle |
|---|---|
| **ID** | TC-018 |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono definir un programa, agrego instancias evaluatorias y selecciono la opción de ponderar equitativamente y confirmo **Entonces** el sistema debe guardar el programa con ponderaciones distribuidas equitativamente |
| **Precondiciones** | El docente está autenticado. Existe un curso creado sin programa definido |
| **Datos de entrada** | Instancia 1: `nombre`: "Taller 1" · Instancia 2: `nombre`: "Taller 2" · Instancia 3: `nombre`: "Taller 3" · Selección: ponderación equitativa |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de definir programa 3. Agregar 3 instancias con sus nombres 4. Seleccionar la opción de ponderación equitativa 5. Confirmar la definición |
| **Resultado esperado** | El programa se guarda. Las ponderaciones se distribuyen equitativamente (33.33%, 33.33%, 33.34% o equivalente). La suma es exactamente 100% |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-019 — Definir programa con ponderación menor o igual a 0

| Campo | Detalle |
|---|---|
| **ID** | TC-019 |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono definir un programa y al menos una instancia tiene ponderación igual a 0 o negativa y confirmo **Entonces** la definición no se debe procesar y el sistema debe notificar que cada instancia debe tener ponderación mayor al 0% |
| **Precondiciones** | El docente está autenticado. Existe un curso creado sin programa definido |
| **Datos de entrada** | Instancia 1: `nombre`: "Parcial 1", `ponderación`: 0% · Instancia 2: `nombre`: "Examen Final", `ponderación`: 100% |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de definir programa 3. Agregar instancia "Parcial 1" con ponderación 0% 4. Agregar instancia "Examen Final" con ponderación 100% 5. Confirmar la definición |
| **Resultado esperado** | El programa NO se guarda. Se notifica que cada instancia debe tener una ponderación mayor al 0%. Se permite editar la ponderación inválida |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-020 — Definir programa con nombres de instancias duplicados

| Campo | Detalle |
|---|---|
| **ID** | TC-020 |
| **HU asociada** | HDU_11 — Definir programa del curso |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono definir un programa y al menos dos instancias evaluatorias tienen el mismo nombre y confirmo **Entonces** la definición no se debe procesar y el sistema debe notificar que no se permiten nombres duplicados |
| **Precondiciones** | El docente está autenticado. Existe un curso creado sin programa definido |
| **Datos de entrada** | Instancia 1: `nombre`: "Parcial", `ponderación`: 50% · Instancia 2: `nombre`: "Parcial", `ponderación`: 50% |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de definir programa 3. Agregar 2 instancias con el mismo nombre "Parcial" 4. Asignar ponderaciones que sumen 100% 5. Confirmar la definición |
| **Resultado esperado** | El programa NO se guarda. Se notifica que las instancias no deben contener nombres duplicados |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_12: Eliminar instancia de evaluación del programa del curso

## TC-021 — Eliminar instancia evaluatoria exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-021 |
| **HU asociada** | HDU_12 — Eliminar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono editar el programa y elimino una instancia de evaluación y actualizo los porcentajes para que sumen 100% y confirmo **Entonces** la edición se debe procesar y la instancia eliminada ya no debe aparecer |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido de al menos 2 instancias (ej: "Parcial 1" 30%, "Parcial 2" 30%, "Examen Final" 40%) |
| **Datos de entrada** | Eliminar: "Parcial 2" · Redistribuir: "Parcial 1" 40%, "Examen Final" 60% |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar editar el programa 3. Eliminar la instancia "Parcial 2" 4. Ajustar "Parcial 1" a 40% y "Examen Final" a 60% 5. Confirmar la edición |
| **Resultado esperado** | El programa se actualiza. "Parcial 2" ya no aparece en la pantalla. Las nuevas ponderaciones se reflejan correctamente (40% + 60% = 100%) |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-022 — Eliminar instancia evaluatoria con suma resultante diferente al 100%

| Campo | Detalle |
|---|---|
| **ID** | TC-022 |
| **HU asociada** | HDU_12 — Eliminar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono editar el programa y elimino una instancia sin redistribuir los porcentajes para que sumen 100% y confirmo **Entonces** la edición no se debe procesar y el sistema debe notificar que la suma debe ser exactamente 100% |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido (ej: "Parcial 1" 30%, "Parcial 2" 30%, "Examen Final" 40%) |
| **Datos de entrada** | Eliminar: "Parcial 2" · Sin redistribuir (suma resultante: 70%) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar editar el programa 3. Eliminar la instancia "Parcial 2" 4. No ajustar las ponderaciones restantes 5. Confirmar la edición |
| **Resultado esperado** | La edición del programa NO se procesa. Se notifica que la suma de ponderaciones debe ser exactamente 100% |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_13: Actualizar instancia de evaluación del programa del curso

## TC-023 — Actualizar ponderación de instancia exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-023 |
| **HU asociada** | HDU_13 — Actualizar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono editar el programa y edito la ponderación de una instancia y la suma total es exactamente 100% y confirmo **Entonces** la edición se debe procesar y la modificación debe reflejarse en pantalla |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido ("Parcial 1" 30%, "Parcial 2" 30%, "Examen Final" 40%) |
| **Datos de entrada** | Editar "Parcial 1" de 30% a 20% · Editar "Parcial 2" de 30% a 40% (suma: 20% + 40% + 40% = 100%) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar editar el programa 3. Cambiar la ponderación de "Parcial 1" a 20% 4. Cambiar la ponderación de "Parcial 2" a 40% 5. Confirmar la edición |
| **Resultado esperado** | El programa se actualiza. Las nuevas ponderaciones se reflejan en pantalla. Si existen notas registradas, los promedios se recalculan automáticamente |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-024 — Actualizar ponderación con suma diferente al 100%

| Campo | Detalle |
|---|---|
| **ID** | TC-024 |
| **HU asociada** | HDU_13 — Actualizar instancia de evaluación del programa |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono editar el programa y edito la ponderación de una instancia y la suma total no es exactamente 100% y confirmo **Entonces** la edición no se debe procesar y el sistema debe notificar que la suma debe ser 100% |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido |
| **Datos de entrada** | Editar "Parcial 1" de 30% a 50% (suma resultante: 50% + 30% + 40% = 120%) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar editar el programa 3. Cambiar la ponderación de "Parcial 1" a 50% sin ajustar las demás 4. Confirmar la edición |
| **Resultado esperado** | La edición NO se procesa. Se notifica que la suma de ponderaciones debe ser exactamente 100% |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-025 — Actualizar nombre de instancia exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-025 |
| **HU asociada** | HDU_13 — Actualizar instancia de evaluación del programa |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono editar el programa y edito el nombre de una instancia por uno válido y no duplicado y confirmo **Entonces** la edición se debe procesar y el nuevo nombre debe reflejarse en pantalla |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido. El nuevo nombre no existe en la lista de instancias |
| **Datos de entrada** | Editar nombre de "Parcial 1" a "Quiz Parcial" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar editar el programa 3. Cambiar el nombre de "Parcial 1" a "Quiz Parcial" 4. Confirmar la edición |
| **Resultado esperado** | El programa se actualiza. La instancia ahora se muestra como "Quiz Parcial" en la pantalla de detalle |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-026 — Actualizar nombre de instancia con nombre vacío o duplicado

| Campo | Detalle |
|---|---|
| **ID** | TC-026 |
| **HU asociada** | HDU_13 — Actualizar instancia de evaluación del programa |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono editar el programa y edito el nombre de una instancia dejándolo vacío o asignando un nombre que ya existe y confirmo **Entonces** la edición no se debe procesar y el sistema debe resaltar el error |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido con instancias "Parcial 1" y "Parcial 2" |
| **Datos de entrada** | Escenario A: Editar nombre de "Parcial 1" a "" (vacío) · Escenario B: Editar nombre de "Parcial 1" a "Parcial 2" (duplicado) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar editar el programa 3. Cambiar el nombre de "Parcial 1" a vacío (o a "Parcial 2") 4. Confirmar la edición |
| **Resultado esperado** | La edición NO se procesa. Escenario A: se resaltan los nombres vacíos. Escenario B: se resaltan los nombres repetidos |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_14: Registrar nota de una instancia de evaluación

## TC-027 — Registrar nota exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-027 |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono registrar nota de una instancia de evaluación a un estudiante e ingreso una nota mayor o igual a 0 **Entonces** el sistema debe guardar la nota y los promedios del estudiante deben actualizarse automáticamente |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido (Parcial 1: 30%, Parcial 2: 30%, Examen Final: 40%). Existe un estudiante inscrito en el curso |
| **Datos de entrada** | `estudiante`: "María López García" · `instancia`: "Parcial 1" · `nota`: 4.5 |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Localizar al estudiante "María López García" 3. Seleccionar la celda de "Parcial 1" 4. Ingresar la nota 4.5 5. Guardar |
| **Resultado esperado** | La nota 4.5 se guarda en "Parcial 1" para el estudiante. El promedio ponderado se actualiza automáticamente. El promedio general se actualiza automáticamente |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-028 — Registrar nota negativa (rechazada)

| Campo | Detalle |
|---|---|
| **ID** | TC-028 |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono registrar nota de una instancia de evaluación a un estudiante e ingreso una nota menor a 0 **Entonces** la nota no se debe guardar y el sistema debe notificar que no se permiten notas negativas |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante inscrito |
| **Datos de entrada** | `estudiante`: "María López García" · `instancia`: "Parcial 1" · `nota`: -2 |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Localizar al estudiante "María López García" 3. Seleccionar la celda de "Parcial 1" 4. Ingresar la nota -2 5. Intentar guardar |
| **Resultado esperado** | La nota NO se guarda. Se notifica que no se permiten registrar notas negativas. Se recibe respuesta `400 Bad Request` |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-029 — Registrar nota nula (Regla de Oro: nula = 0)

| Campo | Detalle |
|---|---|
| **ID** | TC-029 |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono registrar nota de una instancia de evaluación a un estudiante e ingreso una nota nula **Entonces** el sistema debe guardar la nota, resaltar que es nula, y calcular los promedios considerando la nota nula como 0 |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido. Existe un estudiante con al menos una nota registrada en otra instancia |
| **Datos de entrada** | `estudiante`: "María López García" · `instancia`: "Parcial 2" · `nota`: null (vacía) |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Localizar al estudiante "María López García" 3. Seleccionar la celda de "Parcial 2" 4. Dejar el valor nulo/vacío 5. Guardar |
| **Resultado esperado** | La nota nula se guarda. La celda se resalta visualmente indicando que la nota es nula. El promedio ponderado se actualiza considerando la nota nula como 0. El promedio general se actualiza considerando la nota nula como 0 |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-030 — Registrar nota con caracteres no numéricos

| Campo | Detalle |
|---|---|
| **ID** | TC-030 |
| **HU asociada** | HDU_14 — Registrar nota de una instancia de evaluación |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono registrar nota de una instancia de evaluación a un estudiante e ingreso un valor alfanumérico o con símbolos **Entonces** la nota no se debe guardar y el sistema debe mostrar un error de formato |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa y un estudiante inscrito |
| **Datos de entrada** | `estudiante`: "María López García" · `instancia`: "Parcial 1" · `nota`: "abc#!" |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Localizar al estudiante "María López García" 3. Seleccionar la celda de "Parcial 1" 4. Ingresar "abc#!" 5. Intentar guardar |
| **Resultado esperado** | La nota NO se guarda. Se muestra un error de formato indicando que el valor debe ser numérico. Se recibe respuesta `400 Bad Request` |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# HDU_15: Generar boletín del estudiante de un curso

## TC-031 — Generar boletín exitosamente

| Campo | Detalle |
|---|---|
| **ID** | TC-031 |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Crítica |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono generar boletín de un estudiante y selecciono un formato disponible **Entonces** el sistema debe descargar el boletín con la información completa del estudiante, notas y promedios en el formato seleccionado |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido. Existe un estudiante con todas las notas registradas. Los formatos de descarga (PDF, HTML, JSON) están habilitados |
| **Datos de entrada** | `estudiante`: "María López García" · `formato`: PDF |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de generar boletín para "María López García" 3. Seleccionar formato PDF 4. Confirmar la generación |
| **Resultado esperado** | Se descarga un archivo PDF. El boletín contiene: nombre del estudiante, ID, notas por cada instancia evaluatoria, promedio ponderado y promedio general. Los datos coinciden con los registrados en el sistema |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

## TC-032 — Generar boletín con notas vacías (advertencia previa)

| Campo | Detalle |
|---|---|
| **ID** | TC-032 |
| **HU asociada** | HDU_15 — Generar boletín del estudiante |
| **Prioridad** | Alta |
| **Escenario Gherkin** | **Dado** que me encuentro en la pantalla de detalle del curso **Cuando** selecciono generar boletín de un estudiante que tiene al menos una nota vacía **Entonces** el sistema debe informar sobre la existencia de notas vacías y tras confirmar, descargar el boletín en el formato seleccionado |
| **Precondiciones** | El docente está autenticado. Existe un curso con programa definido. Existe un estudiante con al menos una instancia de evaluación sin nota (nula) |
| **Datos de entrada** | `estudiante`: "María López García" (con nota nula en "Parcial 2") · `formato`: JSON |
| **Pasos de ejecución** | 1. Navegar a la pantalla de detalle del curso 2. Seleccionar la opción de generar boletín para "María López García" 3. El sistema muestra advertencia de notas vacías 4. Confirmar la generación 5. Seleccionar formato JSON |
| **Resultado esperado** | Se muestra advertencia indicando que existen notas vacías. Tras confirmar, se descarga un archivo JSON. El boletín refleja las notas vacías como 0 (o N/A según diseño) y los promedios calculados coherentemente |
| **Resultado obtenido** | — |
| **Estado** | Sin ejecutar |

---

# Resumen de cobertura

| HU | Casos funcionales | Casos no funcionales | Total | Prioridad predominante |
|---|---|---|---|---|
| HDU_1 — Registrar usuarios | 3 | 1 | **4** | Crítica |
| HDU_2 — Iniciar sesión | 3 | 1 | **4** | Crítica |
| HDU_3 — Crear un nuevo curso | 3 | 0 | **3** | Crítica / Alta |
| HDU_5 — Agregar estudiantes a un curso | 3 | 0 | **3** | Crítica / Alta |
| HDU_11 — Definir programa del curso | 6 | 0 | **6** | Crítica / Alta |
| HDU_12 — Eliminar instancia de evaluación | 2 | 0 | **2** | Crítica |
| HDU_13 — Actualizar instancia de evaluación | 4 | 0 | **4** | Crítica / Alta |
| HDU_14 — Registrar nota | 4 | 0 | **4** | Crítica / Alta |
| HDU_15 — Generar boletín del estudiante | 2 | 0 | **2** | Crítica / Alta |
| | | | **32** | |

---

## Notas finales

1. **Pruebas exhaustivas son una falacia**: los 32 casos están diseñados para cubrir los criterios de aceptación de cada HU del MVP sin redundancia. Se priorizan los flujos críticos (happy path + principales flujos negativos) según la estrategia de priorización basada en riesgo definida en el TEST_PLAN.md.

2. **Estado uniforme**: todos los casos quedan en estado `Sin ejecutar` como corresponde a esta fase documental.

3. **Registro en GitHub Projects**: cada caso de prueba (TC-001 a TC-032) debe registrarse como sub-tarea dentro de su HU correspondiente en el tablero de GitHub Projects, según lo acordado en el plan de pruebas.

4. **Técnicas aplicadas**: los casos de prueba incorporan partición de equivalencia (datos válidos vs. inválidos), análisis de valor frontera (ponderaciones en 0%, 100%), tabla de decisión (combinaciones en HDU_11) y predicción de errores (caracteres no numéricos, duplicidad), tal como se establece en la sección 4.3 del TEST_PLAN.md.
