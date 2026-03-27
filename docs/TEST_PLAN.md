# Plan de Pruebas — EdTech: Reportes y Boletines

---

## 1. Identificación del Plan

| Campo | Detalle |
|---|---|
| **Proyecto** | EdTech — Reportes y Boletines Académicos |
| **Sistema bajo prueba** | Aplicación local de gestión de cursos, calificaciones y generación de boletines para docentes |
| **Versión del Plan** | 1.0 |
| **Fecha** | 25 de marzo de 2026 |
| **Equipo** | **QA**: Sebastián Stelmaj · **DEV**: Nicolas Vargas |
| **Ciclo de prueba** | MVP — Micro-Sprint 1 y Micro-Sprint 2 |

---

## 2. Contexto

### 2.1 ¿Qué sistema se va a probar?

EdTech es una herramienta de ejecución local diseñada para docentes que necesitan centralizar la creación de cursos, la inscripción de estudiantes, la definición de programas evaluativos, el registro de calificaciones y la generación de boletines académicos exportables.

### 2.2 ¿Qué problema de negocio resuelve?

Los docentes invierten una cantidad significativa de tiempo calculando promedios manualmente y generando boletines académicos. Este proceso es propenso a errores humanos y se realiza de forma descentralizada (hojas de cálculo, registros en papel, etc.). EdTech automatiza el cálculo de promedios ponderados y la exportación de boletines, reduciendo la carga operativa del docente y garantizando coherencia en los datos.

### 2.3 Contexto del ciclo de prueba

Este plan corresponde a un ciclo de **micro-sprints** donde el objetivo es entregar un **MVP funcional e integrado**. No se pretende cubrir la totalidad del backlog, sino validar el flujo crítico de negocio de extremo a extremo.

---

## 3. Alcance de las Pruebas

### 3.1 Historias de Usuario incluidas en el MVP (IN)

Las siguientes HU conforman el flujo crítico del negocio que será validado en este ciclo:

| HU | Nombre | Justificación de inclusión |
|---|---|---|
| **HDU_1** | Registrar usuarios | Sin registro no hay docente en el sistema |
| **HDU_2** | Iniciar sesión | Punto de entrada obligatorio a la aplicación |
| **HDU_3** | Crear un nuevo curso | Eje central de toda la gestión académica |
| **HDU_5** | Agregar estudiantes a un curso | Sin estudiantes no hay sujetos a evaluar |
| **HDU_11** | Definir programa del curso | Estructura evaluativa con ponderaciones (suma = 100%) |
| **HDU_12** | Eliminar instancia de evaluación del programa | Permite corregir el programa definido |
| **HDU_13** | Actualizar instancia de evaluación del programa | Permite ajustar nombres y ponderaciones |
| **HDU_14** | Registrar nota de una instancia de evaluación | Motor de cálculo: promedio general y ponderado |
| **HDU_15** | Generar boletín del estudiante | Entregable de valor concreto para el docente |

### 3.2 Historias de Usuario excluidas de este ciclo (OUT)

| HU | Nombre | Razón de exclusión |
|---|---|---|
| HDU_4 | Consultar curso | Vista implícita en HDU_3 y HDU_5 |
| HDU_6 a HDU_10 | Gestión de grupos | Funcionalidad complementaria, no bloquea el flujo principal |
| HDU_16 | Generar reporte grupal consolidado | Extensión de HDU_15, no es parte del núcleo mínimo |
| HDU_17 | Dar de baja estudiante del curso | Operación de gestión secundaria |
| HDU_18 | Actualizar información personal de estudiante | No bloquea el happy path del MVP |
| HDU_19 | Eliminar estudiante del sistema | Operación destructiva no prioritaria para el MVP |

---

## 4. Estrategia de Pruebas

### 4.1 Niveles de prueba aplicados

| Nivel | Descripción | Responsable |
|---|---|---|
| **Prueba de componente (unitaria)** | Validar lógica interna de cada módulo de forma aislada | DEV |
| **Prueba de integración** | Verificar la comunicación entre componentes (endpoints ↔ lógica ↔ persistencia) | DEV + QA |
| **Prueba de sistema** | Validar los flujos de extremo a extremo sobre el sistema completo | QA |
| **Prueba de aceptación** | Confirmar que los criterios de aceptación de cada HU se cumplen | QA |

### 4.2 Tipos de prueba

| Tipo | Propósito | Enfoque |
|---|---|---|
| **Funcional** | Verificar que el sistema hace lo que debe según los criterios de aceptación | Caja negra, basada en especificación |
| **No funcional — Rendimiento** | Validar tiempos de respuesta bajo carga básica | Prueba de carga sobre endpoints críticos |
| **Regresión** | Confirmar que cambios nuevos no rompen funcionalidad existente | Re-ejecución de escenarios previos |
| **Confirmación** | Verificar que un defecto reportado fue corregido exitosamente | Re-ejecución del caso que falló |

### 4.3 Técnicas de prueba

| Técnica | Aplicación en este plan |
|---|---|
| **Partición de equivalencia** | Clasificar entradas válidas e inválidas (ej: campos vacíos, duplicados, formatos incorrectos) |
| **Análisis de valor frontera** | Validar límites en ponderaciones (0%, 100%, negativos) y notas (0, negativos) |
| **Tabla de decisión** | Combinaciones de condiciones en HDU_11 (suma = 100%, nombres duplicados, ponderación 0) |
| **Prueba basada en escenarios (BDD)** | Escenarios Gherkin Dado/Cuando/Entonces para cada criterio de aceptación |
| **Predicción de errores** | Anticipar defectos comunes: duplicidad de datos, campos vacíos, cálculos de promedios con notas nulas |

### 4.4 Herramientas mandatorias

| Herramienta | Propósito |
|---|---|
| **SerenityBDD + Cucumber** | Automatización de pruebas funcionales con escenarios Gherkin |
| **Karate** | Automatización de pruebas de API (GET, POST, PUT, DELETE) |
| **k6** | Pruebas de rendimiento y carga sobre endpoints críticos |

### 4.5 Priorización de casos de prueba

Se aplica **priorización basada en riesgo**: los casos de prueba que cubren los riesgos más altos del producto se ejecutan primero. La secuencia natural del flujo crítico determina el orden de ejecución:

1. Registro e inicio de sesión (HDU_1, HDU_2)
2. Creación de curso e inscripción de estudiantes (HDU_3, HDU_5)
3. Definición y gestión del programa evaluativo (HDU_11, HDU_12, HDU_13)
4. Registro de notas y cálculo de promedios (HDU_14)
5. Generación de boletín (HDU_15)

---

## 5. Criterios de Entrada y Salida

### 5.1 Criterios de Entrada (para iniciar la prueba de una HU)

| # | Criterio |
|---|---|
| CE-1 | Los criterios de aceptación de la HU fueron revisados y aceptados por 1 par |
| CE-2 | El incremento de software compila y corre sin errores |
| CE-3 | Las pruebas unitarias del DEV pasan con cobertura mínima del 80% sobre el código nuevo |
| CE-4 | El endpoint o funcionalidad está disponible en el entorno de pruebas |
| CE-5 | Los datos de prueba necesarios están preparados o son generables |

### 5.2 Criterios de Salida (para dar una HU por probada)

| # | Criterio |
|---|---|
| CS-1 | Todos los escenarios Gherkin (positivos y negativos) de la HU fueron ejecutados |
| CS-2 | No existen defectos de severidad **Crítica** o **Alta** abiertos para la HU |
| CS-3 | Los defectos encontrados están documentados en informes de defecto |
| CS-4 | Las pruebas de regresión sobre funcionalidades previas pasan exitosamente |
| CS-5 | La evidencia de ejecución de pruebas está registrada (capturas, logs o reportes) |

---

## 6. Entorno de Pruebas

| Aspecto | Descripción |
|---|---|
| **Tipo de entorno** | Local (la aplicación está diseñada como una app de ejecución local, no web) |
| **Configuración** | Máquina local con el sistema construido y corriendo |
| **Datos de prueba** | Generados manualmente al inicio de cada sesión de prueba; se documentan en los casos de prueba |
| **Gestión de versiones** | Repositorio Git compartido entre DEV y QA |
| **Restauración** | Antes de cada ciclo de prueba se parte de un estado limpio de datos |

---

## 7. Herramientas

| Herramienta | Propósito específico |
|---|---|
| **SerenityBDD + Cucumber** | Diseño y ejecución de pruebas funcionales BDD con reportes detallados |
| **Karate** | Pruebas automatizadas de API REST (4 escenarios: GET, POST, PUT, DELETE) contra API pública |
| **k6** | Pruebas de rendimiento: medir tiempos de respuesta y estabilidad bajo carga |
| **GitHub Projects** | Tablero ágil para gestión del sprint, seguimiento de tareas y sub-tareas de prueba |
| **GitHub Issues** | Registro y seguimiento de defectos encontrados |
| **Markdown (documentos .md)** | Documentación de plan de pruebas, casos de prueba y reportes |

---

## 8. Roles y Responsabilidades

### 8.1 QA — Sebastián Stelmaj

| Responsabilidad | Actividades |
|---|---|
| **Planificación de pruebas** | Redactar TEST_PLAN.md y TEST_CASES.md |
| **Diseño de casos de prueba** | Crear escenarios Gherkin por cada HU del MVP |
| **Ejecución de pruebas** | Ejecutar los casos de prueba funcionales (manuales y automatizados) |
| **Gestión de defectos** | Documentar defectos encontrados con evidencia y severidad |
| **Automatización API** | Implementar los 4 escenarios de Karate contra API pública |
| **Pruebas de rendimiento** | Configurar y ejecutar escenarios de k6 |
| **Reportes** | Generar informes de avance y compleción de la prueba |
| **Seguimiento en board** | Registrar casos de prueba como sub-tareas en GitHub Projects |

### 8.2 DEV — Nicolas Vargas

| Responsabilidad | Actividades |
|---|---|
| **Pruebas unitarias** | Escribir y mantener pruebas unitarias con cobertura ≥ 80% |
| **Corrección de defectos** | Resolver los defectos reportados por QA según prioridad |
| **Soporte al entorno** | Asegurar que el entorno de pruebas esté disponible y funcional |
| **Prueba de integración** | Verificar la correcta comunicación entre componentes |
| **Revisión** | Participar en la revisión de criterios de aceptación como par |

---

## 9. Cronograma y Estimación

### 9.1 Estructura de micro-sprints

| Micro-Sprint | Foco QA | Foco DEV |
|---|---|---|
| **MS-1** (Día 1–2) | Redacción TEST_PLAN + TEST_CASES (HDU_1, HDU_2, HDU_3, HDU_5). Setup Karate (escenario GET) | Implementación HDU_1, HDU_2, HDU_3, HDU_5 |
| **MS-2** (Día 3–4) | TEST_CASES (HDU_11–15). Escenarios Karate (POST, PUT, DELETE). Ejecución de pruebas sobre incremento del DEV | Implementación HDU_11, HDU_12, HDU_13, HDU_14, HDU_15 |
| **Cierre** (Día 5) | REALITY_CHECK.md. Revisión final del board. Últimas pruebas de regresión | Corrección de defectos pendientes. REALITY_CHECK.md conjunto |

### 9.2 Estimación de esfuerzo QA por HU

| HU | Story Points | Esfuerzo QA estimado (horas) | Micro-Sprint |
|---|---|---|---|
| HDU_1 — Registrar usuarios | _TBD_ | _TBD_ | MS-1 |
| HDU_2 — Iniciar sesión | _TBD_ | _TBD_ | MS-1 |
| HDU_3 — Crear curso | _TBD_ | _TBD_ | MS-1 |
| HDU_5 — Agregar estudiantes | _TBD_ | _TBD_ | MS-1 |
| HDU_11 — Definir programa | _TBD_ | _TBD_ | MS-2 |
| HDU_12 — Eliminar instancia evaluatoria | _TBD_ | _TBD_ | MS-2 |
| HDU_13 — Actualizar instancia evaluatoria | _TBD_ | _TBD_ | MS-2 |
| HDU_14 — Registrar notas | _TBD_ | _TBD_ | MS-2 |
| HDU_15 — Generar boletín | _TBD_ | _TBD_ | MS-2 |

> **Nota:** Los Story Points y las horas estimadas se completarán tras la sesión de planificación conjunta.

---

## 10. Entregables de Prueba

| Entregable | Descripción | Responsable |
|---|---|---|
| `TEST_PLAN.md` | Este documento: plan de pruebas formal | QA |
| `TEST_CASES.md` | Matriz de casos de prueba con escenarios Gherkin por cada HU del MVP | QA |
| `REALITY_CHECK.md` | Análisis retrospectivo: estimación vs. realidad, valor del MVP y lecciones aprendidas | QA + DEV |
| **Repositorio Karate** | Proyecto con 4 escenarios API automatizados + README con instrucciones de ejecución | QA |
| **Sub-tareas en GitHub Projects** | Cada caso de prueba registrado como sub-tarea dentro de su HU correspondiente | QA |
| **Informes de defectos** | Reportes detallados de cada defecto encontrado (ID, severidad, pasos, evidencia) | QA |
| **Reportes de ejecución** | Evidencia de las pruebas ejecutadas (SerenityBDD reports, logs de Karate, resultados de k6) | QA |

---

## 11. Riesgos y Contingencias

### 11.1 Riesgos de Producto

| ID | Riesgo | Probabilidad | Impacto | Nivel | Mitigación |
|---|---|---|---|---|---|
| RP-1 | Cálculo incorrecto de promedios ponderados cuando existen notas nulas | Alta | Alto | **Crítico** | Casos de prueba específicos con notas vacías tratadas como 0. Validar fórmula con datos conocidos |
| RP-2 | La suma de ponderaciones del programa no se bloquea correctamente cuando ≠ 100% | Media | Alto | **Alto** | Pruebas de valor frontera: 99.9%, 100%, 100.1%. Tabla de decisión con combinaciones |
| RP-3 | Duplicidad de estudiantes en un mismo curso (ID repetido) | Media | Medio | **Medio** | Validar constraint de unicidad. Caso de prueba con ID existente |
| RP-4 | Boletín exportado con datos incompletos o corruptos | Media | Alto | **Alto** | Escenario de prueba con notas vacías + advertencia del sistema. Verificar integridad del archivo generado |
| RP-5 | Contraseñas almacenadas en texto plano | Baja | Alto | **Alto** | Verificación directa en base de datos de que el hash existe |
| RP-6 | Mensaje de error de login revela información específica (ej: "usuario no existe") | Media | Medio | **Medio** | Validar que el mensaje sea genérico: "Credenciales inválidas" |

### 11.2 Riesgos de Proyecto

| ID | Riesgo | Probabilidad | Impacto | Nivel | Mitigación |
|---|---|---|---|---|---|
| RY-1 | El DEV no completa todas las HU del MVP en los micro-sprints | Alta | Alto | **Crítico** | Priorizar pruebas sobre lo entregado. El QA avanza con documentación y Karate independientemente |
| RY-2 | Tiempo insuficiente para ejecutar todos los casos de prueba diseñados | Media | Medio | **Medio** | Priorización basada en riesgo: ejecutar primero los escenarios críticos |
| RY-3 | Entorno de pruebas inestable o no disponible | Media | Alto | **Alto** | QA avanza con pruebas de API contra `automationexercise.com` (Karate). Documentación no depende del entorno |
| RY-4 | Desviación significativa entre Story Points estimados y esfuerzo real | Alta | Medio | **Alto** | Registrar tiempo real invertido por tarea. Este desfase se analiza en REALITY_CHECK.md |
| RY-5 | Falta de comunicación entre DEV y QA sobre el estado de las HU | Baja | Medio | **Medio** | Sincronización diaria breve (standup informal). Uso del tablero de GitHub Projects como fuente de verdad |

---

> **Aprobación del plan**
>
> | Rol | Nombre | Fecha |
> |---|---|---|
> | QA | Sebastián Stelmaj | 25/03/2026 |
> | DEV | Nicolas Vargas | _Pendiente_ |
