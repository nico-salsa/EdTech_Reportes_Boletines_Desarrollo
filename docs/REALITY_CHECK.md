# Reality Check

## Objetivo

Registrar la diferencia entre la situacion esperada del repositorio y la realidad verificada durante la realineacion ASDD.

## Lo que se esperaba

- Backend compilando y ejecutando localmente
- Frontend construyendo sin problemas
- Especificacion activa disponible en OpenSpec
- Documentacion operativa actualizada
- Evidencia de calidad minima para sostener el MVP

## Lo que se encontro

- El backend no compilaba por un metodo duplicado en `ApiExceptionHandler`.
- El frontend si construia correctamente.
- Las specs existian solo en un cambio archivado, no como catalogo activo.
- El README y otros documentos apuntaban a un cambio OpenSpec obsoleto.
- No existian artefactos persistentes de workflow IA, arquitectura viva, trazabilidad o NFRs.

## Correcciones realizadas en esta iteracion

- Se recupero la compilacion base del backend.
- Se confirmo el build del frontend.
- Se creo el cambio `asdd-local-functional-realignment` con proposal, design, specs y tasks.
- Se actualizaron las guias operativas base del repositorio.
- Se creo el paquete minimo de gobierno tecnico y trazabilidad.
- Se agrego una suite automatizada de backend con cobertura de lineas >= `80%` y un workflow CI versionado.
- Se dejo un workflow de integracion por PR contra `main`, separado por jobs para OpenSpec, frontend y backend.
- El gate de backend ahora usa `build` para verificar no solo pruebas y cobertura, sino tambien la construccion real del artefacto Gradle.
- Se elimino del backend el andamiaje JPA y los modulos vacios que no participaban del runtime real.

## Brechas que siguen abiertas

- La automatizacion sigue concentrada en backend; aun faltan pruebas automatizadas del frontend.
- Aun falta profundizar la limpieza documental de referencias historicas y publicar un contrato API formal.
- Falta cerrar la documentacion de entrega final y ampliar la evidencia automatizada hacia contratos y NFRs.


## 1. ¿Qué tareas subestimamos y por qué?

- **Problemas de entorno/locale:** El formateo de promedios en el boletín HTML dependía del locale de la JVM. En entornos con locale `es_UY/es_ES` los decimales usaban coma en vez de punto (BUG-002). Se subestimó la variabilidad de entorno.
- **Ambos bugs fueron detectados por QA**, no en desarrollo. Esto evidencia que faltó cobertura de edge cases en la fase de codificación.
- **Esfuerzo de pruebas para la API:** Se subestimó el esfuerzo necesario para realizar las pruebas automatizadas con KARATE de forma que cubran robustamente la API del MVP 

---

## 2. ¿El MVP es realmente valioso para el negocio?

**Sí.** El flujo crítico completo funciona: registro → login → crear curso → inscribir estudiantes → definir programa evaluativo → registrar notas → calcular promedios → exportar boletín (PDF/HTML/JSON). Los 2 bugs encontrados son de severidad media y tienen solución propuesta documentada, no bloquean el uso del MVP.

---

## 3. ¿Cómo garantizó el QA la calidad del MVP? 

QA aplicó una **estrategia en pirámide de tres niveles** ejecutada en tiempo récord:

| Nivel | Herramienta | Cantidad | Tasa de éxito |
|-------|-------------|:--------:|:--------------:|
| **Unitarias** | JUnit + Vitest | 155 tests (111 back + 44 front) | 98.7% |
| **Integración API** | Karate DSL | 40 casos sobre 12 endpoints | 100% |
| **E2E flujos críticos** | SerenityBDD + Cucumber | 4 escenarios completos | 100% |

**Prácticas clave que garantizaron la calidad:**

1. **Tests como documentación viva:** Los 2 tests fallidos son **intencionales** — validan el comportamiento esperado, no el actual. Cuando se corrijan BUG-001 y BUG-002, los tests pasarán sin modificación.
2. **Cobertura transversal de seguridad:** Caso API-SEC-001 verifica acceso sin token en todos los endpoints protegidos.
3. **Validación de edge cases críticos:** notas vacías tratadas como 0, caracteres especiales escapados contra XSS, formatos no soportados, campos nulos/vacíos en cada endpoint.
4. **Trazabilidad completa:** Cada caso de prueba está mapeado a su Historia de Usuario (HDU), permitiendo verificar cobertura funcional del MVP.
5. **Bugs documentados con causa raíz y fix propuesto:** QA no solo encontró los defectos, sino que entregó la solución de código exacta para cada uno, reduciendo el tiempo de corrección a minutos.

> **Conclusión:** El MVP se entrega con confianza funcional respaldada por 155 pruebas unitarias, 40 casos de integración y 4 flujos E2E, con solo 2 defectos medios documentados y con solución lista.

