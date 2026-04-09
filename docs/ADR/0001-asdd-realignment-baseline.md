# ADR 0001: Realineacion ASDD sobre el MVP existente

## Estado

Aprobado

## Contexto

El repositorio ya contiene el MVP funcional principal, pero la gobernanza de ingenieria no estaba alineada: habia especificacion archivada, documentacion desactualizada, ausencia de artefactos activos y una falla de compilacion en backend.

## Decision

Se decide preservar la funcionalidad actual del MVP y realinear el repositorio alrededor de:

- un cambio OpenSpec activo
- specs separadas por capacidad
- validaciones minimas ejecutables
- documentacion tecnica basada en la realidad actual

## Consecuencias

### Positivas

- Menor riesgo que una reescritura completa
- Mayor trazabilidad entre requerimientos y cambios
- Mejor base para calidad incremental

### Negativas

- Parte de la deuda tecnica heredada seguira visible por varias iteraciones
- La arquitectura no queda completamente homogenea en esta fase

## Alternativas descartadas

- Reescribir backend y frontend desde cero
- Mantener la documentacion previa sin activar specs vivas
- Crear una sola spec monolitica para todo el sistema

