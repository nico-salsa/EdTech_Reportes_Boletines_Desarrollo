# Decisiones Tecnicas del MVP

Este documento consolida las decisiones tecnicas y operativas ya acordadas para el MVP de EdTech. Su objetivo es evitar ambiguedades durante la exploracion, especificacion e implementacion.

## Estado actual

- Modo activo: exploracion
- Cambio OpenSpec activo: `local-mvp-docente-boletines`
- Workflow OpenSpec: `spec-driven`

## Alcance funcional confirmado

Historias de usuario incluidas en el MVP:

- `HDU_1`: Registrar usuarios
- `HDU_2`: Iniciar sesion
- `HDU_3`: Crear curso
- `HDU_5`: Agregar estudiantes a un curso
- `HDU_11`: Definir programa del curso
- `HDU_12`: Eliminar instancia de evaluacion del programa
- `HDU_13`: Actualizar instancia de evaluacion del programa
- `HDU_14`: Registrar nota de una instancia de evaluacion
- `HDU_15`: Generar boletin individual

Historias fuera de este MVP:

- `HDU_16` y posteriores, salvo que se reprioricen explicitamente

## Decisiones de producto

- La aplicacion sera local en frontend y backend.
- El frontend se ejecutara en `localhost`.
- La autenticacion sera local y simple.
- El acceso usa solo `username` y `password`.
- No habra recuperacion de contrasena en el MVP.
- No habra roles ni permisos avanzados en el MVP.
- La unicidad de curso aplica por docente.
- El boletin individual debe ser simple, correcto y descargable.
- La exportacion del MVP debe soportar `PDF`, `HTML` y `JSON`.

## Stack tecnico propuesto

### Frontend

- `React`
- `Vite`
- `TypeScript`

Motivo:

- Permite construir una UI local rapida, mantenible y facil de automatizar desde QA.

### Backend

- `Java 21`
- `Spring Boot`
- API REST local

Motivo:

- Se integra bien con validaciones, pruebas automatizadas y flujos de negocio del MVP.

### Persistencia

- `SQLite`
- Acceso con `Spring Data JPA`

Motivo:

- Evita dependencias externas y encaja con el requerimiento de app local.

### Exportacion

- `HTML`: salida renderizable simple
- `JSON`: salida estructurada para integraciones y validaciones
- `PDF`: generado a partir de plantilla HTML con motor gratuito

Motor PDF propuesto:

- `OpenHTMLtoPDF`

Decision asociada:

- Mantener una plantilla HTML base para compartir estructura visual entre `HTML` y `PDF`

## Reglas funcionales que no deben olvidarse

- Las ponderaciones del programa deben sumar exactamente `100%`.
- No se deben permitir nombres vacios en instancias evaluatorias.
- No se deben permitir nombres duplicados en instancias evaluatorias del mismo curso.
- No se deben permitir ponderaciones menores o iguales a `0`.
- Las notas deben ser numericas y mayores o iguales a `0`.
- Las notas vacias se guardan como vacias a nivel de experiencia, pero cuentan como `0` en los calculos.
- Al editar ponderaciones, los promedios deben recalcularse.
- Al generar boletin con notas vacias, el sistema debe advertir antes de exportar.

## Calidad y automatizacion

Herramientas mandatorias:

- `SerenityBDD + Cucumber` para funcional
- `Karate` para API
- `k6` para rendimiento

Implicaciones tecnicas:

- Los flujos criticos deben poder ejecutarse localmente en `localhost`.
- La UI debe tener estados claros para validaciones y mensajes de error.
- Las APIs deben ser predecibles y faciles de consumir en pruebas automatizadas.

## Convenciones de trabajo

- Los commits deben ser atomicos.
- Los mensajes de commit deben estar en espanol.
- El formato de commit debe seguir Conventional Commits.

Ejemplos validos:

- `feat(readme.md): documenta alcance inicial del mvp local`
- `docs(decisiones_tecnicas_mvp.md): consolida acuerdos del producto`
- `chore(gitignore): excluye documentos locales del repositorio`

## Restricciones del repositorio

- `PRD.md` no debe subirse a GitHub.
- `SUBTASKS.md` no debe subirse a GitHub.
- `USER_STORIES.md` no debe subirse a GitHub.

Accion recomendada:

- Mantener estos archivos excluidos del versionado mediante `.gitignore`.

## Siguiente artefacto OpenSpec

Primer artefacto pendiente:

- `proposal` en `openspec/changes/local-mvp-docente-boletines/proposal.md`

Ese artefacto debe formalizar:

- por que se hace el MVP ahora
- que capacidades nuevas introduce
- impacto tecnico esperado
