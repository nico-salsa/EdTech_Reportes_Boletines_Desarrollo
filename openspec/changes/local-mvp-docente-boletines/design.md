## Context

EdTech se construira como una aplicacion local para docentes con frontend y backend ejecutandose en `localhost`, almacenamiento local en `SQLite` y exportacion de boletines individuales en `PDF`, `HTML` y `JSON`. El alcance del MVP cruza multiples modulos del sistema porque combina autenticacion, gestion de cursos, registro de estudiantes, programa evaluativo, libro de calificaciones y exportacion, por lo que conviene fijar decisiones de arquitectura y organizacion antes de implementar.

El repositorio alojara tanto el frontend como el backend. Para mantener orden y escalabilidad, el frontend debe vivir en una carpeta separada del backend y de la documentacion funcional. Esta decision es especialmente importante porque la respuesta de Figma se traducira despues en una base visual que debe integrarse sin mezclar codigo de UI con la logica del servidor.

## Goals / Non-Goals

**Goals:**
- Definir una estructura de repositorio clara para desarrollo local del MVP.
- Separar responsabilidades entre frontend, backend, documentacion y artefactos OpenSpec.
- Establecer la base tecnica para implementar las historias `HDU_1`, `HDU_2`, `HDU_3`, `HDU_5`, `HDU_11`, `HDU_12`, `HDU_13`, `HDU_14` y `HDU_15`.
- Facilitar automatizacion con `SerenityBDD + Cucumber`, `Karate` y `k6`.
- Reutilizar una plantilla HTML como base comun para salida `HTML` y generacion de `PDF`.

**Non-Goals:**
- Empaquetar la aplicacion como binario de escritorio en esta etapa.
- Implementar portal estudiantil, carga masiva u otras historias fuera del MVP.
- Diseñar una arquitectura distribuida o con servicios separados por despliegue.
- Definir en este documento el detalle de cada endpoint o cada criterio funcional; eso corresponde a las specs.

## Decisions

### 1. Monorepo local con separacion por carpetas

**Decision:** Mantener frontend y backend en el mismo repositorio, pero en carpetas independientes.

**Estructura inicial propuesta:**
- `frontend/`: aplicacion React + Vite + TypeScript
- `backend/`: API local en Java 21 + Spring Boot + SQLite
- `docs/`: decisiones tecnicas y documentacion persistente del proyecto
- `openspec/`: artefactos de especificacion y cambio

**Rationale:**
- Permite una sola fuente de verdad para el MVP.
- Reduce friccion para desarrollo local y trazabilidad del cambio.
- Mantiene el frontend aislado para incorporar luego el resultado de Figma sin contaminar backend ni documentacion.

**Alternativas consideradas:**
- Mezclar frontend y backend en la raiz del repo: descartado por desorden y baja mantenibilidad.
- Separar frontend y backend en repos distintos: descartado en MVP por mayor sobrecosto de coordinacion.

### 2. Frontend web local servido en localhost

**Decision:** El frontend sera una aplicacion web local ejecutada en `localhost`.

**Rationale:**
- Es la opcion mas simple para iterar rapido en MVP.
- Facilita pruebas funcionales automatizadas sobre flujos reales de UI.
- Permite integrar la propuesta de Figma directamente en componentes React.

**Alternativas consideradas:**
- Desktop wrapper desde el inicio: descartado por complejidad adicional no esencial para validar el MVP.

### 3. Backend local con Spring Boot y SQLite

**Decision:** El backend se implementara con `Java 21`, `Spring Boot`, `Spring Data JPA` y `SQLite`.

**Rationale:**
- Encaja bien con validaciones de negocio, automatizacion API y pruebas unitarias.
- SQLite elimina dependencia de infraestructura externa y es adecuada para una app local.
- Spring Boot permite una estructura clara para autenticacion, cursos, estudiantes, programa y notas.

**Alternativas consideradas:**
- Node.js para backend: valido, pero menos alineado con el enfoque ya planteado de pruebas y estructura empresarial.
- Base embebida distinta a SQLite: innecesaria para el nivel de complejidad del MVP.

### 4. Modelo modular por dominios del MVP

**Decision:** Organizar backend por dominios funcionales del cambio.

**Modulos objetivo:**
- `auth`
- `courses`
- `students`
- `evaluation-program`
- `gradebook`
- `reports`

**Rationale:**
- Alinea implementacion con las capacidades definidas en `proposal.md`.
- Reduce acoplamiento y mejora trazabilidad entre historia, spec y codigo.

**Alternativas consideradas:**
- Organizacion puramente por capas tecnicas: descartada como estructura principal porque dificulta leer el negocio del MVP.

### 5. Exportacion unificada desde plantilla HTML

**Decision:** Generar boletin `HTML` y usar esa misma estructura como base del `PDF` con `OpenHTMLtoPDF`; `JSON` se generara desde el mismo modelo de reporte.

**Rationale:**
- Mantiene consistencia visual y funcional entre formatos.
- Reduce duplicacion de logica de presentacion.
- Usa un motor gratuito compatible con el requerimiento del MVP.

**Alternativas consideradas:**
- Crear renderer distinto para cada formato: descartado por duplicacion y mayor costo de mantenimiento.

### 6. Sesion local simple para MVP

**Decision:** Implementar autenticacion local con `username` y `password`, sin roles ni recuperacion de contrasena.

**Rationale:**
- Cubre exactamente el alcance del MVP.
- Minimiza complejidad de seguridad y experiencia sin agregar flujos no priorizados.

**Alternativas consideradas:**
- JWT complejo o soporte multirol: descartado por no aportar valor inmediato al MVP.

### 7. Reglas de negocio persistentes desde el inicio

**Decision:** Tratar como invariantes del sistema estas reglas desde la primera iteracion:
- cursos unicos por docente
- ponderaciones que suman exactamente `100%`
- ponderaciones mayores a `0`
- nombres de instancias no vacios ni duplicados por curso
- notas numericas mayores o iguales a `0`
- notas vacias visibles para UX pero equivalentes a `0` en calculo
- advertencia previa antes de exportar si existen notas vacias

**Rationale:**
- Son reglas nucleares del valor del producto.
- Evitan rehacer modelo, validaciones y pruebas mas adelante.

## Risks / Trade-offs

- [Monorepo con dos stacks] -> Mitigacion: separar `frontend/` y `backend/` desde el inicio y documentar scripts por carpeta.
- [Complejidad en exportacion PDF] -> Mitigacion: partir de una sola plantilla HTML simple y correcta, sin layout avanzado en MVP.
- [Desalineacion entre Figma y estructura real] -> Mitigacion: reservar `frontend/` como destino unico del codigo visual y traducir el diseño a componentes reutilizables.
- [Mayor alcance por cubrir varias historias nucleares] -> Mitigacion: mantener las reglas del MVP cerradas y posponer capacidades fuera del alcance confirmado.
- [Pruebas funcionales inestables por UI cambiante] -> Mitigacion: usar IDs o selectores consistentes desde el inicio y priorizar estados de pantalla claros.

## Migration Plan

- No aplica migracion de produccion en esta etapa porque el sistema aun no existe.
- La implementacion debe comenzar creando la estructura fisica del repo con `frontend/` y `backend/`.
- El backend debe exponer contratos locales estables antes de conectar la UI final.
- Cuando llegue la respuesta de Figma, se aterrizara dentro de `frontend/` respetando la estructura ya fijada.

## Open Questions

- La respuesta exacta de Figma aun no esta disponible; su integracion definira la estructura interna de componentes del frontend.
- Falta definir el mecanismo exacto de sesion local en backend: cookie de sesion tradicional o token simple.
- Falta definir si el backend servira archivos exportados directamente o si el frontend descargara blobs desde endpoints dedicados.
