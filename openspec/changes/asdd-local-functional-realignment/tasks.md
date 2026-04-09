## 1. Runtime Baseline

- [x] 1.1 Corregir la compilacion del backend y dejar verde la validacion base de Gradle
- [x] 1.2 Confirmar el build del frontend y alinear la configuracion local esperada entre frontend y backend
- [x] 1.3 Verificar la ejecucion local documentada del MVP con base de datos SQLite, puertos y variables reales

## 2. Functional Contract Alignment

- [ ] 2.1 Revisar y ajustar `user-auth` contra la implementacion real de registro, login, sesion y logout
- [ ] 2.2 Revisar y ajustar `course-management` y `evaluation-program` contra los endpoints y validaciones actuales
- [ ] 2.3 Revisar y ajustar `gradebook` y `student-report-export` contra los calculos, advertencias y formatos soportados

## 3. Architecture Cleanup

- [ ] 3.1 Resolver inconsistencias criticas del backend que impiden una linea base estable
- [x] 3.2 Documentar la direccion arquitectonica real y registrar deuda tecnica visible del modelo de persistencia
- [ ] 3.3 Eliminar o dejar explicitamente acotados los artefactos estructurales que hoy generan confusion sin aportar al runtime

## 4. Engineering Governance

- [x] 4.1 Actualizar `README.md`, `backend/README.md` y la guia de ejecucion local para que reflejen el estado actual del repositorio
- [x] 4.2 Crear `AI_WORKFLOW.md`, `docs/ARCHITECTURE.md` y un set minimo de ADRs o decisiones persistentes alineadas con el cambio
- [x] 4.3 Crear `docs/TRACEABILITY_MATRIX.md` y `docs/REALITY_CHECK.md` enlazando alcance, implementacion, evidencias y brechas

## 5. Quality Gates

- [x] 5.1 Incorporar validaciones automatizadas minimas para backend, frontend y artefactos OpenSpec
- [x] 5.2 Crear o actualizar suites de prueba y smoke checks para los flujos criticos del MVP
- [x] 5.3 Crear un workflow CI y checklist de merge compatibles con la linea base local del proyecto

## 6. NFR and Delivery Evidence

- [x] 6.1 Crear `docs/NFRS.md` con criterios y evidencia minima de portabilidad, seguridad basica, observabilidad y mantenibilidad
- [x] 6.2 Registrar la evidencia real de ejecucion de pruebas y riesgos residuales del MVP
- [x] 6.3 Alinear la documentacion de entrega con el estado real del cambio antes del cierre

## 7. Final Validation

- [ ] 7.1 Ejecutar la validacion final del backend, frontend y artefactos OpenSpec
- [ ] 7.2 Revisar que la funcionalidad del MVP se mantenga sin ampliar alcance
- [ ] 7.3 Preparar el cambio para implementacion y cierre incremental con commits atomicos
