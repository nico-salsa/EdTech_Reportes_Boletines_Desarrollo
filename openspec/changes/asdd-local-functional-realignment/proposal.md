## Why

El repositorio ya implementa el flujo funcional principal del MVP, pero hoy no puede sostener una disciplina ASDD verificable: no hay specs activas en `openspec/specs/`, el backend no compila, faltan artefactos de gobierno y calidad, y parte de la documentacion operativa esta desalineada con el estado real del proyecto. Este cambio es necesario ahora para conservar la funcionalidad existente, dejar el sistema completamente operativo en local y convertir la especificacion en la fuente activa de verdad para futuras iteraciones.

## What Changes

- Reorganizar la base OpenSpec para que las capacidades funcionales actuales del MVP queden publicadas como specs activas y no solo como artefactos archivados.
- Corregir la base tecnica local para que frontend y backend puedan compilar, ejecutarse y validarse en una instalacion local reproducible.
- Documentar el estado real del sistema con artefactos de arquitectura, workflow IA, trazabilidad, plan de pruebas, casos de prueba, reality check y criterios operativos de merge.
- Alinear contratos funcionales y tecnicos con lo que ya existe en autenticacion, cursos, estudiantes, programa evaluativo, libro de calificaciones y exportacion de boletines.
- Incorporar lineamientos minimos de calidad para pruebas, CI local/remota, NFRs y versionamiento sin cambiar el alcance funcional del MVP.
- **BREAKING** Reemplazar referencias documentales obsoletas al cambio archivado y al flujo anterior por una estructura vigente centrada en specs activas y artefactos actuales del repositorio.

## Capabilities

### New Capabilities
- `user-auth`: contrato activo para registro, login, sesion local y proteccion de vistas del docente.
- `course-management`: contrato activo para creacion de cursos, consulta de detalle e inscripcion de estudiantes.
- `evaluation-program`: contrato activo para definir, editar y eliminar instancias evaluativas con suma exacta de ponderaciones.
- `gradebook`: contrato activo para registrar notas, preservar notas vacias y recalcular promedios.
- `student-report-export`: contrato activo para exportar boletines individuales en PDF, HTML y JSON con advertencias por informacion incompleta.
- `local-runtime-baseline`: capacidad operativa para compilar, ejecutar y validar localmente frontend, backend, base de datos y configuracion del MVP.
- `engineering-governance`: capacidad documental y de proceso para mantener arquitectura, workflow IA, trazabilidad, reality check, NFRs y checklist de entrega alineados con ASDD.
- `quality-gates`: capacidad de control para pruebas, automatizacion minima, CI y validaciones requeridas antes de integrar cambios.

### Modified Capabilities

Ninguna. El catalogo activo `openspec/specs/` aun no contiene capacidades publicadas; este cambio crea la base vigente tomando como referencia la funcionalidad ya construida y los artefactos archivados.

## Impact

- Afecta `openspec/`, `docs/`, `README.md`, `backend/` y `frontend/`.
- Formaliza y reemplaza referencias desactualizadas al cambio archivado del MVP.
- Exige correcciones en compilacion, contratos documentales y automatizacion basica para dejar el repositorio operable en local.
- Introduce un catalogo activo de especificaciones, artefactos de gobierno y criterios de calidad que guiaran la implementacion posterior.
