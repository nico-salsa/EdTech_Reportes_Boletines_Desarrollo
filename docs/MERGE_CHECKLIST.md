# Merge Checklist

- [ ] El cambio OpenSpec activo valida correctamente.
- [ ] `backend` pasa `.\gradlew.bat check`.
- [ ] La cobertura automatizada del backend es >= `80%` y el reporte JaCoCo queda disponible en `backend/build/reports/jacoco/test/html/index.html`.
- [ ] `frontend` pasa `pnpm --dir frontend build`.
- [ ] La documentacion operativa refleja el estado real del repositorio.
- [ ] Los riesgos residuales quedaron documentados en `docs/REALITY_CHECK.md`.
- [ ] Los NFRs minimos quedaron revisados en `docs/NFRS.md`.
- [ ] El cambio mantiene el alcance funcional del MVP sin introducir historias fuera de scope.
