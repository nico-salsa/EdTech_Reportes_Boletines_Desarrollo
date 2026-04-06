# Ejecucion Local

## Requisitos

- `Java 21`
- `Gradle 8+`
- `Node.js 20+`
- `pnpm 10+`

## Backend

Ubicacion: `backend/`

Comando principal:

```bash
./gradlew bootRun
```

Detalles:

- Puerto por defecto: `8080`
- Base de datos local: `backend/data/edtech.db` desde la raiz del repo
- Si ejecutas desde `backend/`, la ruta efectiva es `data/edtech.db`
- La API queda disponible en `http://localhost:8080/api`

## Frontend

Ubicacion: `frontend/`

Variables opcionales:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

Comandos principales:

```bash
pnpm install
pnpm dev
```

Detalles:

- Puerto esperado en desarrollo: `5173`
- El frontend consume la API local del backend
- Si no se define `VITE_API_BASE_URL`, el cliente usa `http://localhost:8080/api`

## Flujo recomendado

1. Crear o verificar la carpeta `backend/data/`
2. Entrar a `backend/` y levantar el backend con `./gradlew bootRun`
3. Instalar dependencias del frontend con `pnpm install`
4. Levantar el frontend con `pnpm dev`
5. Abrir la aplicacion en `http://localhost:5173`

## Modulos implementados en esta iteracion

- `auth`: registro, login, sesion y logout
- `course`: cursos, estudiantes, actividades y notas
- `report`: exportacion individual en `PDF`, `HTML` y `JSON`

## Pendientes tecnicos conocidos

- Ejecutar validaciones completas de compilacion en cada nueva iteracion relevante
- Mantener alineados los contratos entre frontend y backend cuando siga el desarrollo
