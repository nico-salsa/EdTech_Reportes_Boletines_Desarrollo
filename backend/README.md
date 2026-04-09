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
.\gradlew.bat bootRun
```

La configuracion local usa SQLite en `data/edtech.db` dentro de `backend/` y expone la API en `http://localhost:8080/api`.

## Validacion minima

Desde `backend/`:

```bash
.\gradlew.bat test
```

Esta validacion deja la linea base de compilacion del backend en verde.

## Referencias

- Guia de ejecucion completa: `docs/EJECUCION_LOCAL.md`
- Cambio OpenSpec activo: `openspec/changes/asdd-local-functional-realignment/`
