# Traceability Matrix

| Capacidad | Spec activa | Codigo principal | Evidencia actual |
|---|---|---|---|
| Autenticacion local | `user-auth` | `backend/src/main/java/com/edtech/app/auth/*`, `frontend/src/app/contexts/AuthContext.tsx` | Backend compila; rutas protegidas y token local implementados |
| Gestion de cursos y estudiantes | `course-management` | `backend/src/main/java/com/edtech/app/course/*`, `frontend/src/app/contexts/AppDataContext.tsx` | Endpoints y consumo frontend presentes |
| Programa evaluativo | `evaluation-program` | `backend/src/main/java/com/edtech/app/course/CourseService.java`, `frontend/src/app/components/EditProgramModal.tsx` | Validacion de ponderaciones y edicion de actividades presentes |
| Libro de calificaciones | `gradebook` | `backend/src/main/java/com/edtech/app/course/CourseService.java`, `frontend/src/app/components/GradesTable.tsx` | Registro de notas y calculo local de promedios presentes |
| Exportacion individual | `student-report-export` | `backend/src/main/java/com/edtech/app/report/*`, `frontend/src/app/components/ExportReportModal.tsx` | Formatos `PDF`, `HTML`, `JSON` implementados |
| Runtime local | `local-runtime-baseline` | `backend/build.gradle.kts`, `frontend/package.json`, `docs/EJECUCION_LOCAL.md` | `.\gradlew.bat check` y `pnpm --dir frontend build` exitosos |
| Gobierno de ingenieria | `engineering-governance` | `AI_WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/ADR/*` | Artefactos creados en este cambio |
| Quality gates | `quality-gates` | `.github/workflows/ci.yml`, `backend/build.gradle.kts`, `docs/MERGE_CHECKLIST.md` | CI versionado con OpenSpec, backend + cobertura >= 80% y build frontend |
