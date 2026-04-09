# NFRs

## Portabilidad

- Objetivo minimo: ejecutar frontend y backend localmente sin infraestructura externa.
- Evidencia actual: frontend build exitoso; backend compilacion exitosa y respuesta HTTP local en `8080`.

## Seguridad basica

- Objetivo minimo: no almacenar contrasenas en texto plano y proteger rutas privadas.
- Evidencia actual: uso de `BCryptPasswordEncoder` y `X-Session-Token` para vistas protegidas.
- Riesgo abierto: no hay endurecimiento adicional, expiracion avanzada de sesion ni controles enterprise.

## Observabilidad

- Objetivo minimo: errores HTTP consistentes y comportamiento verificable en runtime local.
- Evidencia actual: `ApiExceptionHandler` centraliza respuestas de error.
- Riesgo abierto: no hay logging estructurado, metricas ni trazas.

## Mantenibilidad

- Objetivo minimo: separar frontend, backend, specs y docs con una fuente de verdad activa.
- Evidencia actual: cambio OpenSpec activo y artefactos de arquitectura/gobierno creados.
- Riesgo abierto: aun falta ampliar la cobertura automatizada y publicar contratos API formales.

## Rendimiento

- Objetivo minimo: no degradar el flujo local del MVP.
- Evidencia actual: no hay pruebas de carga ejecutadas en esta iteracion.
- Riesgo abierto: NFR de rendimiento aun no medido formalmente.
