## Why

Los docentes necesitan una herramienta local que reduzca el trabajo manual de registrar estudiantes, definir evaluaciones, calcular promedios y generar boletines individuales sin depender de hojas de calculo dispersas. Este cambio se propone ahora para convertir el alcance del MVP ya priorizado en una base formal de especificacion que permita construir el producto con reglas claras de negocio y exportacion.

## What Changes

- Incorporar autenticacion local simple para registro e inicio de sesion de docentes con `username` y `password`.
- Incorporar gestion basica de cursos por docente, incluyendo creacion de curso con regla de unicidad por docente.
- Incorporar inscripcion de estudiantes a cursos con alta on-the-fly y reutilizacion de estudiantes existentes por identificacion.
- Incorporar definicion y edicion del programa evaluativo del curso, incluyendo creacion, actualizacion y eliminacion de instancias con validacion estricta de ponderaciones.
- Incorporar registro de notas por estudiante e instancia evaluativa con recalculo de promedio general y ponderado.
- Incorporar generacion de boletin individual en `PDF`, `HTML` y `JSON`, con advertencia previa cuando existan notas vacias.
- Formalizar el MVP como aplicacion local con frontend y backend ejecutandose en `localhost`, persistencia local y una base tecnica clara para continuar el desarrollo.

## Capabilities

### New Capabilities
- `user-auth`: registro e inicio de sesion local para docentes con credenciales basicas.
- `course-management`: creacion de cursos por docente e inscripcion de estudiantes con alta on-the-fly.
- `evaluation-program`: definicion, actualizacion y eliminacion de instancias evaluativas con ponderaciones que suman exactamente `100%`.
- `gradebook`: registro de notas por estudiante, tratamiento de notas vacias para calculo y recalculo de promedios.
- `student-report-export`: generacion de boletin individual en `PDF`, `HTML` y `JSON` con advertencias por informacion incompleta.

### Modified Capabilities

Ninguna. No existen capacidades base previas en `openspec/specs` para modificar en este cambio.

## Impact

- Afecta la arquitectura base del producto, incluyendo frontend local, backend local, persistencia SQLite y contratos de API.
- Introduce reglas de negocio centrales para autenticacion, cursos, estudiantes, programa evaluativo, notas y exportacion.
- Define el alcance tecnico y funcional minimo que debe quedar resuelto en esta iteracion de desarrollo.
- Requiere crear nuevas specs en OpenSpec para cada capacidad declarada antes de pasar a diseno y tareas.
