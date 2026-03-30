## 1. Estructura base del backend

- [x] 1.1 Crear el esqueleto inicial de `backend/` con Gradle, Java 21 y Spring Boot
- [ ] 1.2 Configurar SQLite, JPA y la estructura modular base por dominios (`auth`, `courses`, `students`, `evaluation-program`, `gradebook`, `reports`)
- [x] 1.3 Definir configuracion local compartida para ejecucion en `localhost` y manejo de errores de API

## 2. Autenticacion local

- [x] 2.1 Implementar persistencia y modelo de docente con `username` unico y `password` protegido
- [x] 2.2 Implementar endpoint y validaciones de registro segun la spec `user-auth`
- [x] 2.3 Implementar endpoint y flujo de login con sesion local para rutas protegidas
- [x] 2.4 Conectar el frontend del MVP con el registro, login y proteccion de rutas

## 3. Cursos y estudiantes

- [x] 3.1 Implementar modelo y persistencia de cursos con unicidad por docente
- [x] 3.2 Implementar endpoint de creacion de curso y listado para dashboard
- [x] 3.3 Implementar directorio global de estudiantes y busqueda por identificacion
- [x] 3.4 Implementar alta on-the-fly y enrolamiento de estudiantes en cursos con validaciones requeridas
- [x] 3.5 Conectar dashboard, creacion de curso y flujo de agregar estudiante desde el frontend

## 4. Programa evaluativo

- [x] 4.1 Implementar modelo de instancias evaluativas y pesos por curso
- [x] 4.2 Implementar creacion del programa con validaciones de nombres, pesos y suma exacta de `100%`
- [x] 4.3 Implementar actualizacion y eliminacion de instancias preservando la ultima version valida del programa
- [x] 4.4 Conectar la edicion del programa desde el frontend y reflejar el estado validado en detalle de curso

## 5. Libro de calificaciones

- [x] 5.1 Implementar persistencia de notas por estudiante e instancia evaluativa
- [x] 5.2 Implementar validaciones para notas numericas, no negativas y vacias
- [x] 5.3 Implementar recalculo de promedio general y promedio ponderado por estudiante
- [x] 5.4 Actualizar la tabla de notas del frontend para registrar notas y mostrar promedios sincronizados

## 6. Exportacion de boletin individual

- [x] 6.1 Diseñar el modelo de datos comun para exportacion individual del estudiante
- [x] 6.2 Implementar plantilla HTML base del boletin individual y su renderizado en `HTML`
- [x] 6.3 Implementar generacion de `PDF` a partir de la plantilla HTML con `OpenHTMLtoPDF`
- [x] 6.4 Implementar salida `JSON` usando el mismo modelo de reporte
- [x] 6.5 Implementar advertencia previa para exportacion con notas vacias y conectarla al modal del frontend

## 7. Cierre tecnico del MVP

- [x] 7.1 Revisar consistencia de contratos entre backend y frontend del MVP
- [x] 7.2 Documentar scripts de ejecucion local para `frontend/` y `backend/`
- [ ] 7.3 Ejecutar validaciones finales del cambio y preparar el siguiente paso de implementacion con `/opsx:apply`
