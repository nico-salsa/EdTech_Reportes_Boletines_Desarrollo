# Backend

API local del MVP de EdTech.

## Alcance actual

- Registro e inicio de sesion local de docentes
- Gestion de cursos por docente
- Alta y busqueda de estudiantes
- Programa evaluativo por curso
- Registro de notas por estudiante
- Exportacion individual en `PDF`, `HTML` y `JSON`

## Ejecucion

Desde `backend/`:

```bash
gradle bootRun
```

La configuracion local usa SQLite en `backend/data/edtech.db` y expone la API en `http://localhost:8080/api`.

## Referencias

- Guia de ejecucion completa: `docs/EJECUCION_LOCAL.md`
- Cambio OpenSpec activo: `openspec/changes/local-mvp-docente-boletines/`
