# AI Workflow

## Objetivo

Este repositorio usa un flujo AI-native guiado por OpenSpec, pero la responsabilidad final de calidad, alcance y aprobacion sigue siendo humana. La IA puede proponer, implementar, documentar y validar; no reemplaza la decision final sobre que entra al repositorio.

## Fuente de verdad

El orden de autoridad para cambios nuevos o refactorizaciones relevantes es:

1. `openspec/changes/<change>/proposal.md`
2. `openspec/changes/<change>/design.md`
3. `openspec/changes/<change>/specs/**/spec.md`
4. `openspec/changes/<change>/tasks.md`
5. Codigo y pruebas del repositorio
6. Documentacion tecnica persistente en `docs/`

Los documentos historicos como `PRD.md`, `SUBTASKS.md` y `USER_STORIES.md` sirven como contexto, pero no deben reemplazar los artefactos activos del cambio.

## Flujo operativo

1. Explorar el repositorio y confirmar la realidad del sistema.
2. Crear o actualizar un cambio OpenSpec.
3. Separar el alcance en specs pequenas por capacidad.
4. Implementar por bloques pequenos y verificables.
5. Ejecutar validaciones minimas antes de integrar.
6. Registrar evidencia, riesgos residuales y desviaciones.

## Reglas de colaboracion con IA

- No asumir comportamiento no evidenciado en codigo o specs.
- No marcar una tarea como hecha sin evidencia tecnica o documental.
- Preferir cambios pequenos y commits atomicos.
- Si la implementacion descubre una contradiccion en specs o design, primero se corrige el artefacto fuente.
- Si hay divergencia entre codigo y documento, gana la realidad verificada y se documenta el ajuste.

## Validaciones minimas actuales

- Backend: `cd backend; .\gradlew.bat test`
- Frontend: `pnpm --dir frontend build`
- OpenSpec: `openspec validate <change>`

## Responsabilidad humana final

Antes de merge o cierre de cambio, una persona debe revisar:

- alcance implementado vs alcance especificado
- riesgos abiertos
- evidencia de validacion
- impacto de arquitectura y deuda tecnica

