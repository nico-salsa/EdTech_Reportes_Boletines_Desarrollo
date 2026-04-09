## Context

EdTech ya contiene una implementacion funcional del MVP local para docentes, pero la base de ingenieria esta fragmentada entre codigo operativo, artefactos OpenSpec archivados, documentos desactualizados y un backend que actualmente no compila. El problema principal no es la ausencia total de producto, sino la falta de una linea base confiable donde la funcionalidad existente, la documentacion y los controles de calidad apunten a la misma realidad.

El cambio es transversal porque toca varios niveles al mismo tiempo: especificacion activa, compilacion local, documentacion tecnica, contratos funcionales, calidad, trazabilidad y preparacion para cambios futuros. Tambien afecta tanto backend como frontend porque la meta es dejar el MVP completamente funcional en local sin alterar el alcance de negocio ya implementado.

## Goals / Non-Goals

**Goals:**
- Publicar en `openspec/specs/` las capacidades actuales del MVP y usarlas como catalogo activo de verdad.
- Recuperar una linea base local estable donde backend y frontend compilen y el flujo principal pueda ejecutarse de punta a punta.
- Alinear documentacion, arquitectura, workflow IA, trazabilidad, pruebas y criterios operativos con lo que el repositorio realmente contiene.
- Separar los artefactos por capacidades pequenas para reducir ambiguedad y facilitar implementacion incremental.
- Preparar un backlog implementable por bloques pequenos, con cambios compatibles con commits atomicos.

**Non-Goals:**
- Cambiar el alcance funcional del MVP mas alla de lo necesario para estabilizarlo y documentarlo.
- Introducir autenticacion avanzada, nuevos roles, nuevas historias fuera del MVP o una arquitectura distribuida.
- Reescribir el producto completo con otro stack o redisenar la UX desde cero.
- Convertir esta iteracion en un esfuerzo de endurecimiento enterprise; el objetivo es una base ASDD realista para el alcance local del proyecto.

## Decisions

### 1. Mantener el MVP funcional y realinear la estructura alrededor de lo construido

**Decision:** Preservar los flujos actuales de autenticacion, cursos, estudiantes, programa evaluativo, notas y exportacion; la intervencion se enfocara en corregir, completar y documentar.

**Rationale:**
- El repositorio ya contiene valor funcional y reglas de negocio implementadas.
- Reescribir sin aprovechar lo existente aumentaria riesgo y difumina la trazabilidad de la auditoria.
- La meta del usuario es "misma funcionalidad, completamente funcional localmente", no un producto nuevo.

**Alternativas consideradas:**
- Reescribir backend y frontend desde cero: descartado por costo y perdida de trazabilidad.
- Documentar sin corregir la base tecnica: descartado porque mantendria un repositorio no ejecutable.

### 2. Convertir las specs archivadas en catalogo activo y complementarlas con capacidades de gobierno

**Decision:** Crear specs activas para las cinco capacidades funcionales ya presentes en el MVP y anadir tres capacidades de soporte: runtime local, gobierno de ingenieria y quality gates.

**Rationale:**
- Las specs archivadas ya contienen una base valida del dominio y reducen riesgo de inventar comportamiento.
- Separar funcionalidad y gobierno permite tareas mas pequenas y menos ambiguas.
- El catalogo activo debe cubrir tanto el "que hace el sistema" como "como se controla y entrega".

**Alternativas consideradas:**
- Una sola spec gigante para todo el repositorio: descartada por exceso de contexto y baja mantenibilidad.
- Solo mover las cinco specs funcionales sin gobierno: descartado porque no resolveria la brecha ASDD detectada.

### 3. Estabilizar primero la linea base local antes de ampliar automatizacion

**Decision:** Priorizar compilacion backend, build frontend y coherencia de ejecucion local antes de exigir automatizacion mas amplia.

**Rationale:**
- No existe calidad gobernada si el repositorio no puede compilarse de forma consistente.
- La automatizacion debe validar una base que ya sea ejecutable.

**Alternativas consideradas:**
- Crear CI antes de corregir el runtime: descartado porque solo formalizaria fallos actuales.

### 4. Documentar la realidad del sistema y no el estado idealizado

**Decision:** Los nuevos artefactos tecnicos describiran el estado real y las correcciones necesarias, incluyendo deuda tecnica, decisiones vigentes y evidencia de prueba.

**Rationale:**
- Parte de la documentacion actual habla de un cambio "activo" que ya esta archivado.
- ASDD exige trazabilidad y control sobre hechos verificables, no sobre intenciones historicas.

**Alternativas consideradas:**
- Mantener narrativa previa y anexar notas menores: descartado porque deja dos fuentes contradictorias.

### 5. Reducir deriva arquitectonica sin forzar una reescritura profunda en esta fase

**Decision:** Limpiar inconsistencias criticas y documentar la direccion arquitectonica, pero evitar una refactorizacion total del modelo de persistencia si no es imprescindible para dejar el MVP estable.

**Rationale:**
- Hoy coexisten esqueletos JPA y servicios en `JdbcTemplate`; esa deriva debe explicarse y acotarse.
- El foco inmediato es estabilidad, claridad y trazabilidad.

**Alternativas consideradas:**
- Migrar toda la persistencia a JPA ahora: descartado por alcance.
- Ignorar la deriva: descartado porque perpetua confusion en la arquitectura.

### 6. Crear tareas por bloques pequenos compatibles con commits atomicos

**Decision:** Partir el trabajo en bloques implementables de bajo riesgo: runtime, especificacion activa, documentacion de gobierno, quality gates y cierre de trazabilidad.

**Rationale:**
- El usuario pidio evitar cambios demasiado grandes por commit.
- Esta descomposicion reduce alucinacion y facilita validar cada bloque.

## Risks / Trade-offs

- [Corregir compilacion puede destapar fallos ocultos] -> Mitigacion: validar backend y frontend por separado antes de ampliar cambios documentales.
- [Crear muchas specs puede fragmentar demasiado el contexto] -> Mitigacion: separar solo por capacidades de negocio y gobierno claramente distinguibles.
- [La documentacion nueva puede quedar por delante de la implementacion] -> Mitigacion: usar lenguaje que refleje estado actual, estado objetivo inmediato y evidencia concreta.
- [La deriva entre codigo legado y arquitectura objetivo puede persistir temporalmente] -> Mitigacion: registrar deuda tecnica y atacarla solo donde bloquee operacion local o trazabilidad.
- [El alcance ASDD puede crecer mas que el MVP] -> Mitigacion: mantener como restriccion no agregar nuevas historias funcionales fuera del flujo ya construido.

## Migration Plan

1. Crear el catalogo activo del cambio: proposal, design, specs y tasks.
2. Corregir la base tecnica minima para que backend y frontend puedan compilar y ejecutarse localmente.
3. Actualizar y crear documentos de gobierno segun la realidad del repositorio.
4. Incorporar quality gates minimos y evidencias de ejecucion.
5. Cerrar la iteracion con trazabilidad entre especificacion, codigo, pruebas y documentacion.

No aplica migracion de produccion. El foco es la estabilizacion y realineacion del repositorio local.

## Open Questions

- Si los repositorios y entidades JPA no usados deben eliminarse en esta misma iteracion o documentarse como deuda inmediata.
- Si el pipeline CI se dejara solo para build/test/spec validation o tambien incluira chequeos de formato/lint desde esta fase.
- Si los artefactos historicos `PRD.md`, `SUBTASKS.md` y `USER_STORIES.md` deben mantenerse fuera del control remoto mediante limpieza de historial futura o solo dejar de ser fuente operativa vigente.
