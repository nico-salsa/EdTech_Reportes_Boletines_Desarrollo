# EdTech

Aplicacion local para docentes orientada a gestionar cursos, estudiantes, programas evaluativos, registro de notas y exportacion de boletines.

## Objetivo del MVP

El MVP busca cubrir el flujo minimo de trabajo de un docente desde el acceso al sistema hasta la generacion de un boletin individual.

Historias incluidas:

- `HDU_1`: Registrar usuario
- `HDU_2`: Iniciar sesion
- `HDU_3`: Crear curso
- `HDU_5`: Agregar estudiantes al curso
- `HDU_11`: Definir programa del curso
- `HDU_12`: Eliminar instancia de evaluacion del programa
- `HDU_13`: Actualizar instancia de evaluacion del programa
- `HDU_14`: Registrar nota por estudiante e instancia
- `HDU_15`: Generar boletin individual

## Propuesta tecnica inicial

- Frontend local en `localhost`: `React`, `Vite` y `TypeScript`
- Backend local en `localhost`: `Java 21`, `Spring Boot`
- Persistencia local: `SQLite`
- Exportacion: `PDF`, `HTML` y `JSON`
- Motor PDF propuesto: generacion desde plantilla HTML con `OpenHTMLtoPDF`
- Seguridad MVP: autenticacion local simple con `username` y `password`

## Lineamientos clave

- La aplicacion es local tanto en frontend como en backend.
- Los cursos se consideran duplicados por docente, no a nivel global.
- El boletin individual del MVP debe ser simple pero correcto.
- Las notas vacias deben distinguirse visualmente, pero para calculo cuentan como `0`.
- La suma de ponderaciones del programa debe ser exactamente `100%`.

## Calidad minima de desarrollo

- Mantener separacion clara entre `frontend/` y `backend/`
- Evitar code smells evidentes y respetar responsabilidades por modulo
- Mantener contratos de API claros y consistentes con la UI
- Validar como minimo el backend con `.\gradlew.bat check` y el frontend con `pnpm --dir frontend build`
- El pipeline de integracion valida `OpenSpec`, el build del frontend y `.\gradlew.bat build` en backend para asegurar que tambien se construya el artefacto Java
- Mantener cobertura automatizada minima del `80%` en lineas del backend; el reporte local queda en `backend/build/reports/jacoco/test/html/index.html`

## Ejecucion local

Requisitos minimos:

- `Java 21`
- `Node.js 20+`
- `pnpm 10+`

### Levantar backend

Desde `backend/`:

```powershell
.\gradlew.bat bootRun
```

Validacion recomendada:

```powershell
.\gradlew.bat check
```

Validacion equivalente al pipeline de integracion:

```powershell
.\gradlew.bat build
```

Detalles:

- API disponible en `http://localhost:8080/api`
- Base de datos local en `backend/data/edtech.db`
- `check` compila, ejecuta pruebas y exige al menos `80%` de cobertura del backend
- `build` hace lo anterior y ademas verifica que el backend se construya correctamente como artefacto Gradle

### Levantar frontend

Desde `frontend/`:

```powershell
pnpm install
pnpm dev
```

Detalles:

- Frontend disponible en `http://localhost:5173`
- Si no defines `VITE_API_BASE_URL`, el cliente usa `http://localhost:8080/api`

### Flujo rapido recomendado

1. Entrar a `backend/` y ejecutar `.\gradlew.bat bootRun`
2. En otra terminal, entrar a `frontend/`
3. Ejecutar `pnpm install`
4. Ejecutar `pnpm dev`
5. Abrir `http://localhost:5173`

## Artefactos de contexto

- Decisiones tecnicas persistentes: `docs/DECISIONES_TECNICAS_MVP.md`
- Cambio OpenSpec activo: `openspec/changes/asdd-local-functional-realignment/`
- Guia de ejecucion local: `docs/EJECUCION_LOCAL.md`

## Notas de repositorio

- `PRD.md`, `SUBTASKS.md` y `USER_STORIES.md` son documentos de apoyo local y no deben publicarse en GitHub.
- Los cambios deben hacerse con commits atomicos y mensajes en espanol usando Conventional Commits.
